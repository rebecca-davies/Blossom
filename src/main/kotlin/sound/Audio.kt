package sound

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

val AUDIO_FORMAT = AudioFormat(44100F, 16, 1, true, false)
val LINE_INFO = DataLine.Info(TargetDataLine::class.java, AUDIO_FORMAT)