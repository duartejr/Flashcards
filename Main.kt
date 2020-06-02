package flashcards

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    Flashcards().start(args)
}

class Flashcards {
    private val scanner = Scanner(System.`in`)
    private val cards = mutableMapOf<String, String>()
    private val mistakes = mutableMapOf<String, Int>()
    private var log = ""
    private var exportFile = "null"

    fun start(args: Array<String>) {

        for (p in args.indices){
            if (args[p] == "-import") {
                import(args[p+1])
            }
            if (args[p] == "-export") {
                exportFile = args[p+1]
            }
        }

        loop@ while (true) {
            printMessage("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
            when (getInput()) {
                "add" -> add()
                "remove" -> remove()
                "import" -> import()
                "export" -> export()
                "ask" -> ask()
                "log" -> log()
                "hardest card" -> hardestCard()
                "reset stats" -> resetStats()
                "exit" -> {
                    println("Bye bye!")
                    if (exportFile != "null") {
                        export(exportFile)
                    }
                    return
                }
                else -> continue@loop
            }
        }
    }

    private fun add() {
        printMessage("The card:")
        val card = getInput()
        if (cards.containsKey(card)) {
            printMessage("The card \"$card\" already exists.\n")
            return
        }

        printMessage("The definition of the card:")
        val definition = getInput()
        if (cards.containsValue(definition)) {
            printMessage("The definition \"$definition\" already exists.\n")
            return
        }
        cards[card] = definition
        mistakes[card] = 0
        printMessage("The pair (\"$card\":\"$definition\") has been added.\n")
    }

    private fun remove() {
        printMessage("The card:")
        val card = getInput()
        printMessage(if (cards.contains(card)) {
            cards.remove(card)
            mistakes.remove(card)
            "The $card has been removed.\n"
        } else "Can't remove \"$card\": there is no such card.\n")

    }

    private fun import(fileName: String = "null") {
        if (fileName == "null") {
            printMessage("File name:")
            val fileName = getInput()
        }
        val file = File(fileName)
        if (!file.exists()) {
            printMessage("File not found.\n")
            return
        }
        val lines = file.readLines()
        for (i in 0..lines.lastIndex step 3) {
            cards[lines[i]] = lines[i + 1]
            mistakes[lines[i]] = lines[i + 2].toInt()
        }
        printMessage("${lines.size / 3} cards have been loaded.\n")
    }

    private fun export(fileName: String = "null") {
        var message = "cards have been saved.\n"
        if (fileName == "null") {
            printMessage("File name:")
            val fileName = getInput()
        }
        val file = File(fileName)
        var data = ""
        for ((key, value) in cards) {
            data += "$key\n$value\n${mistakes[key]}\n"
        }
        file.writeText(data.dropLast(1))

        printMessage("${cards.count()} $message")
    }

    private fun ask() {
        printMessage("How many times to ask?")
        repeat(getInput().toInt()) {
            val card = cards.entries.shuffled()[0]
            printMessage("Print the definition of \"${card.key}\":")
            val answer = getInput()
            if (answer != card.value) {
                printMessage("Wrong answer. " + if (cards.containsValue(answer)) "The correct one is \"${card.value}\", " +
                        "you've just written the definition of \"${cards.entries.find { it.value == answer }?.key}\"."
                else "The correct one is \"${card.value}\".")
                mistakes[card.key] = mistakes.getValue(card.key) + 1
            } else printMessage("Correct answer.")
        }
        println()
    }

    private fun log() {
        printMessage("File name:")
        val fileName = getInput()
        val file = File(fileName)
        file.writeText(log)
        println("The log has been saved.\n")
    }

    private fun hardestCard() {
        val maxMistake = mistakes.maxBy { it.value }
        if (mistakes.count() == 0 || mistakes[maxMistake?.key] == 0) {
            printMessage("There are no cards with errors.\n")
            return
        }
        val maxMistakes = mistakes.filter { it.value == mistakes[maxMistake?.key] }
        var cards = ""
        for (m in maxMistakes) {
            cards += "\"${m.key}\", "
        }
        cards = cards.dropLast(2)
        printMessage(if (maxMistakes.count() == 1)
            "The hardest card is $cards. You have ${mistakes[maxMistake?.key]} errors answering it.\n"
        else
            "The hardest cards are $cards. You have ${mistakes[maxMistake?.key]} errors answering them.\n")
    }

    private fun resetStats() {
        for ((key, _) in mistakes) {
            mistakes[key] = 0
        }
        printMessage("Card statistics has been reset\n")
    }

    private fun printMessage(message: String) {
        log += if (log.isEmpty() || message.first() == '\n') message else "\n$message"
        println(message)
    }

    private fun getInput(): String {
        val input = scanner.nextLine()
        log += "\n$input"
        return input
    }
}
