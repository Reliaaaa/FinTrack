import id.fintrack.app.vm.OperationStore
import kotlinx.coroutines.*

fun main() = runBlocking {
    var checks = 0
    fun verify(value: Boolean) { check(value); checks++ }
    val store = OperationStore(this)
    val waiting = CompletableDeferred<Unit>()
    var writes = 0
    store.submit("save") { writes++; waiting.await() }
    store.submit("save") { writes++ }
    verify(store.state("save").value.saving)
    yield()
    verify(writes == 1)
    store.reset("save")
    verify(store.state("save").value.saving)
    store.submit("save") { writes++ }
    yield()
    verify(writes == 1)
    store.submit("other") { }
    yield()
    verify(store.state("other").value.saved && store.state("save").value.saving)
    waiting.complete(Unit)
    yield()
    verify(store.state("save").value.saved)
    store.submit("save") { writes++ }
    yield()
    verify(writes == 1)
    store.reset("save")
    store.submit("save") { throw IllegalStateException("Saldo overflow") }
    yield()
    verify(store.state("save").value.error == "Saldo overflow" && !store.state("save").value.saving)
    store.submit("save") { writes++ }
    yield()
    verify(writes == 2 && store.state("save").value.saved && store.state("save").value.error == null)
    store.reset("save")
    verify(!store.state("save").value.saved)
    val childScope = CoroutineScope(coroutineContext + Job())
    val cancelled = OperationStore(childScope)
    cancelled.submit("cancel") { awaitCancellation() }
    yield()
    childScope.cancel()
    yield()
    verify(!cancelled.state("cancel").value.saving && cancelled.state("cancel").value.error == null)
    println("PASS: $checks operation lifecycle checks")
}
