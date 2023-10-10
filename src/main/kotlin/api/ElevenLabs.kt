package api

import javazoom.jl.player.advanced.AdvancedPlayer
import kotlinx.coroutines.runBlocking
import log
import net.andrewcpu.elevenlabs.ElevenLabs

/**
 * ElevenLabs class for generating voice using the ElevenLabs API.
 *
 * This class implements the Api interface for integration with a larger system.
 */
class ElevenLabs : Api {

    /**
     * Initialization function for the ElevenLabs class.
     * Logs the successful loading of the class and sets the API key for ElevenLabs.
     */
    override fun init() {
        log.info("Loaded ${this::class.java.simpleName}")

        // Setting the API key for ElevenLabs
        ElevenLabs.setApiKey(AppConfig.availableApis.api["elevenlabs"]?.key!!)

        //Display warning if the API is disabled
        if (AppConfig.availableApis.api["elevenlabs"]?.enabled == false) {
            log.warning("ElevenLabs API is set to disabled")
        }
    }

    /**
     * Message handler for generating voice using the ElevenLabs API.
     * If the ElevenLabs API is disabled, logs a message and returns early.
     *
     * @param msg The input message for voice generation.
     */
    override fun message(msg: String) {
        // Checking if ElevenLabs API is enabled and log response in text instead
        if (AppConfig.availableApis.api["elevenlabs"]?.enabled == false) {
            log.info(msg)
            return
        }

        log.info("Generating voice")

        // Getting the voice from ElevenLabs API
        val voice = ElevenLabs.getVoice("oWAxZDx7w5VEj9dCyTzz")

        // Generating a stream from the voice for the provided message
        val inputStream = voice.generateStream(msg)

        log.info("Playing voice")

        // Playing the generated voice using AdvancedPlayer
        val player = AdvancedPlayer(inputStream)
        player.play()
    }

    /**
     * Response handler for the ElevenLabs API (not implemented).
     *
     * @param msg The input message for the response (not used in this implementation).
     */
    override fun response(msg: String) = runBlocking {
        // Not implemented for this class
    }
}
