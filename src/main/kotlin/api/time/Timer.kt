package api.time

import ApiContainer
import api.Api
import com.ibm.icu.text.RuleBasedNumberFormat
import com.ibm.icu.util.ULocale
import javazoom.jl.player.advanced.AdvancedPlayer
import log
import kotlinx.coroutines.*
import java.text.ParsePosition
import java.util.concurrent.TimeUnit

/**
 * Data class to store information about an active timer.
 *
 * @property job The coroutine job representing the timer execution.
 * @property duration The original duration of the timer in milliseconds.
 * @property unixTimeStamp The timestamp when the timer was started in UNIX time.
 */
data class TimerInfo(val job: Job, val duration: Long, val unixTimeStamp: Long)

/**
 * Timer class that allows setting and triggering timers with corresponding alarms.
 *
 * This class implements the Api interface for integration with a larger system.
 *
 * @property timerJob The coroutine job representing the timer execution.
 * @property jobLength The string representation of the timer duration for reporting purposes.
 * @property alarm Input stream for the alarm sound file, assumed to be an MP3 file.
 * @property activeTimers List to store information about active timers.
 */
class Timer : Api {

    private val activeTimers = mutableListOf<TimerInfo>()
    private var timerJob: Job? = null
    private var jobLength: String = ""
    private var alarm = javaClass.getResourceAsStream("/audio/beep.mp3")

    /**
     * Initialization function for the Timer class.
     * Logs the successful loading of the class.
     */
    override fun init() {
        log.info("Loaded ${this::class.java.simpleName}")
    }

    /**
     * Message handler for timer-related messages.
     * Sets and triggers a timer based on the provided message.
     *
     * @param msg The input message containing timer-related information.
     */
    @OptIn(DelicateCoroutinesApi::class)
    override fun message(msg: String) {
        if(msg.contains("how much time is left", ignoreCase = true)) {
            checkTimers()
            return
        }
        val duration = extractTime(msg)
        if (duration != null) {
            timerJob = GlobalScope.launch {
                val player = AdvancedPlayer(alarm)
                delay(duration)
                player.play()
            }
            activeTimers.add(TimerInfo(timerJob!!, duration, System.currentTimeMillis()))
            ApiContainer.elevenlabs.message("A timer has been set for $jobLength.")
        }
    }

    /**
     * Checks and logs the time left for active timers.
     */
    private fun checkTimers() {
        for (timerInfo in activeTimers.toList()) {
            val timeElapsed = System.currentTimeMillis() - timerInfo.unixTimeStamp
            val timeDifference = timerInfo.duration - timeElapsed
            val (minutes, seconds) = calculateMinutesAndSeconds(timeDifference)
            val formattedTime = formatTime(minutes, seconds)
            val message = if (formattedTime != "finished") "Your timer has $formattedTime left" else "Your timer has finished"
            log.info(message)
        }
    }

    /**
     * Response handler for timer-related messages (not implemented).
     *
     * @param msg The input message containing timer-related information.
     */
    override fun response(msg: String) {
        // Not implemented for this class
    }

    /**
     * Extracts the time duration from the provided sentence.
     * Converts words like "five" to numerical representation and calculates the duration in milliseconds.
     *
     * @param sentence The input sentence containing timer-related information.
     * @return The duration of the timer in milliseconds, or null if extraction fails.
     */
    private fun extractTime(sentence: String): Long? {
        val numberFormat = RuleBasedNumberFormat(ULocale.getDefault(), RuleBasedNumberFormat.SPELLOUT)
        val regex = Regex("(\\w+)\\s*(seconds?|minutes?)")
        val matchResult = regex.find(sentence)

        return matchResult?.let {
            val (amount, unit) = it.destructured
            val numericAmount = try {
                numberFormat.parse(amount, ParsePosition(0)).toLong()
            } catch (e: Exception) {
                null
            }
            jobLength = "$amount $unit"
            numericAmount?.let { num ->
                val multiplier = when (unit) {
                    "second", "seconds" -> 1000
                    "minute", "minutes" -> 60000
                    else -> 1
                }
                num * multiplier
            }
        }
    }

    /**
     * Calculates minutes and seconds from the given time difference.
     *
     * @param timeDifference The time difference in milliseconds.
     * @return Pair of minutes and seconds.
     */
    private fun calculateMinutesAndSeconds(timeDifference: Long): Pair<Long, Long> {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference - TimeUnit.MINUTES.toMillis(minutes))
        return Pair(minutes, seconds)
    }

    /**
     * Formats minutes and seconds into a human-readable string.
     *
     * @param minutes The minutes part.
     * @param seconds The seconds part.
     * @return The formatted time string.
     */
    private fun formatTime(minutes: Long, seconds: Long): String {
        val formattedMinutes = "$minutes ${if (minutes.toInt() != 1) "minutes" else "minute"}"
        val formattedSeconds = "$seconds second${if (seconds.toInt() != 1) "s" else ""}"
        return when {
            minutes > 0 && seconds > 0 -> "$formattedMinutes and $formattedSeconds"
            minutes > 0 -> formattedMinutes
            seconds > 0 -> formattedSeconds
            else -> "finished"
        }
    }
}
