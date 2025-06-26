import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking
import java.rmi.server.LogStream.log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


fun main(args: Array<String>) {

    runBlocking {
        println("Starting the async computation")
        val myFirstDeferred = async { slowlyAddNumbers(2, 2) }
        val mySecondDeferred = async { slowlyAddNumbers(4, 4) }
        println("Waiting for the deferred value to be available")
        println("The first result: ${myFirstDeferred.await()}")
        println("The second result: ${mySecondDeferred.await()}")
        println("ABOBA")
    }

//    val english = listOf("red", "yellow", "blue").asFlow()
//    val russian = listOf("красный", "желтый", "синий").asFlow()
//    english.zip(russian) { a, b -> "$a: $b" }
//        .collect { word -> println(word) }
//
//    val list = mutableListOf<Int>(1, 2, 3)
//    println(list)
//    list.clear()
//    println(list)
//
//    val doge: Doge = Doge()
//    println(doge.paws)
//
//    val datePattern = "dd.MM.yyyy K:mm a"
//    val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
//
//    val date1 = "14.10.2020 14:30 AM"
//    val date2 = "15.10.2020 14:30 AM"
//    val date3 = "15.10.2020 14:30 AM"
//
//    val dateOne = dateFormat.parse(date1)
//    val dateTwo = dateFormat.parse(date2)
//
//    println(compareTo(date1, date2))
//
//
//    var xList = listOf<Double>(8.0, 9.0, 7.0, 8.0, 7.0)
//    val ND = Statistics()
//
//    println("Схрон | Запрос")
//    xList = listOf<Double>(8.0, 9.0, 7.0, 8.0, 7.0)
//    ND.calculate(xList)
//
//    println("Схрон | Кэш")
//    xList = listOf<Double>(8.0, 10.0, 7.0, 9.0, 8.0)
//    ND.calculate(xList)
//
//
//    println("Торговец | Запрос")
//    xList = listOf<Double>(644.0, 531.0, 413.0, 533.0, 426.0)
//    ND.calculate(xList)
//
//
//    println("Торговец | Кэш")
//    xList = listOf<Double>(121.0, 68.0, 55.0, 45.0, 43.0)
//    ND.calculate(xList)
//
//    val aop = ArithmeticOperationsSumOnly()
//
//    println(aop.sum(1, 2))
//    println(aop.subtract(3, 2))
//    println(aop.multiply(3, 8))
//    println(aop.divide(50, 10))
//
//    var no = NumberOperations()
//    var array = arrayOf(1, 10, 20, 30, 40, 50)
//    println(no.closestNumbTo(231, array))
//
//    val jsonToCSVConverter = JSONToCSVConverter()
//    jsonToCSVConverter.convert("./input/stream_list.json")

}

fun compareTo(date1: String, date2: String): Int {
    val datePattern = "dd.MM.yyyy K:mm a"
    val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)


    val thisDate = dateFormat.parse(date1)

    val otherDate = dateFormat.parse(date2)

    return when {
        thisDate!!.time > otherDate!!.time -> 1
        thisDate.time < otherDate.time -> -1
        else -> 0
    }
}

suspend fun slowlyAddNumbers(a: Int, b: Int): Int {
    log("Waiting a bit before calculating $a + $b")
    delay(1000.milliseconds * a)
    return a + b
}