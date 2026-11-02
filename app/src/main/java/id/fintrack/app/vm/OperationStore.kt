package id.fintrack.app.vm

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OperationState(
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

/** UI operations are submitted on the main thread; keys isolate unrelated forms. */
class OperationStore(private val scope: CoroutineScope) {
    private val states = mutableMapOf<String, MutableStateFlow<OperationState>>()
    private fun flow(key: String) = states.getOrPut(key) { MutableStateFlow(OperationState()) }
    fun state(key: String): StateFlow<OperationState> = flow(key)

    fun reset(key: String) {
        val state = flow(key)
        if (!state.value.saving) state.value = OperationState()
    }

    fun submit(key: String, block: suspend () -> Unit) {
        val state = flow(key)
        if (state.value.saving || state.value.saved) return
        state.value = OperationState(saving = true)
        scope.launch {
            try {
                block()
                state.value = OperationState(saved = true)
            } catch (e: CancellationException) {
                state.value = OperationState()
                throw e
            } catch (e: Exception) {
                state.value = OperationState(error = e.message ?: "Gagal menyimpan. Coba lagi.")
            }
        }
    }
}
