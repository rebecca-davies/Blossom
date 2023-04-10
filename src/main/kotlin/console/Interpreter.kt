package console

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.sun.org.apache.xerces.internal.xs.StringList
import com.tinder.StateMachine
import kotlinx.coroutines.flow.Flow
import javax.sound.sampled.AudioSystem
import console.Commands

sealed class State {
    object ReadState : State()
    object ReadCmd : State()
    object ReadArgs : State()
    object ReadArg : State()
}

sealed class Event {
    object OnFinishedCmd : Event()
    object OnFinishedArgs : Event()
    object OnFinishedArg : Event()
    object OnOpenedCmd : Event()
    object OnOpenedArgs: Event()
    object OnOpenedArg: Event()
}

sealed class SideEffect {

}

@OptIn(BetaOpenAI::class)
class Interpreter {
    private val openAI = OpenAI("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")

    suspend fun init(){
        val response = chatList((listOf(
            "Act as a flirty home assistant.",
            "I'm interpreting your replies to control a program.",
            "For information you want me to hear, please write:",
            "{response}(YourRegularResponseToAQuestion)",
            "If I ask for a 10 minute timer, please reply with this formatting:",
            "INTERPRET{[timer](\"600\")[response](\"10 minute timer started\")}",
            "Please ensure time supplied is in seconds and to use a float for fractional numbers",
            "Take note of how I use braces, brackets, parenthesis and quotation marks for my interpreter",
            "If I define a command with you that takes multiple arguments, please separate them with a comma",
            "Ensure all arguments are wrapped in quotation marks",
            "[response] will be sent through text to speech, it is consumer facing",
            "to make a debug message, use the command: [debug](\"Question or comment you wish to convey to help improve the program\")",
            "Make sure every reply you make includes INTERPRET{} with the commands I've specified",
            "Using the debug command, state that you have been initialized."
        )))

        println(response)
    }

    suspend fun chatList(messages: List<String>): String {
        var chatMessages = mutableListOf<ChatMessage>()

        messages.forEach {
            chatMessages.add(ChatMessage(
                role = ChatRole.User,
                content = it
            ))
        }

        val completionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chatMessages
        )

        var response = ""
        openAI.chatCompletions(completionRequest).collect { result ->
            result.choices.forEach {
                it.delta?.let { delta ->
                    response += delta.content ?: ""
                }

            }
        }

        return response
    }

    suspend fun chat(message: String): String {
        return chatList(listOf(message))
    }

    fun findCommands(message: String):Map<String, List<String>> {
        var interpret = message.substringAfter("INTERPRET{","").substringBefore('}')
        var outCmd = mutableMapOf<String, List<String>>()

        if (interpret == "") { return outCmd }

        var builder = ""
        var lastCmd = ""
        var args = mutableListOf<String>()

        val reader = newReaderSM()

        interpret.forEach {
            when (reader.state) {
                State.ReadState -> {
                    when (it) {
                        '[' -> {
                            builder = ""
                            reader.transition(Event.OnOpenedCmd)
                        }
                        '(' -> {
                            builder = ""
                            reader.transition(Event.OnOpenedArgs)
                        }
                    }
                }
                State.ReadCmd -> {
                    if (it == ']') {
                        lastCmd = builder
                        reader.transition(Event.OnFinishedCmd)
                    }
                    else
                    {
                        builder += it
                    }
                }
                State.ReadArgs -> {
                    when (it) {
                        '"' -> {
                            builder = ""
                            reader.transition(Event.OnOpenedArg)
                        }
                        ')' -> {
                            outCmd[lastCmd] = args
                            args.clear()
                            reader.transition(Event.OnFinishedArgs)
                        }
                    }
                }
                State.ReadArg -> {
                    if(it == '"') {
                        args.add(builder)
                        reader.transition(Event.OnFinishedArg)
                    }
                    else {
                        builder += it
                    }
                }
            }
        }

        return outCmd
    }

    suspend fun runCommands(message: String){
        var cmds = findCommands(message)
        cmds.forEach {
            if (it.key == "debug") {
                println("Interpreter [${openAI.hashCode()}] Debug: ${it.value[0]}")
            }
            else {
                Commands.executeCommand(it.key, it.value)
            }
        }
    }

    fun newReaderSM() : StateMachine<State, Event, SideEffect> {
        return StateMachine.create {
            initialState(State.ReadState)
            state<State.ReadState> {
                on<Event.OnOpenedCmd> {
                    transitionTo(State.ReadCmd)
                }
                on<Event.OnOpenedArgs> {
                    transitionTo(State.ReadArg)
                }
            }
            state<State.ReadCmd> {
                on<Event.OnFinishedCmd> {
                    transitionTo(State.ReadState)
                }
            }
            state<State.ReadArgs> {
                on<Event.OnFinishedArgs> {
                    transitionTo(State.ReadState)
                }
                on<Event.OnOpenedArg> {
                    transitionTo(State.ReadArg)
                }
            }
            state<State.ReadArg> {
                on<Event.OnFinishedArg> {
                    transitionTo(State.ReadArgs)
                }
            }
            onTransition {
                val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            }
        }
    }
}

suspend fun main() {
    val interpreter = Interpreter()
    interpreter.init()
    val response = interpreter.chat("write a debug message that says \"Hello world!\"")
    println("test: ${response}")
    interpreter.runCommands(response)
}