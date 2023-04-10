package sound

import console.Interpreter
import kotlinx.coroutines.delay
import org.vosk.Model
import org.vosk.Recognizer
import java.lang.Math.round
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.math.roundToLong


suspend fun main() {
    val interpreter = Interpreter()
    interpreter.init()

    val callbacks = object : Callbacks {
        override suspend fun onMessage(message: String) {
            println("Recognizer: $message")
            interpreter.chat(message)
        }
    }

    val heyBlossom = HeyBlossom()
    heyBlossom.listen(callbacks)
}

interface Callbacks {
    suspend fun onMessage(message: String)
}

class HeyBlossom {

    suspend fun listen(callbacks: Callbacks) {
        val audioFormat = AudioFormat(16000.0f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val targetLine = AudioSystem.getLine(info) as TargetDataLine

        val model = Model("model")
        val recognizer = Recognizer(model, 16000.0f)

        val data = ByteArray(4096)
        targetLine.open(audioFormat, data.size)
        targetLine.start()

        val emptyAir = 1.5
        while (true) {
            val bytesRead = targetLine.read(data, 0, data.size)
            var lastResult = ""

            if (bytesRead > 0 && recognizer.acceptWaveForm(data, bytesRead)) {
                val result = recognizer.result ?: ""
                if(lastResult == result && result.contains("hey blossom", true)) {
                    callbacks.onMessage(lastResult.lowercase().substringAfter("hey blossom"))
                }
            }

            delay((emptyAir * 1000).roundToLong())
        }
    }
}