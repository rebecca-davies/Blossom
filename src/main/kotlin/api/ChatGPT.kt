package api

import ApiContainer
import AppConfig
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlinx.coroutines.runBlocking
import log

/**
 * ChatGPT class that communicates with the OpenAI GPT-3.5-turbo model for natural language processing.
 *
 * This class implements the Api interface for integration with a larger system.
 *
 * @property client The OpenAI client used for communication.
 */
class ChatGPT : Api {

    private lateinit var client: OpenAI

    /**
     * Initialization function for the ChatGPT class.
     * Logs the successful loading of the class and initializes the OpenAI client.
     */
    override fun init() {
        log.info("Loaded ${this::class.java.simpleName}")
        // Assumes that the OpenAI API key is available in the AppConfig
        client = OpenAI(OpenAIConfig(AppConfig.availableApis.api["openai"]?.key!!))
    }

    /**
     * Message handler for natural language processing using the OpenAI GPT-3.5-turbo model.
     * Sends a user message to the model and processes the response.
     *
     * @param msg The input message for natural language processing.
     */
    @OptIn(BetaOpenAI::class)
    override fun message(msg: String) = runBlocking {
        log.info("Sending message: $msg")

        // Constructing a conversation with a user message
        val conversation = listOf(ChatMessage(role = ChatRole.User, content = msg))

        // Creating a request for chat completion
        val chatCompletion = ChatCompletionRequest(model = ModelId("gpt-3.5-turbo"), messages = conversation, maxTokens = 256)

        var chatResponse = ""

        // Collecting and processing the response from the OpenAI GPT-3.5-turbo model
        client.chatCompletions(chatCompletion).collect { result ->
            result.choices.forEach {
                it.delta?.let { delta ->
                    chatResponse += delta.content ?: ""
                }
            }
        }

        // Processing the response
        response(chatResponse)
    }

    /**
     * Response handler for the output of the natural language processing.
     * Logs the received response and sends it to a downstream system (ApiContainer.elevenlabs).
     *
     * @param msg The processed message from the natural language processing.
     */
    override fun response(msg: String) = runBlocking {
        log.info("Received response: $msg")
        ApiContainer.elevenlabs.message(msg)
    }
}
