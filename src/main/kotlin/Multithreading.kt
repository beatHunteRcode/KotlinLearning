import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
//    simpleThread()
//    daemonThread()
//    daemonThreadWithJoin()
//    runnableExample()

//    raceCondition()
//    raceConditionAtomic()
//    raceConditionMutex()
//    raceConditionSingleThread()

//    deadlock()
//    volatile()

//    concurrentCollectionsAdd()
//    concurrentCollectionsChange()
//    concurrentCollectionsChangeSynchronized()

//    mutex()
//    mutexWithDeadlock()
//    mutexWithOwner()
//    synchronizedWithLock()
//    synchronizedBlockedThreadButMutexNot()
    reentrantReadWriteLock()

//    semaphore()

//    concurrentList()
//    mutableObjectInHashMap()
//    concurrentMap()
//    concurrentSet()
}

fun simpleThread() {
    val thread = Thread {
        for (i in 1..5) {
            println("${Thread.currentThread().id} Value $i")
            Thread.sleep(1000)
        }
    }
    thread.start()
}

fun daemonThread() {
    val thread = Thread {
        for (i in 1..5) {
            println("${Thread.currentThread().id} Value $i")
            Thread.sleep(1000)
        }
    }
    thread.isDaemon = true
    thread.start()
    Thread.sleep(1500)
    println("ABOBA")
}

fun daemonThreadWithJoin() {
    val thread = Thread {
        for (i in 1..5) {
            println("${Thread.currentThread().id} Value $i")
            Thread.sleep(1000)
        }
    }
    thread.isDaemon = true
    thread.start()
    Thread.sleep(1500)
    println("ABOBA")
    thread.join()
}

fun runnableExample() {
    val defaultRunnable = Thread(object : Runnable {
        override fun run() {
            println("defaultRunnable executed")
        }
    })
    defaultRunnable.start()

    val defaultRunnableTrailingLambda = Thread {
        println("defaultRunnable with trailing lambda executed")
    }
    defaultRunnableTrailingLambda.start()

    val customRunnable = Thread(MyRunnable())
    customRunnable.start()
}

class MyRunnable : Runnable {
    override fun run() {
        println("MyRunnable executed")
    }
}

fun raceCondition() = runBlocking {
    var notAtomic = 0
    val incrementNotAtomicBlock = {
        for (i in 1..10_000_000) {
            notAtomic += 1
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val timeNotAtomic = measureTimeMillis {
        val notAtomicJob1 = scope1.launch {
            incrementNotAtomicBlock()
        }
        val notAtomicJob2 = scope2.launch {
            incrementNotAtomicBlock()
        }

        notAtomicJob1.join()
        notAtomicJob2.join()
    }

    println("NotAtomic: $notAtomic \t time: $timeNotAtomic ms")
}

fun raceConditionAtomic() = runBlocking {
    val atomic = AtomicInteger(0)
    val incrementAtomicBlock = {
        for (i in 1..10_000_000) {
            atomic.addAndGet(1)
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val timeAtomic = measureTimeMillis {
        val atomicJob1 = scope1.launch {
            incrementAtomicBlock()
        }
        val atomicJob2 = scope2.launch {
            incrementAtomicBlock()
        }

        atomicJob1.join()
        atomicJob2.join()
    }

    println("Atomic: $atomic \t time: $timeAtomic ms")
}

fun raceConditionMutex() = runBlocking {
    var mutexInt = 0
    val mutex = Mutex()
    val incrementMutexBlock = suspend {
        for (i in 1..10_000_000) {
            mutex.withLock {
                mutexInt += 1
            }
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val timeMutex = measureTimeMillis {
        val mutexJob1 = scope1.launch {
            incrementMutexBlock()
        }
        val mutexJob2 = scope2.launch {
            incrementMutexBlock()
        }

        mutexJob1.join()
        mutexJob2.join()
    }

    println("Mutex: $mutexInt \t time: $timeMutex ms")
}

@OptIn(ExperimentalCoroutinesApi::class)
fun raceConditionSingleThread() = runBlocking {
    var value = 0
    val dispatcher = Dispatchers.IO.limitedParallelism(1)
    val incrementSingleThreadBlock = suspend {
        for (i in 1..10_000_000) {
            withContext(dispatcher) {
                value += 1
            }
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val timeSingleThread = measureTimeMillis {
        val singleThreadJob1 = scope1.launch {
            incrementSingleThreadBlock()
        }
        val singleThreadJob2 = scope2.launch {
            incrementSingleThreadBlock()
        }

        singleThreadJob1.join()
        singleThreadJob2.join()
    }

    println("Single thread: $value \t time: $timeSingleThread ms")
}

fun deadlock() = runBlocking {
    val mutex1 = Mutex()
    val mutex2 = Mutex()

    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)

    val job1 = scope1.launch {
        println("Coroutine 1: Locking mutex1...")
        mutex1.lock()
        println("Coroutine 1: Locked mutex1!")
        delay(100)
        println("Coroutine 1: Locking mutex2...")
        mutex2.lock()
        println("Coroutine 1: Locked mutex2!")

        mutex1.unlock()
        println("Coroutine 1: Unlocked mutex1.")
        mutex2.unlock()
        println("Coroutine 1: Unlocked mutex2.")
    }

    val job2 = scope2.launch {
        println("Coroutine 2: Locking mutex2...")
        mutex2.lock()
        println("Coroutine 2: Locked mutex2!")
        delay(100)
        println("Coroutine 2: Locking mutex1...")
        mutex1.lock()
        println("Coroutine 2: Locked mutex1!")

        mutex1.unlock()
        println("Coroutine 2: Unlocked mutex1.")
        mutex2.unlock()
        println("Coroutine 2: Unlocked mutex2.")
    }

    job1.join()
    job2.join()

    println("END")
}

@Volatile
var volatiledWrong = 0

@Volatile
var volatiledRight = 0
fun volatile() = runBlocking {
    val incrementVolatileWrongBlock = {
        for (i in 1..1000) {
            volatiledWrong += 1
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val timeVolatile = measureTimeMillis {
        val volatileJob1 = scope1.launch {
            incrementVolatileWrongBlock()
        }
        val volatileJob2 = scope2.launch {
            incrementVolatileWrongBlock()
        }

        volatileJob1.join()
        volatileJob2.join()
    }

    println("VolatiledWrong: $volatiledWrong \t time: $timeVolatile ms")

    val incrementVolatileRightBlock = {
        for (i in 1..1000) {
            volatiledRight += 1
        }
    }
    val readVolatileBlock: suspend (str: String) -> Unit = { str: String ->
        for (i in 1..3) {
            println("$str VolatiledRight: $volatiledRight")
            delay(1000)
        }
    }
    val scope3 = CoroutineScope(Dispatchers.Default)
    val readVolatileJob1 = scope1.launch {
        readVolatileBlock("Cor1")
    }
    val readVolatileJob2 = scope2.launch {
        readVolatileBlock("Cor2")
    }
    val incrementVolatileJob = scope3.launch {
        incrementVolatileRightBlock()
    }

    readVolatileJob1.join()
    readVolatileJob2.join()
    incrementVolatileJob.join()
}

fun concurrentCollectionsAdd() = runBlocking {
    val hashMapToAdd = mutableMapOf<Int, String>()
    val concurrentHashMapToAdd = ConcurrentHashMap<Int, String>()

    val addLambda = {
        for (i in 1..1000) {
            hashMapToAdd[i] = "A"
            concurrentHashMapToAdd[i] = "A"
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val job1 = scope1.launch {
        addLambda()
    }
    val job2 = scope2.launch {
        addLambda()
    }

    job1.join()
    job2.join()

    println("Adding elements:")
    println("hashMap size: ${hashMapToAdd.size}")
    println("concurrentHashMap size: ${concurrentHashMapToAdd.size}")
}

fun concurrentCollectionsChange() = runBlocking {
    val hashMapToChange = mutableMapOf("A" to 0)
    val concurrentHashMapToChange = ConcurrentHashMap<String, Int>()
    concurrentHashMapToChange["A"] = 0

    val changeLambda = {
        for (i in 1..1000) {
            val hashMapValue = hashMapToChange["A"] ?: 0
            hashMapToChange["A"] = hashMapValue + 1

            val concurrentHashMapValue = concurrentHashMapToChange["A"] ?: 0
            concurrentHashMapToChange["A"] = concurrentHashMapValue + 1
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val job1 = scope1.launch {
        changeLambda()
    }
    val job2 = scope2.launch {
        changeLambda()
    }

    job1.join()
    job2.join()

    println("Changing elements:")
    println("hashMap: $hashMapToChange")
    println("concurrentHashMap: $concurrentHashMapToChange")
}

fun concurrentCollectionsChangeSynchronized() = runBlocking {
    val hashMapToChange = mutableMapOf("A" to 0)
    val concurrentHashMapToChange = ConcurrentHashMap<String, Int>()
    concurrentHashMapToChange["A"] = 0

    val objectToLock = Any()

    val changeLambda = {
        for (i in 1..1000) {
            synchronized(objectToLock) {
                val hashMapValue = hashMapToChange["A"] ?: 0
                hashMapToChange["A"] = hashMapValue + 1

                val concurrentHashMapValue = concurrentHashMapToChange["A"] ?: 0
                concurrentHashMapToChange["A"] = concurrentHashMapValue + 1
            }
        }
    }
    val scope1 = CoroutineScope(Dispatchers.Default)
    val scope2 = CoroutineScope(Dispatchers.Default)
    val job1 = scope1.launch {
        changeLambda()
    }
    val job2 = scope2.launch {
        changeLambda()
    }

    job1.join()
    job2.join()

    println("Changing elements:")
    println("hashMap: $hashMapToChange")
    println("concurrentHashMap: $concurrentHashMapToChange")
}

fun mutex() = runBlocking {
    val lambda = suspend {
        for (i in 1..10) {
            launch {
                delay(1000)
                println(i)
            }
        }
    }

    val mutex = Mutex()
    val lambdaMutex = suspend {
        for (i in 1..10) {
            launch {
                mutex.withLock {
                    delay(1000)
                    println(i)
                }
            }
        }
    }

    CoroutineScope(Dispatchers.Default).launch {
        lambda()
    }.join()
    delay(500)

    CoroutineScope(Dispatchers.Default).launch {
        lambdaMutex()
    }.join()
}

fun mutexWithDeadlock() = runBlocking {
    val list = listOf(1, 2)
    val mutex = Mutex()
    val lambdaSynchronized = suspend {
        for (i in 1..10) {
            mutex.lock()
            println(i)
            delay(1000)
            list[3] // IndexOutOfBoundsException
            mutex.unlock()
        }
    }

    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        try {
            lambdaSynchronized()
        } catch (ex: Exception) {
            println(ex)
        }
    }.join()
    scope.launch {
        lambdaSynchronized()
    }.join()

    println("END")
}

fun mutexWithOwner() = runBlocking {
    val mutex = Mutex()
    val mutexOwner = "aboba"
    println("Started")
    mutex.withLock(mutexOwner) {
        mutex.withLock(mutexOwner) {
            println("Will never be printed")
        }
    }
}

fun synchronizedWithLock() = runBlocking {
    val lockObj = Any()
    println("Started")
    synchronized(lockObj) {
        println()
        synchronized(lockObj) {
            println("Will be printed")
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun synchronizedBlockedThreadButMutexNot() = runBlocking {
    val lambdaSynchronized = {
        println("1 synchronized job")
        synchronized(this) {
            Thread.sleep(100)
            while (true) {

            }
        }
    }
    val mutex = Mutex()
    val lambdaMutex = suspend {
        println("1 mutex job")
        mutex.withLock {
            delay(100)
            while (true) {

            }
        }
    }

    val synchronizedDispatcher = Dispatchers.Default.limitedParallelism(1)
    val mutexDispatcher = Dispatchers.Default.limitedParallelism(1)

    val scope1 = CoroutineScope(synchronizedDispatcher)
    val scope2 = CoroutineScope(mutexDispatcher)

    val job1Synchronized = scope1.launch {
        lambdaSynchronized()
    }
    val job2Synchronized = scope1.launch {
        println("2 synchronized job")
    }

    val job1Mutex = scope2.launch {
        lambdaMutex()
    }
    val job2Mutex = scope2.launch {
        println("2 mutex job")
    }

    job1Synchronized.join()
    job2Synchronized.join()
    job1Mutex.join()
    job2Mutex.join()
}

fun reentrantReadWriteLock() = runBlocking {
    val map = mutableMapOf<Int, String>(
        1 to "Lupa",
        2 to "Pupa"
    )

    val readWriteLock = ReentrantReadWriteLock()
    val lambdaRead = suspend {
        val jobs = (1..5).map { id ->
            launch(Dispatchers.Default) {
                readWriteLock.readLock().withLock {
                    println("Reading map...: ${map[1]}. Attempt $id")
                    Thread.sleep(1000)
                }
            }
        }
        jobs.joinAll()
    }

    val lambdaWrite = suspend {
        val jobs = (1..5).map { id ->
            launch(Dispatchers.Default) {
                readWriteLock.writeLock().withLock {
                    print("Writing map...: ")
                    val key = Random.nextInt(3, 20)
                    map[key] = "ABOBA"
                    print(map[key])
                    println(". Attempt $id")
                    Thread.sleep(1000)
                }
            }
        }
        jobs.joinAll()
    }

    val lambdaReadWithWrite = suspend {
        val jobs = (1..5).map { id ->
            if (id < 3) {
                launch(Dispatchers.Default) {
                    readWriteLock.readLock().withLock {
                        println("Reading map...: ${map[1]}. Attempt $id")
                        Thread.sleep(1000)
                    }
                }
            }
            if (id == 3) {
                launch(Dispatchers.Default) {
                    readWriteLock.writeLock().withLock {
                        print("Writing map...: ")
                        val key = Random.nextInt(3, 20)
                        map[key] = "ABOBA"
                        print(map[key])
                        println(". Attempt $id")
                        Thread.sleep(1000)
                    }
                }
            }
            if (id > 3) {
                launch(Dispatchers.Default) {
                    readWriteLock.readLock().withLock {
                        println("Reading map...: ${map[1]}. Attempt $id")
                        Thread.sleep(1000)
                    }
                }
            } else {
                launch {  }
            }
        }
        jobs.joinAll()
    }

    CoroutineScope(Dispatchers.Default).launch {
        println("Only Reading using ReentrantReadWriteLock")
        lambdaRead()

        println()
        println("Only Writing using ReentrantReadWriteLock")
        lambdaWrite()

        println()
        println("Read with Write using ReentrantReadWriteLock")
        lambdaReadWithWrite()
    }.join()
}

val semaphore = Semaphore(5)
val activeConnections = AtomicInteger(0)
fun semaphore() = runBlocking {
    val downloadJobs = List(20) { id ->
        launch(Dispatchers.Default) {
            downloadData(connectionId = id + 1)
        }
    }
    downloadJobs.joinAll()
}

suspend fun downloadData(connectionId: Int) {
    semaphore.acquire()
    println("Connection $connectionId acquired: Downloading.... | Active connections: ${activeConnections.incrementAndGet()}")
    delay(Random.nextInt(2000, 5000).toLong())
    semaphore.release()
    println("Connection $connectionId released: Downloaded! | Active connections: ${activeConnections.decrementAndGet()}")
}

fun concurrentList() = runBlocking {
    val list = ArrayList<Int>(listOf(1, 2, 3, 4, 6))
    val cowList = CopyOnWriteArrayList<Int>(listOf(1, 2, 3, 4, 6))
    val scope = CoroutineScope(Dispatchers.Default)
    val listReadJob = scope.launch {
        println("Reading ArrayList... $list")
        try {
            for (item in list) {
                println("Read: $item $list")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }

    val listAddJob = scope.launch {
        delay(2000)
        println("Trying to add element in ArrayList")
        list.add(index = 4, element = 5)
    }

    listAddJob.join()
    listReadJob.join()
    println("______")

    val cowListReadJob = scope.launch {
        println("Reading CopyOnWriteArrayList... $cowList")
        try {
            for (item in cowList) {
                println("Read: $item $cowList")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }
    val cowListAddJob = scope.launch {
        delay(2000)
        println("Trying to add element in CopyOnWriteArrayList")
        cowList.add(index = 4, element = 5)
    }

    cowListAddJob.join()
    cowListReadJob.join()
}

data class Aboba(
    var number: Int
)

fun mutableObjectInHashMap() {
    val map = HashMap<Aboba, Int>()
    val aboba = Aboba(number = 1)
    map[aboba] = 123
    println("Before changing: ${map[aboba]} | $map")
    aboba.number = 2
    println("After changing: ${map[aboba]} | $map")
}

fun concurrentMap() = runBlocking {
    val map = HashMap<Int, String>(mapOf(
        1 to "A",
        2 to "B",
        3 to "C",
        4 to "D",
        6 to "F")
    )
    val concurrentMap = ConcurrentHashMap(mapOf(
        1 to "A",
        2 to "B",
        3 to "C",
        4 to "D",
        6 to "F")
    )

    val scope = CoroutineScope(Dispatchers.Default)
    val mapReadJob = scope.launch {
        println("Reading HashMap... $map")
        try {
            for (item in map) {
                println("Read: $item $map")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }

    val mapPutJob = scope.launch {
        delay(2000)
        println("Trying to put element in HashMap")
        map.put(5, "E")
    }

    mapPutJob.join()
    mapReadJob.join()
    println("______")

    val cowListReadJob = scope.launch {
        println("Reading ConcurrentHashMap... $concurrentMap")
        try {
            for (item in concurrentMap) {
                println("Read: $item $concurrentMap")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }
    val cowListPutJob = scope.launch {
        delay(2000)
        println("Trying to put element in ConcurrentHashMap")
        concurrentMap.put(5, "E")
    }

    cowListPutJob.join()
    cowListReadJob.join()
}

fun concurrentSet() = runBlocking {
    val set = HashSet<Int>(listOf(1, 2, 3, 4, 6))
    val concurrentSet = ConcurrentHashMap.newKeySet<Int>()
    concurrentSet.add(1)
    concurrentSet.add(2)
    concurrentSet.add(3)
    concurrentSet.add(4)
    concurrentSet.add(6)
    val scope = CoroutineScope(Dispatchers.Default)
    val setReadJob = scope.launch {
        println("Reading HashSet... $set")
        try {
            for (item in set) {
                println("Read: $item $set")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }

    val setAddJob = scope.launch {
        delay(2000)
        println("Trying to add element in HashSet")
        set.add(5)
    }

    setAddJob.join()
    setReadJob.join()
    println("______")

    val concurrentSetReadJob = scope.launch {
        println("Reading ConcurrentHashMap.KeySetView... $concurrentSet")
        try {
            for (item in concurrentSet) {
                println("Read: $item $concurrentSet")
                delay(1000)
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
    }
    val concurrentSetAddJob = scope.launch {
        delay(2000)
        println("Trying to add element in ConcurrentHashMap.KeySetView")
        concurrentSet.add(element = 5)
    }

    concurrentSetAddJob.join()
    concurrentSetReadJob.join()
}

