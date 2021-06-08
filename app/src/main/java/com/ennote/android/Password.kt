package com.ennote.android

import kotlin.random.Random
import kotlin.random.nextInt

object Password {
    var length: Int = 5
    var upperCase: Boolean = false
    var lowerCase: Boolean = false
    var number: Boolean = false
    var symbol: Boolean = false

    private val uppers = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )
    private val lowers = listOf(
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z"
    )
    private val numbers = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    private val symbols = listOf("!", "@", "#", "\$", "%", "^", "&", "*")

    fun getPassword(): String {
        if (!upperCase && !lowerCase && !number && !symbol) return ""

        val stringBuilder = StringBuilder()

        while (stringBuilder.length < length) {
            when (Random.nextInt(1..4)) {
                1 -> if (upperCase) stringBuilder.append(uppers[Random.nextInt(uppers.indices)])
                2 -> if (lowerCase) stringBuilder.append(lowers[Random.nextInt(lowers.indices)])
                3 -> if (number) stringBuilder.append(numbers[Random.nextInt(numbers.indices)])
                4 -> if (symbol) stringBuilder.append(symbols[Random.nextInt(symbols.indices)])
            }
        }

        return stringBuilder.toString()
    }

    fun reset() {
        length = 5
        upperCase = false
        lowerCase = false
        number = false
        symbol = false
    }
}