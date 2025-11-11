package com.example.ottogether.core.model

@JvmInline value class Money(val amountWon: Int) {
    override fun toString() = "%,d원".format(amountWon)
}