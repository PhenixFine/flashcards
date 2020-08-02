import utility.getNum
import utility.getString

fun main() {
    val strNumOfCards = "Input the number of cards:"
    val strCardNum = { num: Int -> "The card #$num:" }
    val strDefinition = { num: Int -> "The definition of the card #$num:" }
    val strGetDef = { term: String -> "Print the definition of \"$term\":" }
    val strCorrect = "Correct answer."
    val strWrong = { definition: String -> "Wrong answer. The correct one is \"$definition\"." }
    val numOfCards = getNum(strNumOfCards)
    val flashcards = Array(numOfCards) { Flashcard(getString(strCardNum(it + 1)), getString(strDefinition(it + 1))) }

    flashcards.forEach {
        val answer = getString(strGetDef(it.term))
        println(if (answer == it.definition) strCorrect else strWrong(it.definition))
    }
}