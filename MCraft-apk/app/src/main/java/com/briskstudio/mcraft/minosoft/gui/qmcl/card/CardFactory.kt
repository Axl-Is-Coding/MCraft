package de.bixilon.minosoft.gui.qmcl.card

interface CardFactory<T> {
    fun build(): T
}
