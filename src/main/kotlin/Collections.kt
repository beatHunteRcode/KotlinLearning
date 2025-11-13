fun main(args: Array<String>) {
//    linkedHashMap()
    linkedHashSet()
}

fun linkedHashMap() {
    val map = HashMap<String, Int>(mapOf("Foxtrot" to 1, "Uniform" to 2, "Charlie" to 3, "Kilo" to 4))
    println("Items of HashMap:")
    for (item in map) {
        println(item)
    }

    println("____")

    val linkedMap = LinkedHashMap<String, Int>(mapOf("Foxtrot" to 1, "Uniform" to 2, "Charlie" to 3, "Kilo" to 4))
    println("Items of LinkedHashMap:")
    for (item in linkedMap) {
        println(item)
    }
}

fun linkedHashSet() {
    val set = HashSet<String>(setOf("Foxtrot", "Uniform", "Charlie", "Kilo"))
    println("Items of HashSet:")
    for (item in set) {
        println(item)
    }

    println("____")

    val linkedSet = LinkedHashSet<String>(setOf("Foxtrot", "Uniform", "Charlie", "Kilo"))
    println("Items of LinkedHashSet:")
    for (item in linkedSet) {
        println(item)
    }
}