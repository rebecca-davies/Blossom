package actions

import console.Commands

object Timers {
    init {
        Commands.addCommand("timer") {
            println("Timer started with ${it[0] ?: "invalid"} seconds")
        }
    }
}
