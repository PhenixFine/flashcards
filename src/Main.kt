import utility.getNum
import utility.getString
import java.io.File
import java.util.*

private val FLASHCARDS = mutableMapOf<String, String>()

fun main() {
    val strCommands = "Input the action (add, remove, import, export, ask, exit):"
    val errorAsk = { println("No questions stored to ask.\n") }
    var exit = false

    do {
        when (getString(strCommands).toLowerCase()) {
            "add" -> getFlashcard()
            "remove" -> removeFlashcard()
            "import" -> import()
            "export" -> export()
            "ask" -> if (FLASHCARDS.isEmpty()) errorAsk() else ask()
            "exit" -> exit = true
        }
    } while (!exit)
    println("Bye Bye!")
}

private fun getFlashcard() {
    val strTermExists = { term: String -> "The card \"$term\" already exists.\n" }
    val strDefExists = { def: String -> "The definition \"$def\" already exists.\n" }
    val term = getString("The card:")
    if (FLASHCARDS.containsKey(term)) {
        println(strTermExists(term))
        return
    }
    val definition = getString("The definition of the card:")
    if (FLASHCARDS.containsValue(definition)) {
        println(strDefExists(definition))
        return
    }
    FLASHCARDS[term] = definition
    println("The pair (\"$term\":\"$definition\") has been added.\n")
}

private fun removeFlashcard() {
    val strRemoved = "The card has been removed.\n"
    val term = getString("Which card:")
    val strNotFound = "Can't remove \"$term\": there is no such card.\n"

    println(
        if (FLASHCARDS.containsKey(term)) {
            FLASHCARDS.remove(term)
            strRemoved
        } else strNotFound
    )
}

private fun import() {
    val fileName = getString("File name:")
    try {
        var count = 0
        var term = ""
        File(fileName).forEachLine {
            count++
            if (count % 2 != 0) term = it else FLASHCARDS[term] = it
        }
        println("${count / 2} cards have been loaded\n")
    } catch (e: Exception) {
        println("File not found.\n")
    }
}

private fun export() {
    val myFile = File(getString("File name:"))

    try {
        var hold = ""
        for ((term, definition) in FLASHCARDS) hold += "$term\n$definition\n"
        myFile.writeText(hold.trim())
        println("${FLASHCARDS.size} cards have been saved.")
    } catch (e: Exception) {
        println("There was an error in writing your file, please try again.\n")
    }
}

private fun ask() {
    val tries = getNum("How many times to ask?")
    val strGetDef = { term: String -> "Print the definition of \"$term\":" }
    val strCorrect = "Correct answer."
    val strWrong = { definition: String -> "Wrong answer. The correct one is \"$definition\"" }
    val strWrongTerm = { term: String -> ", you've just written the definition of \"$term\"." }
    val terms = FLASHCARDS.keys.toTypedArray()
    val random = { (0..terms.lastIndex).random() }

    repeat(tries) {
        val term = terms[random()]
        val answer = getString(strGetDef(term))
        val definition = FLASHCARDS[term]!!
        println(
            when {
                answer == definition -> strCorrect
                FLASHCARDS.containsValue(answer) -> {
                    strWrong(definition) + strWrongTerm(FLASHCARDS.keys.first { answer == FLASHCARDS[it] })
                }
                else -> "${strWrong(definition)}."
            }
        )
    }
    println()
}

// this useful random function was found on Stack Overflow
private fun IntRange.random() = Random().nextInt(endInclusive + 1 - start) + start