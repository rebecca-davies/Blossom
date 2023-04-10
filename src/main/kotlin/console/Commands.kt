package console

import sound.HeyBlossom
import kotlinx.coroutines.*
import org.jetbrains.annotations.Nullable
import javax.sound.sampled.AudioSystem
import kotlin.reflect.typeOf

class Command {
    private var name: String = ""
    private var function: ((List<String>) -> Unit)? = null

    constructor(inName: String, inFunc: (List<String>) -> Unit) {
        name = inName
        function = inFunc
    }

    fun run(arguments: List<String>) {
        function?.invoke(arguments)
    }
}

object Commands {
    private val userInputScope = CoroutineScope(Dispatchers.IO)
    private var commands = mutableMapOf<String, Command>()

    fun start() {
        userInputScope.launch {
            while (true) {
                val userInput = readlnOrNull()
                if (userInput != null) {
                    handleUserInput(userInput)
                }
            }
        }
    }

    fun stop() {
        userInputScope.cancel()
    }

    fun isActive() : Boolean {
        return userInputScope.isActive
    }

    fun addCommand(name: String, func: (List<String>) -> Unit) {
        commands[name] = Command(name, func)
    }

    private suspend fun handleUserInput(userInput: String) {
        // Split the input into a command and arguments
        val inputParts = userInput.split(" ")

        // Access the command and arguments
        val command = inputParts.firstOrNull()
        val arguments = inputParts.drop(1)

        // Execute the command
        executeCommand(command, arguments)
    }

    suspend fun executeCommand(command: String?, arguments: List<String>) {
        commands[command] ?.run(arguments)
    }
}

fun main() = runBlocking {

    Commands.addCommand("list") {
        AudioSystem.getMixerInfo().forEachIndexed { index, info ->
            println("[$index] ${info.name}")
        }
    }

    Commands.start()
    while (Commands.isActive()) {
        delay(100)
    }
}