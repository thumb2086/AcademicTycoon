package com.tycoon.academic.ui.screens.casino

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String {
        return "${rank.symbol}${suit.symbol}"
    }
}

enum class Suit(val symbol: Char) { CLUB('♣'), DIAMOND('♦'), HEART('♥'), SPADE('♠') }

enum class Rank(val symbol: String, val value: Int) {
    ACE("A", 11), TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), 
    SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10), 
    JACK("J", 10), QUEEN("Q", 10), KING("K", 10)
}

class Deck {
    private val cards = mutableListOf<Card>()

    init {
        reset()
    }

    fun reset() {
        cards.clear()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
        cards.shuffle()
    }

    fun drawCard(): Card = if (cards.isNotEmpty()) cards.removeAt(0) else throw IllegalStateException("Deck is empty")
}

fun calculateHandValue(hand: List<Card>): Int {
    var value = hand.sumOf { it.rank.value }
    var aces = hand.count { it.rank == Rank.ACE }
    while (value > 21 && aces > 0) {
        value -= 10
        aces--
    }
    return value
}
