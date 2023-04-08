package sound.`in`

import com.orctom.vad4j.VAD
import sound.AUDIO_FORMAT
import sound.LINE_INFO
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class Capture {

//    fun capture() : AudioInputStream? {
//        try {
//            val line = AudioSystem.getLine(LINE_INFO) as TargetDataLine
//            line.open(AUDIO_FORMAT)
//            line.start()
//
//            val out = ByteArrayOutputStream()
//            var byteCount = 0
//            val data = ByteArray(line.bufferSize / 5)
//            var listening = true
//            var vad = VAD()
//
//            while(listening) {
//                byteCount = line.read(data, 0, data.size)
//                out.write(data, 0, byteCount)
//                var isSilent = vad.isSilent(data)
//                if(isSilent) {
//                    out.write(data, 0, byteCount)
//                } else {
//                    Thread.sleep(500)
//                    if(vad.isSilent(data)) {
//                        vad.close()
//                        listening = false
//                    }
//                }
//            }
//
//            line.stop()
//            line.close()
//            val audioData = out.toByteArray()
//
//            return AudioInputStream(ByteArrayInputStream(audioData), AUDIO_FORMAT,
//                (audioData.size / AUDIO_FORMAT.frameSize).toLong()
//            )
//
//        } catch(e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    private fun hasSpeech(audioData: ByteArray) : Boolean {
//        return false;
//    }

}