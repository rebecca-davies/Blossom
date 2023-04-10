package app

import org.vosk.Model
import org.vosk.Recognizer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import actions.*
import org.vosk.LibVosk
import org.vosk.LogLevel


suspend fun main() {
    TTS2
    Timers

    val interpreter = Interpreter()
    interpreter.init()

    val callbacks = object : Callbacks {
        override suspend fun onMessage(message: String) {
            println("Recognizer: $message")
            var result = interpreter.chat(message)
            println(result)
        }
    }

    val heyBlossom = Listener()
    heyBlossom.listen(callbacks)
}

interface Callbacks {
    suspend fun onMessage(message: String)
}

class Listener {
    suspend fun listen(callbacks: Callbacks) {
        val audioFormat = AudioFormat(16000.0f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val targetLine = AudioSystem.getLine(info) as TargetDataLine

        val model = Model("model")
        LibVosk.vosk_set_log_level(-1)

        val recognizer = Recognizer(model, 16000.0f)

        val data = ByteArray(4096)
        targetLine.open(audioFormat, data.size)
        targetLine.start()

        val emptyAir = 1.5

        while (true) {
            val bytesRead = targetLine.read(data, 0, data.size)

            if (bytesRead > 0 && recognizer.acceptWaveForm(data, bytesRead)) {
                val result = recognizer.result ?: ""
                if(result.contains("hey blossom", true)) {
                    callbacks.onMessage(result.lowercase().substringAfter("hey blossom"))
                }
            }
        }
    }
}