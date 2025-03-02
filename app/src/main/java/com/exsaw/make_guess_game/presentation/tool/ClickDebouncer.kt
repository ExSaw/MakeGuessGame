package com.exsaw.make_guess_game.presentation.tool

/**
 * * Created by Alexander Chudov (RickRip)
 * * usatu.robotics@gmail.com
 * * https://github.com/ExSaw
 */

import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.exsaw.make_guess_game.presentation.core.IDispatchersProvider
import com.exsaw.make_guess_game.presentation.di.CoreQualifiers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val DEFAULT_CLICK_DEBOUNCE_TIME = 100L


fun onDebouncedClick(
    coroutineScope: CoroutineScope,
    debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME,
    isVibrateOnBlockedState: Boolean = true,
    action: (() -> Unit)? = null,
): () -> Unit = {
    action?.let {
        ClickDebouncer.performActionCompose(
            coroutineScope = coroutineScope,
            debounceTime = debounceTime,
            isVibrateOnBlockedState = isVibrateOnBlockedState,
            action = action
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.debouncedClickable(
    debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME,
    isVibrateOnBlockedState: Boolean = true,
    actionOnLongClick: (() -> Unit)? = null,
    action: (() -> Unit)? = null,
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val debouncedActionOnClick = action?.let {
        {
            ClickDebouncer.performActionCompose(
                coroutineScope = coroutineScope,
                debounceTime = debounceTime,
                isVibrateOnBlockedState = isVibrateOnBlockedState,
                action = action
            )
        }
    }
    val debouncedActionOnLongClick = actionOnLongClick?.let {
        {
            ClickDebouncer.performActionCompose(
                coroutineScope = coroutineScope,
                debounceTime = debounceTime,
                isVibrateOnBlockedState = isVibrateOnBlockedState,
                action = actionOnLongClick
            )
        }
    }
    this.combinedClickable(
        enabled = action != null || actionOnLongClick != null,
        onClick = { debouncedActionOnClick?.invoke() },
        onLongClick = { debouncedActionOnLongClick?.invoke() },
    )
}

fun View.setDebounceClickListener(
    debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME,
    isVibrateOnBlockedState: Boolean = true,
    actionOnLongClick: ((View) -> Unit)? = null,
    action: ((View) -> Unit)?
) {
    isClickable = if (action != null || actionOnLongClick != null) {
        if (action != null) setOnClickListener(
            getDebounceClickListener(
                view = this,
                debounceTime = debounceTime,
                isVibrateOnBlockedState = isVibrateOnBlockedState
            ) { action(this) }
        )
        if (actionOnLongClick != null) setOnLongClickListener(
            getDebounceLongClickListener(
                view = this,
                debounceTime = debounceTime
            ) { actionOnLongClick(this) }
        )
        else setOnLongClickListener { true }
        true
    } else {
        setOnClickListener(null)
        false
    }
}

private fun getDebounceClickListener(
    view: View,
    debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME,
    isVibrateOnBlockedState: Boolean,
    action: (() -> Unit)
): View.OnClickListener {
    return View.OnClickListener {
        ClickDebouncer.performAction(
            view = view,
            debounceTime = debounceTime,
            isVibrateOnBlockedState = isVibrateOnBlockedState,
            action = action
        )
    }
}

private fun getDebounceLongClickListener(
    view: View,
    debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME,
    action: (() -> Unit)
): View.OnLongClickListener {
    return View.OnLongClickListener {
        ClickDebouncer.performAction(
            view = view,
            debounceTime = debounceTime,
            isVibrateOnBlockedState = false,
            action = action
        )
        true
    }
}

fun getBlockedStateFlow(): StateFlow<Boolean> = ClickDebouncer.isBlockedState

fun emitClickToDebouncer(debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME) {
    ClickDebouncer.blockInput(true, debounceTime)
}

object ClickDebouncer : KoinComponent {

    private val dispatchers: IDispatchersProvider by inject()
    private val appScope: CoroutineScope by inject(CoreQualifiers.APP_SCOPE.qualifier)
    private val vibrator: Vibrator by inject()

    private var clickJob: Job? = null

    private val _isBlockedState = MutableStateFlow(false)
    val isBlockedState = _isBlockedState.asStateFlow()

    private var debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME

    init {
        appScope.launch(dispatchers.default) {
            _isBlockedState
                .filter { it == true }
                .collectLatest {
                    delay(debounceTime)
                    _isBlockedState.update { false }
                }
        }
    }

    fun performAction(
        view: View,
        debounceTime: Long,
        isVibrateOnBlockedState: Boolean,
        action: (() -> Unit)
    ) {
        when {
            (!isBlockedState.value && (clickJob?.isCompleted == true || clickJob == null)) -> {
                blockInput(true, debounceTime)
                clickJob = view.findViewTreeLifecycleOwner()
                    ?.lifecycleScope
                    ?.launch {
                        action.invoke()
                    }
            }

            isVibrateOnBlockedState -> {
                vibrator.vibrate(
                    duration = 50L,
                    pattern = 0,
                    isCutItself = true
                )
            }
        }
    }

    fun performActionCompose(
        coroutineScope: CoroutineScope,
        debounceTime: Long,
        isVibrateOnBlockedState: Boolean,
        action: (() -> Unit)
    ) {
        when {
            (!isBlockedState.value && (clickJob?.isCompleted == true || clickJob == null)) -> {
                blockInput(true, debounceTime)
                clickJob = coroutineScope.launch { action.invoke() }
            }

            isVibrateOnBlockedState -> {
                vibrator.vibrate(
                    duration = 50L,
                    pattern = 0,
                    isCutItself = true
                )
            }
        }
    }

    fun blockInput(
        isBlock: Boolean,
        debounceTime: Long
    ) {
        ClickDebouncer.debounceTime = debounceTime
        _isBlockedState.update { isBlock }
    }
}