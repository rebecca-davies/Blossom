package api.time

import api.Api

import log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the Api interface to provide current time information.
 *
 * This class retrieves the current time and formats it in a 12-hour clock format with AM or PM.
 */
class CurrentTime : Api {

    /**
     * Initialization function for the CurrentTime class.
     * Logs the successful loading of the class.
     */
    override fun init() {
        log.info("Loaded ${this::class.java.simpleName}")
    }

    /**
     * Message handler for time-related requests.
     * Retrieves the current time and sends a message with the formatted 12-hour clock time.
     *
     * @param msg Ignored in this implementation.
     */
    override fun message(msg: String) {
        // Format the current time in 12-hour clock format with AM or PM.
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val current = LocalDateTime.now().format(formatter)

        // Send a message with the current time.
        ApiContainer.elevenlabs.message("It's $current")
    }

    /**
     * Response handler (not implemented).
     *
     * @param msg Ignored in this implementation.
     */
    override fun response(msg: String) {
        // Not implemented for this class
    }
}
