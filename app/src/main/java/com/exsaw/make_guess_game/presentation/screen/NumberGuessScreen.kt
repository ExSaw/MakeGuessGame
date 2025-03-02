package com.exsaw.make_guess_game.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exsaw.make_guess_game.presentation.action.NumberGuessAction
import com.exsaw.make_guess_game.presentation.core.IDispatchersProvider
import com.exsaw.make_guess_game.presentation.di.mainModule
import com.exsaw.make_guess_game.presentation.state.NumberGuessUIState
import com.exsaw.make_guess_game.presentation.tool.onDebouncedClick
import com.exsaw.make_guess_game.presentation.ui.theme.MakeGuessGameTheme
import com.exsaw.make_guess_game.presentation.viewmodel.NumberGuessViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds


@Composable
fun NumberGuessScreenRoot(
    modifier: Modifier = Modifier
) {
    //  val viewModel = viewModel<NumberGuessViewModel>() // compose injection
    val viewModel: NumberGuessViewModel = koinViewModel()
    val dispatchers: IDispatchersProvider = koinInject()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    NumberGuessScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

// screen holds UI
@OptIn(FlowPreview::class)
@Composable
fun NumberGuessScreen(
    uiState: State<NumberGuessUIState?>,
    onAction: (NumberGuessAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldState = remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { textFieldState.value }
            .debounce(0.2.seconds)
            .distinctUntilChanged()
            .onEach { text ->
                onAction(
                    NumberGuessAction.OnNumberTextChange(text)
                )
            }
            .launchIn(this)
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        TextField(
            value = textFieldState.value,
            onValueChange = { newText ->
                textFieldState.value = newText
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        val guessText = uiState.value?.guessText
        if (guessText?.isNotBlank() == true) {
            Text(
                text = guessText
            )
        }

        val buttonText = uiState.value?.guessButtonText
        if (buttonText?.isNotBlank() == true) {
            Button(
                onClick = onDebouncedClick(rememberCoroutineScope()) {
                    onAction(NumberGuessAction.OnGuessButtonClick)
                }
            ) {
                Text(
                    text = buttonText
                )
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(
    showBackground = true,
    backgroundColor = 0xEFE,
    showSystemUi = true,
    apiLevel = 33,
    device = "id:pixel_4a",
    fontScale = 1.0f,
)
@Composable
fun NumberGuessPreview(modifier: Modifier = Modifier) {
    KoinApplication(
        application = { modules(mainModule) }
    ) {
        MakeGuessGameTheme {
            NumberGuessScreen(
                uiState = mutableStateOf(
                    NumberGuessUIState(
                        numberText = "100",
                        guessText = "Make guess",
                        guessButtonText = "Make guess",
                        isGuessCorrect = false,
                    )
                ),
                onAction = {},
                modifier = modifier
            )
        }
    }
}