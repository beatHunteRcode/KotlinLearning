import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Run with Configuration -> Edit -> VM Options -> -Dkotlinx.coroutines.debug
 * to see coroutines-numbers using println()
 */
class Coroutines {

    private val currentThread
        get() = "[Thread: ${Thread.currentThread().name}]"

    fun runExamples() {
//        runCoroutineWithBlockMainThread()
//        println()
//
//        runBlockingWithLaunch()
//        println()
//
//        withContextExample()
//        println()

// ____________________________________________________________________________________

//        runSequentialIncrementWithOneCoroutine()
//        println()
//
//        runParallelIncrementWithManyCoroutines()
//        println()
//
//        runParallelIncrementWithManyCoroutinesUsingMutex()
//        println()

// ____________________________________________________________________________________

//        showCoroutineContextInfo()

//        runParentWithChildrenCoroutine()

//        coroutineScope_VS_CoroutineScope()

//        coroutineParentChildRelations()

//        coroutineCancel()
//        withTimeoutExample()
//        cascadeChildrenCancelling()
//        cancelDoesNotWorkWithoutSuspendPoint()
        yieldExample()
    }

    private fun runCoroutineWithBlockMainThread() {
        println("$currentThread Running with block main thread...")
        runBlocking {
            println("$currentThread Starting the async computation")
            val myFirstDeferred = async { slowlyAddNumbers(2, 2) }
            val mySecondDeferred = async { slowlyAddNumbers(4, 4) }
            println("$currentThread Waiting for the deferred value to be available")
            println("$currentThread The first result: ${myFirstDeferred.await()}")
            println("$currentThread The second result: ${mySecondDeferred.await()}")
        }
        println("$currentThread ABOBA")
    }

    private fun runBlockingWithLaunch() {
        println("$currentThread Running \"runBlocking { }\" with \"launch { }\" inside...")
        runBlocking {
            println("$currentThread Starting the async computation")
            launch {
                val myFirstDeferred = async { slowlyAddNumbers(2, 2) }
                val mySecondDeferred = async { slowlyAddNumbers(4, 4) }
                println("$currentThread Waiting for the deferred value to be available")
                println("$currentThread The first result: ${myFirstDeferred.await()}")
                println("$currentThread The second result: ${mySecondDeferred.await()}")
            }
            println("$currentThread ABOBA")
        }
    }

    private fun withContextExample() {
        println("$currentThread Running \"runBlocking { }\" with \"withContext { }\" inside...")
        runBlocking {
            println("$currentThread Starting the async computation")
            withContext(Dispatchers.Default) {
                val myFirstDeferred = async { slowlyAddNumbers(2, 2) }
                val mySecondDeferred = async { slowlyAddNumbers(4, 4) }
                println("$currentThread Waiting for the deferred value to be available")
                println("$currentThread The first result: ${myFirstDeferred.await()}")
                println("$currentThread The second result: ${mySecondDeferred.await()}")
            }
            println("$currentThread ABOBA")
        }
    }

    private suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
        println("$currentThread Waiting a bit before calculating $a + $b")
        delay(1000.milliseconds * a)
        return a + b
    }

    private fun runSequentialIncrementWithOneCoroutine() {
        runBlocking {
            println("Running sequential increment with one coroutine...")
            repeat(3) {
                var a = 0
                launch {
                    repeat(10_000) {
                        a++
                    }
                }
                delay(1.seconds)
                println(a)
            }
        }
    }

    private fun runParallelIncrementWithManyCoroutines() {
        runBlocking {
            println("Running parallel increment with many coroutines...")
            repeat(3) {
                var a = 0
                repeat(10_000) {
                    launch(Dispatchers.Default) {
                        a++
                    }
                }
                delay(1.seconds)
                println(a)
            }
        }
    }

    private fun runParallelIncrementWithManyCoroutinesUsingMutex() {
        runBlocking {
            println("Running parallel increment with many coroutines using mutex...")
            repeat(3) {
                var a = 0
                val mutex = Mutex()
                repeat(10_000) {
                    launch(Dispatchers.Default) {
                        mutex.withLock {
                            (a++)
                        }
                    }
                }
                delay(1.seconds)
                println(a)
            }
        }
    }

    private fun runParentWithChildrenCoroutine() {
        runBlocking { // this: CoroutineScope
            launch { // this: CoroutineScope
                delay(1.seconds)
                launch {
                    delay(250.milliseconds)
                    println("Grandchild done")
                }
                println("Child 1 done!")
            }
            launch {
                delay(500.milliseconds)
                println("Child 2 done!")
            }
            println("Parent done!")
        }
        println("main done!")
    }

    private fun coroutineScope_VS_CoroutineScope() {
        println("-------- USING coroutineScope --------")
        runBlocking {
            coroutineScope {
                println("Reached Start of coroutineScope")
                launch {
                    println("Launched 1")
                    delay(3000)
                    println("Finished 1")
                }
                launch {
                    println("Launched 2")
                    delay(1000)
                    println("Finished 2")
                }
                println("Reached End of coroutineScope")
            }
            println("Reached End of runBlocking")
        }
        println("DONE")

        Thread.sleep(2000)
        println()

        println("-------- USING CoroutineScope() --------")
        runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                println("Reached Start of CoroutineScope()")
                launch {
                    println("Launched 1")
                    delay(3000)
                    println("Finished 1")
                }
                launch {
                    println("Launched 2")
                    delay(1000)
                    println("Finished 2")
                }
                println("Reached End of CoroutineScope()")
            }
            println("Reached End of runBlocking")
        }
        println("DONE")
    }

    private fun showCoroutineContextInfo() {
        runBlocking {
            println("Default Coroutine: $coroutineContext")
        }

        runBlocking(Dispatchers.Default) {
            println("Dispatcher changed: $coroutineContext")
        }

        runBlocking(Dispatchers.Default + CoroutineName("ABOBAtine")) {
            println("Dispatcher + name changed: $coroutineContext")
        }

        runBlocking(Dispatchers.Default + SupervisorJob() + CoroutineName("ABOBAtine")) {
            println("Dispatcher + job + name changed: $coroutineContext")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun coroutineParentChildRelations() {
        runBlocking(CoroutineName("COROUTINE_MAIN")) {
            println("Relations of ${coroutineContext[CoroutineName]?.name} before running launch:")
            println("\tParent: ${coroutineContext.job.parent}")
            println("\tChildren: ${coroutineContext.job.children.toList()}")
            launch(CoroutineName("COROUTINE_LAUNCH")) {
                println("- Relations of ${coroutineContext[CoroutineName]?.name} before running inside launch:")
                println("\t- Parent: ${coroutineContext.job.parent}")
                launch(CoroutineName("COROUTINE_INSIDE_LAUNCH")) {
                    println("-- Relations of ${coroutineContext[CoroutineName]?.name}:")
                    println("\t-- Parent: ${coroutineContext.job.parent}")
                }
                println("- Relations of ${coroutineContext[CoroutineName]?.name} after running inside launch:")
                println("\t- Parent: ${coroutineContext.job.parent}")
                println("\t- Children: ${coroutineContext.job.children.toList()}")
            }
            println("Relations of ${coroutineContext[CoroutineName]?.name} after running launch:")
            println("\tParent: ${coroutineContext.job.parent}")
            println("\tChildren: ${coroutineContext.job.children.toList()}")
        }
    }

    private fun coroutineCancel() = runBlocking {
        val launchedJob = launch {
            println("I'm launched!")
            delay(1000.milliseconds)
            println("I'm done!")
        }
        val asyncDeferred = async {
            println("I'm async")
            delay(1000.milliseconds)
            println("I'm done!")
        }
        delay(200.milliseconds)
        launchedJob.cancel()
        asyncDeferred.cancel()
    }

    private fun withTimeoutExample() = runBlocking {
        val quickResult = withTimeoutOrNull(500.milliseconds) {
            delay(1.seconds)
            return@withTimeoutOrNull "withTimeoutOrNull done"
        }
        println("withTimeoutOrNull with 500 ms and delay 1 sec: $quickResult")

        val slowResult = withTimeoutOrNull(3.seconds) {
            delay(1.seconds)
            return@withTimeoutOrNull "withTimeoutOrNull done"
        }
        println("withTimeoutOrNull with 3 sec and delay 1 sec: $slowResult")

        println("withTimeout with 500 ms and delay 1 sec:")
        withTimeout(500.milliseconds) {
            delay(1.seconds)
            println("withTimeout done")
        }
    }

    private fun cascadeChildrenCancelling() = runBlocking {
        val job = launch {
            println("Parent")
            delay(500.milliseconds)
            launch {
                println("Child 1")
                delay(500.milliseconds)
                launch {
                    println("Grand Child 1")
                    delay(500.milliseconds)
                    launch {
                        println("Grand Grand Child 1")
                    }
                }
            }
            delay(200.milliseconds)
            launch {
                println("Child 2")
                delay(500.milliseconds)
                launch {
                    println("Grand Child 2")
                    delay(500.milliseconds)
                    launch {
                        println("Grand Grand Child 2")
                    }
                }
            }
        }
        delay(800.milliseconds)
        job.cancel()
    }

    private fun cancelDoesNotWorkWithoutSuspendPoint() = runBlocking {
        val job = launch(Dispatchers.Default) {
            repeat(5) {
                println("Count: ${it + 1}; isActive - $isActive")
                val timeNow = System.currentTimeMillis()
                while (System.currentTimeMillis() < timeNow + 1000) {
                    //artificial delay
                }
            }
        }
        delay(2.seconds)
        job.cancel()
    }

    private fun yieldExample() = runBlocking {
        val job = launch(Dispatchers.Default) {
            repeat(5) {
                println("Count: ${it + 1}; isActive - $isActive")
                yield()
                val timeNow = System.currentTimeMillis()
                while (System.currentTimeMillis() < timeNow + 1000) {
                    //artificial delay
                }
            }
        }
        delay(2.seconds)
        job.cancel()
    }

}