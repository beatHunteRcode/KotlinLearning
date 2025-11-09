fun main(args: Array<String>) {
    val prefix = "Executing: "
    doSomething(
        func = {
            println("$prefix $it aboba")
            return@doSomething
        },
        func2 = {
            println("$prefix $it")
        }
    )
    println("END")
}

inline fun doSomething(func: (Int) -> Unit, noinline func2: (Int) -> Unit) {
    println("a")
    doAbobathing {
        return@doAbobathing
    }
    func(10)
    func2(2)
    println("asdasda")
}

inline fun doAbobathing(crossinline func: (Int) -> Unit) {
    doAnything {
        func(4)
    }
}

fun doAnything(func: (Int) -> Unit) {
    func(2)
}

private inline fun <reified T> printType() {
    println(T::class.simpleName)
}

