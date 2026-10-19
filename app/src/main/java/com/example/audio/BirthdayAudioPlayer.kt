package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class BirthdayAudioPlayer {

    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _visualizerAmplitudes = MutableStateFlow(floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f))
    val visualizerAmplitudes: StateFlow<FloatArray> = _visualizerAmplitudes.asStateFlow()

    data class Note(val freq: Double, val durationMs: Int, val isPause: Boolean = false)

    // Frequencies
    companion object {
        const val C4 = 261.63
        const val D4 = 293.66
        const val E4 = 329.63
        const val F4 = 349.23
        const val G4 = 392.00
        const val A4 = 440.00
        const val Bb4 = 466.16
        const val B4 = 493.88
        const val C5 = 523.25
        const val D5 = 587.33
        const val REST = 0.0
    }

    private fun getMelody(track: MusicTrack): List<Note> {
        val baseMelody = listOf(
            Note(C4, 250), Note(C4, 250), Note(D4, 500), Note(C4, 500), Note(F4, 500), Note(E4, 900),
            Note(REST, 200, true),
            Note(C4, 250), Note(C4, 250), Note(D4, 500), Note(C4, 500), Note(G4, 500), Note(F4, 900),
            Note(REST, 200, true),
            Note(C4, 250), Note(C4, 250), Note(C5, 500), Note(A4, 500), Note(F4, 450), Note(E4, 450), Note(D4, 800),
            Note(REST, 200, true),
            Note(Bb4, 250), Note(Bb4, 250), Note(A4, 500), Note(F4, 500), Note(G4, 500), Note(F4, 1100),
            Note(REST, 400, true)
        )

        return when (track) {
            MusicTrack.CLASSIC_BDAY -> baseMelody
            MusicTrack.MUSIC_BOX -> baseMelody.map { Note(it.freq * 1.5, (it.durationMs * 0.9).toInt(), it.isPause) }
            MusicTrack.CELEBRATION_POP -> baseMelody.map { Note(it.freq, (it.durationMs * 0.75).toInt(), it.isPause) }
            MusicTrack.ROMANTIC_PIANO -> baseMelody.map { Note(it.freq * 0.75, (it.durationMs * 1.2).toInt(), it.isPause) }
            MusicTrack.PERSONALIZED_SONG -> {
                // An extra celebratory fanfare preceding the melody
                listOf(
                    Note(C4, 150), Note(E4, 150), Note(G4, 200), Note(C5, 400),
                    Note(REST, 150, true)
                ) + baseMelody
            }
        }
    }

    fun play(track: MusicTrack = MusicTrack.CLASSIC_BDAY, loop: Boolean = true) {
        stop()

        playbackJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()
                _isPlaying.value = true

                val melody = getMelody(track)

                while (isActive) {
                    for (note in melody) {
                        if (!isActive) break

                        if (note.isPause || note.freq <= 0.0) {
                            _visualizerAmplitudes.value = floatArrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f)
                            delay(note.durationMs.toLong())
                        } else {
                            val samples = generateAudioSamples(note.freq, note.durationMs, track)
                            audioTrack?.write(samples, 0, samples.size)

                            // Update visualizer state
                            val amp1 = (0.3f + (sin(note.freq) * 0.6f).toFloat()).coerceIn(0.15f, 1.0f)
                            val amp2 = (0.5f + (sin(note.freq * 1.5) * 0.45f).toFloat()).coerceIn(0.2f, 1.0f)
                            val amp3 = (0.7f + (sin(note.freq * 0.7) * 0.25f).toFloat()).coerceIn(0.25f, 1.0f)
                            val amp4 = (0.4f + (sin(note.freq * 2.0) * 0.5f).toFloat()).coerceIn(0.15f, 1.0f)
                            val amp5 = (0.6f + (sin(note.freq * 1.2) * 0.35f).toFloat()).coerceIn(0.2f, 1.0f)
                            _visualizerAmplitudes.value = floatArrayOf(amp1, amp2, amp3, amp4, amp5)

                            delay(note.durationMs.toLong())
                        }
                    }

                    if (!loop) {
                        break
                    }
                }
            } catch (_: Exception) {
            } finally {
                _isPlaying.value = false
                _visualizerAmplitudes.value = floatArrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f)
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
                audioTrack = null
            }
        }
    }

    private fun generateAudioSamples(freq: Double, durationMs: Int, track: MusicTrack): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
        val buffer = ShortArray(totalSamples)

        val attackSamples = (sampleRate * 0.02).toInt().coerceAtLeast(1)
        val decaySamples = (totalSamples * 0.7).toInt().coerceAtLeast(1)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            // Envelope ADSR
            val envelope = when {
                i < attackSamples -> i.toDouble() / attackSamples
                i > totalSamples - decaySamples -> {
                    val remaining = totalSamples - i
                    remaining.toDouble() / decaySamples
                }
                else -> 1.0
            }

            // Timbre synthesis based on track
            val raw = when (track) {
                MusicTrack.MUSIC_BOX -> {
                    // Pure chime sine with high harmonic
                    0.7 * sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)
                }
                MusicTrack.ROMANTIC_PIANO -> {
                    // Warmer rich harmonic
                    0.6 * sin(2.0 * PI * freq * t) +
                            0.25 * sin(4.0 * PI * freq * t) +
                            0.15 * sin(6.0 * PI * freq * t)
                }
                MusicTrack.CELEBRATION_POP -> {
                    // Crisp upbeat square-softened wave
                    0.5 * sin(2.0 * PI * freq * t) +
                            0.3 * sin(6.0 * PI * freq * t) +
                            0.2 * sin(8.0 * PI * freq * t)
                }
                else -> {
                    // Classic Happy Birthday bright acoustic bell
                    0.65 * sin(2.0 * PI * freq * t) +
                            0.25 * sin(4.0 * PI * freq * t) +
                            0.10 * sin(8.0 * PI * freq * t)
                }
            }

            val sampleValue = (raw * envelope * 18000.0).toInt().coerceIn(-32767, 32767)
            buffer[i] = sampleValue.toShort()
        }
        return buffer
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
        _isPlaying.value = false
        _visualizerAmplitudes.value = floatArrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f)
    }
}
