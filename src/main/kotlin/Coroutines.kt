import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

/**
 * Run with Configuration -> Edit -> VM Options -> -Dkotlinx.coroutines.debug
 * to see coroutines-numbers using println()
 */
class Coroutines {

    private val currentThread
        get() = "[Thread: ${Thread.currentThread().name}]"

    fun runExamples() {
        runCoroutineWithBlockMainThread()
        println()

        runBlockingWithLaunch()
        println()

        withContextExample()
        println()
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

}