package com.pallisahayak.core.voice

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.pallisahayak.core.common.constants.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor() {

    private var audioTrack: AudioTrack? = null

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    suspend fun play(audioData: ByteArray) = withContext(Dispatchers.IO) {
        stop()

        val pcmData = if (isWavFile(audioData)) {
            audioData.copyOfRange(44, audioData.size)
        } else {
            audioData
        }

        val bufferSize = AudioTrack.getMinBufferSize(
            24000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(24000)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        _playbackState.value = PlaybackState.PLAYING

        audioTrack?.write(pcmData, 0, pcmData.size)

        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        _playbackState.value = PlaybackState.IDLE
    }

    fun stop() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        _playbackState.value = PlaybackState.IDLE
    }

    private fun isWavFile(data: ByteArray): Boolean =
        data.size > 44 &&
            data[0] == 'R'.code.toByte() &&
            data[1] == 'I'.code.toByte() &&
            data[2] == 'F'.code.toByte() &&
            data[3] == 'F'.code.toByte()

    enum class PlaybackState { IDLE, PLAYING }
}
