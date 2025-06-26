import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

        showCoroutineContextInfo()

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

}