import utility.getNum
import utility.getString

fun main() {
    val strGetDef = { term: String -> "Print the definition of \"$term\":" }
    val strCorrect = "Correct answer."
    val strWrong = { definition: String -> "Wrong answer. The correct one is \"$definition\"" }
    val strWrongTerm = { term: String -> ", you've just written the definition of \"$term\"." }
    val flashcards = getFlashCards()

    for ((term, definition) in flashcards) {
        val answer = getString(strGetDef(term))
        println(
            when {
                answer == definition -> strCorrect
                flashcards.containsValue(answer) -> {
                    "${strWrong(definition)}${strWrongTerm(flashcards.keys.first { answer == flashcards[it] })}"
                }
                else -> "${strWrong(definition)}."
            }
        )
    }
}

fun getFlashCards(): Map<String, String> {
    val strNumOfCards = "Input the number of cards:"
    val strCardNum = { num: Int -> "The card #$num:" }
    val strDefinition = { num: Int -> "The definition of the card #$num:" }
    val strTermExists = { term: String -> "The card \"$term\" already exists. Try again:" }
    val strDefExists = { def: String -> "The definition \"$def\" already exists. Try again:" }
    val numOfCards = getNum(strNumOfCards)
    val flashcards = mutableMapOf<String, String>()

    for (i in 1..numOfCards) {
        var term = getString(strCardNum(i))
        while (flashcards.containsKey(term)) term = getString(strTermExists(term))
        var definition = getString(strDefinition(i))
        while (flashcards.containsValue(definition)) definition = getString(strDefExists(definition))
        flashcards[term] = definition
    }

    return flashcards
}