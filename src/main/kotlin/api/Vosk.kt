package api

import ApiContainer
import com.fasterxml.jackson.databind.ObjectMapper
import log
import org.vosk.LibVosk
import org.vosk.Model
import org.vosk.Recognizer
import java.io.FileNotFoundException
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.io.path.absolutePathString
import kotlin.io.path.toPath

/**
 * Vosk class for speech recognition using the Vosk library.
 *
 * This class listens for a trigger phrase and processes the recognized speech.
 */
class Vosk {

    // Initialization of the Vosk library log level
    init {
        LibVosk.vosk_set_log_level(-1)
    }

    // Audio format configuration for speech recognition
    private val audioFormat = AudioFormat(16000.0f, 16, 1, true, false)
    private val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
    private val targetLine = AudioSystem.getLine(info) as TargetDataLine

    // Loading the Vosk model for speech recognition
    private val model = Model({}::class.java.getResource("/vosk")?.toURI()?.toPath()?.absolutePathString()
        ?: throw FileNotFoundException("Model folder not found"))
    private val recognizer = Recognizer(model, 16000.0f)

    // Buffer for audio data
    private val data = ByteArray(4096)

    // ObjectMapper for JSON processing
    private val mapper = ObjectMapper()

    /**
     * Listens for the trigger phrase "Hey Blossom" and processes the recognized speech.
     */
    fun listen() {
        log.info("Listening for \"Hey Blossom\"")
        targetLine.open(audioFormat, data.size)
        targetLine.start()

        while (true) {
            val bytesRead = targetLine.read(data, 0, data.size)

            // Accepting the waveform for speech recognition
            if (bytesRead > 0 && recognizer.acceptWaveForm(data, bytesRead)) {
                var result = mapper.readTree(recognizer.result).get("text").asText() ?: ""

                // Processing the recognized speech
                if (result.contains("hey blossom", true)) {
                    result = result.substringAfter("hey blossom ")
                    log.info(result)

                    with(result) {
                        when {
                            contains("set a timer") -> ApiContainer.timer.message(result)
                            contains("how much time is left") -> ApiContainer.timer.message(result)
                            contains("what time is it") -> ApiContainer.currenttime.message(result)
                            else -> ApiContainer.openai.message(result)
                        }
                    }
                }
            }
        }
    }
}
