package flashcards

import java.io.File
import java.io.FileNotFoundException
import java.util.*
val scanner = Scanner(System.`in`)
var deck = mutableMapOf<String, String>()

fun main() {

    loop@ while (true) {
        println("Input the action (add, remove, import, export, ask, exit):")
        when (scanner.next()) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import()
            "export" -> export()
            "ask" -> ask()
            "exit" -> break@loop
        }
    }
    println("Bye Bye!")
}

fun add() {
    val scan = Scanner(System.`in`)
    println("The card:")
    val card = scan.nextLine()
    if (card in deck.keys) {
        println("The card \"$card\" already exists.\n")
        return
    }
    println("The definition of the card:")
    val definition = scan.nextLine()
    if (definition in deck.values) {
        println("The definition \"$definition\" already exists.\n")
        return
    }
    deck[card] = definition
    println("The pair (\"$card\":\"$definition\") has been added.\n")
}

fun remove() {
    val scan = Scanner(System.`in`)
    println("The card:")
    val card = scan.nextLine()
    if (card in deck.keys) {
        deck.remove(card)
        println("The card has been removed.\n")
    } else {
        println("Can't remove \"$card\": there is no such card.\n")
    }
}

fun import() {
    val scan = Scanner(System.`in`)
    println("File name:")
    val filename = scan.nextLine()
    try {
        val lines = File(filename).readLines()
        deck.clear()
        for (line in lines){
            val card = line.split("=")[0]
            val definition = line.split("=")[1]
            deck[card] = definition
        }
    } catch (e: FileNotFoundException) {
        println("File not found.\n")
    }
}

fun export() {
    val scan = Scanner(System.`in`)
    println("File name:")
    val filename = scan.nextLine()
    val myFile = File(filename)
    val nCards = deck.size
    if (nCards == 0) {
        println("Has no cards do export.\n")
    } else {
        myFile.printWriter().use { out ->
            for (i in deck) {
                out.println(i)
            }
        }
        println("$nCards cards have been saved.\n")
    }
}

fun ask() {
    val scan = Scanner(System.`in`)
    println("How many times to ask?")
    val nAsks = scan.nextLine().toInt()
    val nCards = deck.size
    for (i in 1..nAsks) {
        val key = (deck.keys).random()
        println("Print the definition of \"$key\":")
        val answer = scan.nextLine()
        if (deck[key].equals(answer)) println("Correct answer.")
        else {
            val answer = deck[key]
            println("Wrong answer. The correct one is \"$answer\"")
        }
    }
    println("")
}