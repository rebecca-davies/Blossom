package sound

import org.vosk.Model
import org.vosk.Recognizer
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine


fun main() {
}


class HeyBlossom {

    var message = ""
    fun listen() {
        val audioFormat = AudioFormat(16000.0f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val targetLine = AudioSystem.getLine(info) as TargetDataLine

        val model = Model("model")
        val recognizer = Recognizer(model, 16000.0f)

        val data = ByteArray(4096)
        targetLine.open(audioFormat, data.size)
        targetLine.start()

        while (true) {
            val bytesRead = targetLine.read(data, 0, data.size)

            if (bytesRead > 0 && recognizer.acceptWaveForm(data, bytesRead)) {
                val result = recognizer.result
                println(result)
                if (result.contains("Hey Blossom", ignoreCase = true)) {
                    message = result
                }
            }
        }
    }
}