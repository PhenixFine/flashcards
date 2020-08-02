fun main() {
    val wrong = "Your answer is wrong..."
    val right = "Your answer is right!"
    val term = readLine()!!
    val definition = readLine()!!
    val answer = readLine()!!

    println(if (definition == answer) right else wrong)
}