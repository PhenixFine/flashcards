import java.io.File
import java.util.*

private val TERMS = mutableListOf<String>()
private val DEFINITIONS = mutableListOf<String>()
private val MISTAKES = mutableListOf<Int>()
private val LOG = mutableListOf<String>()

fun main() {
    val strCommands = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"
    val errorAsk = { output("No questions stored to ask.\n") }
    var exit = false

    do {
        when (getString(strCommands).toLowerCase()) {
            "add" -> getFlashcard()
            "remove" -> removeFlashcard()
            "import" -> import()
            "export" -> export()
            "ask" -> if (TERMS.isEmpty()) errorAsk() else ask()
            "exit" -> exit = true
            "log" -> export(true)
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
        }
    } while (!exit)
    println("Bye Bye!")
}

private fun getFlashcard() {
    val strTermExists = { term: String -> "The card \"$term\" already exists.\n" }
    val strDefExists = { def: String -> "The definition \"$def\" already exists.\n" }
    val term = getString("The card:")
    if (TERMS.contains(term)) {
        output(strTermExists(term))
        return
    }
    val definition = getString("The definition of the card:")
    if (DEFINITIONS.contains(definition)) {
        output(strDefExists(definition))
        return
    }
    addCard(term, definition, 0)
    output("The pair (\"$term\":\"$definition\") has been added.\n")
}

private fun removeFlashcard() {
    val strRemoved = "The card has been removed.\n"
    val term = getString("Which card:")
    val strNotFound = "Can't remove \"$term\": there is no such card.\n"

    output(
        if (TERMS.contains(term)) {
            val index = TERMS.indexOf(term)
            TERMS.removeAt(index)
            DEFINITIONS.removeAt(index)
            MISTAKES.removeAt(index)
            strRemoved
        } else strNotFound
    )
}

private fun import() {
    val file = getFile()
    try {
        var count = 0
        var list: List<String>
        file.forEachLine {
            count++
            list = it.split("@:#:%")
            if (TERMS.contains(list[0])) {
                overWriteCard(list[0], list[1], list[2].toInt())
            } else addCard(list[0], list[1], list[2].toInt())

        }
        output("$count cards have been loaded\n")
    } catch (e: Exception) {
        output("File not found.\n")
    }
}

private fun export(log: Boolean = false) {
    val file = getFile()
    val strExport = { num: Int ->
        if (log) LOG[num] else "${TERMS[num]}@:#:%${DEFINITIONS[num]}@:#:%${MISTAKES[num]}\n"
    }
    val strOutput = if (log) "The log has been saved.\n" else "${TERMS.size} cards have been saved.\n"

    try {
        file.writeText("")
        for (num in (if (log) LOG.indices else TERMS.indices)) file.appendText(strExport(num))
        output(strOutput)
    } catch (e: Exception) {
        output("There was an error in writing your file, please try again.\n")
    }
}

private fun ask() {
    val tries = getNum("How many times to ask?")
    val strGetDef = { term: String -> "Print the definition of \"$term\":" }
    val strCorrect = "Correct answer."
    val strWrong = { definition: String -> "Wrong. The right answer is \"$definition\"" }
    val strWrongTerm = { term: String -> ", but your definition is correct for \"$term\"." }
    val random = { (0..TERMS.lastIndex).random() }

    repeat(tries) {
        var index = random()
        val answer = getString(strGetDef(TERMS[index]))
        val definition = DEFINITIONS[index]
        output(
            when {
                answer == definition -> strCorrect
                DEFINITIONS.contains(answer) -> {
                    MISTAKES[index] += 1
                    index = DEFINITIONS.indexOf(answer)
                    strWrong(definition) + strWrongTerm(TERMS[index])
                }
                else -> {
                    MISTAKES[index] += 1
                    "${strWrong(definition)}."
                }
            }
        )
    }
    output("")
}

private fun hardestCard() {
    if (MISTAKES.all { (it == 0) }) output("There are no cards with errors.\n") else {
        val lrgNum = MISTAKES.max()!!
        val indexes = mutableListOf<Int>()
        for (num in MISTAKES.indices) if (MISTAKES[num] == lrgNum) indexes.add(num)
        if (indexes.size == 1) {
            output("The hardest card is \"${TERMS[indexes[0]]}\". You have $lrgNum errors answering it.\n")
        } else {
            var combo = ""
            for (i in indexes.indices) combo += "\"${TERMS[indexes[i]]}\"" + if (i != indexes.lastIndex) ", " else ""

            output("The hardest cards are $combo. You have $lrgNum errors answering them.\n")
        }
    }
}

private fun resetStats() {
    for (i in MISTAKES.indices) MISTAKES[i] = 0
    output("Card statistics have been reset.\n")
}

private fun addCard(term: String, definition: String, mistakes: Int) {
    TERMS.add(term)
    DEFINITIONS.add(definition)
    MISTAKES.add(mistakes)
}

private fun overWriteCard(term: String, definition: String, mistakes: Int) {
    val index = TERMS.indexOf(term)
    DEFINITIONS[index] = definition
    MISTAKES[index] = mistakes
}

private fun getFile(): File = File(getString("File name:"))

private fun output(string: String) {
    LOG.add("$string\n")
    println(string)
}

// this useful random function was found on Stack Overflow
private fun IntRange.random() = Random().nextInt(endInclusive + 1 - start) + start

private fun getNum(text: String, defaultMessage: Boolean = false): Int {
    val strErrorNum = " was not a number, please try again: "
    var num = text
    var default = defaultMessage

    do {
        num = getString(if (default) num + strErrorNum else num)
        if (!default) default = true
    } while (!isNumber(num))

    return num.toInt()
}

private fun getString(text: String): String {
    output(text)
    val hold = readLine()!!
    LOG.add("$hold\n")
    return hold
}

private fun isNumber(number: String) = number.toIntOrNull() != null