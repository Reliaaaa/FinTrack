package id.fintrack.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.fintrack.app.vm.MainViewModel
import id.fintrack.app.vm.OperationState

internal val LocalOperationState = staticCompositionLocalOf { OperationState() }

@Composable
fun rememberOperation(vm: MainViewModel, key: String, onSaved: () -> Unit): OperationState {
    val state by vm.operations.state(key).collectAsStateWithLifecycle()
    val latestOnSaved by rememberUpdatedState(onSaved)
    LaunchedEffect(state.saved) {
        if (state.saved) {
            latestOnSaved()
            vm.operations.reset(key)
        }
    }
    DisposableEffect(vm, key) {
        onDispose { vm.operations.reset(key) }
    }
    return state
}

/** FormDialog consumes this state without changing the caller's remembered input. */
@Composable
fun OperationForm(vm: MainViewModel, key: String, onSaved: () -> Unit, content: @Composable () -> Unit) {
    val state = rememberOperation(vm, key, onSaved)
    CompositionLocalProvider(LocalOperationState provides state, content = content)
}

@Composable
fun OperationConfirm(vm: MainViewModel, key: String, title: String, text: String,
                     onDismiss: () -> Unit, onConfirm: () -> Unit, label: String = "Hapus") {
    OperationForm(vm, key, onSaved = onDismiss) {
        FormDialog(title, confirmLabel = label, confirmEnabled = true,
            onDismiss = onDismiss, onConfirm = onConfirm) { Text(text) }
    }
}

@Composable
fun OperationButton(vm: MainViewModel, key: String, label: String, onClick: () -> Unit) {
    val state = rememberOperation(vm, key) {}
    TextButton(onClick = onClick, enabled = !state.saving && !state.saved) {
        Text(if (state.saving) "Memproses..." else label)
    }
    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}
