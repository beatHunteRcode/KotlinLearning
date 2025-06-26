import kotlin.math.abs

/***
 * Class with base arithmetic operations only with sum operator
 */
class ArithmeticOperationsSumOnly {

    public fun sum(a: Int, b: Int): Int = a + b

    public fun subtract(a: Int, b: Int): Int = a + negate(b)

    public fun multiply(a: Int, b: Int): Int {
        var sum = 0;
        for (i in 1..b) {
            sum += a
        }
        return sum
    }

    /***
     * Воспроизведения алгоритма "деления в столбик"
     * a - делимое
     * b - делитель
     * answer - частное
     * div - остаток от деления
     * count - цифры из частного
     */
    public fun divide(a: Int, b: Int): Int {
        var answer = 0;
        var div = a
        while (div > b) {
            answer = (multiply(answer, 10))
            var sumTemp = 0
            var count = 0
            while (sumTemp < a) {
                sumTemp += b
                count++
            }
            div = subtract(div, multiply(b, count))
            answer += count
        }
        return answer
    }

    private fun negate(a: Int): Int {
        var thisA = a
        var answer = 0
        val d = if (thisA < 0) 1 else -1
        while (thisA != 0) {
            answer += d
            thisA += d
        }
        return answer
    }

    /***
     * Дан массив натуральных неотрицательных чисел.
     * Требуется за один проход найти число, максимально близкое введенному пользователем числу.
     *
     * Основная идея - нахождение разности между двумя числами.
     * Возвращается самая маленькая разность между двумя числами
     */

    public fun closestNumbTo(entryNumb : Int, array : Array<Int>): Int {
        var closestNumb = 0
        var delta = Int.MAX_VALUE
        for (arrayNumb in array) {
            if (abs(entryNumb - arrayNumb) < delta) {
                delta = abs(entryNumb - arrayNumb)
                closestNumb = arrayNumb
            }
        }
        return closestNumb
    }
}