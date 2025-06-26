/**
 * Kotlin
 */

import kotlin.math.abs

class NumberOperations {

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