package actions

import app.Commands
import com.google.cloud.texttospeech.v1.*
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import javax.sound.sampled.*

object TTS {
    init {

        Commands.addCommand("response") {
            val text = it[0] ?: "error"
            println("Response: $text")

            val tts = text.replace("\\n", " ")

            // Instantiates a client
            TextToSpeechClient.create().use { textToSpeechClient ->

                val input = SynthesisInput.newBuilder().setText(tts).build()
                val voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .setName("en-US-Wavenet-H")
                    .build()
                val audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build()

                val response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)
                val audioContents = response.audioContent?.toByteArray() ?: ByteArray(0)

                val audioInputStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(audioContents))

                // Get the audio format from the audio input stream
                val audioFormat = audioInputStream.format

                // Create a data line for the audio format
                val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)

                // Open the data line and start playing the audio
                val sourceDataLine = AudioSystem.getLine(dataLineInfo) as SourceDataLine
                sourceDataLine.open(audioFormat)
                sourceDataLine.start()

                // Create a buffer to read the audio data
                val bufferSize = (audioFormat.sampleRate * audioFormat.frameSize).toInt()
                val buffer = ByteArray(bufferSize)

                // Read the audio data from the audio input stream and write it to the data line buffer
                var bytesRead = audioInputStream.read(buffer, 0, buffer.size)
                while (bytesRead != -1) {
                    sourceDataLine.write(buffer, 0, bytesRead)
                    bytesRead = audioInputStream.read(buffer, 0, buffer.size)
                }

                // Stop and close the data line and audio input stream
                sourceDataLine.drain()
                sourceDataLine.stop()
                sourceDataLine.close()
                audioInputStream.close()
            }
        }
    }
}