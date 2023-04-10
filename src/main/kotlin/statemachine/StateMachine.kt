package statemachine

import kotlinx.coroutines.delay

class StateEntry(var canEnterState: () -> Boolean) {
    constructor() : this({ true })

    fun canEnter(): Boolean = canEnterState()
}

class State {
    var entries = mutableMapOf<String, StateEntry>()
    var intermediateStates = mutableListOf<State>()
    var connections = mutableListOf<Pair<State, String>>()

    private lateinit var intermediateEntry : State

    fun addEntry(id: String, entry: StateEntry) {
        entries[id] = entry;
    }

    fun addIntermediateState(state: State, setAsEntry: Boolean = false) {
        if(setAsEntry)
        {
            intermediateEntry = state
        }
        intermediateStates.add(state)
    }

    fun addConnection(state: State, entry: String) {
        connections.add(Pair(state, entry))
    }

    fun canEnter(id: String): Boolean {
        return entries[id]?.canEnter() ?: true
    }

    suspend fun update() {
        connections.forEach {
            if(it.first.canEnter(it.second)) {
                it.first.update()
                return
            }
        }
        intermediateEntry.update()
    }
}

suspend fun main() {
    val Main = State()
    val Body = State()
    Body.addEntry("1", StateEntry() {
        return@StateEntry true
    })
    Main.addConnection(Body, "1")

    Main.update()
}