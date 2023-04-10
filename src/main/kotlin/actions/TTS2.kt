package actions

import app.Commands
import net.andrewcpu.elevenlabs.ElevenLabsAPI
import net.andrewcpu.elevenlabs.elements.voice.Voice
import net.andrewcpu.elevenlabs.elements.voice.VoiceSettings
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.*

object TTS2 {
    init {

        Commands.addCommand("response") {
            val text = it[0] ?: "error"
            println("Response: $text")

            val tts = text.replace("\\n", " ")

            ElevenLabsAPI.getInstance().setAPIKey("6b32dd7f761624fec6884a94b7653968")
            VoiceSettings.getDefaultVoiceSettings()

            val file = File("test.wav")
            Voice.get("21m00Tcm4TlvDq8ikWAM").generate("Hey cutie", file)
            val audioInputStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(file.readBytes()))

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