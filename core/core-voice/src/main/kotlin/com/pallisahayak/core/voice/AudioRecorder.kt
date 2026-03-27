package com.pallisahayak.core.voice

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.pallisahayak.core.common.constants.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor() {

    private var audioRecord: AudioRecord? = null

    @Volatile
    private var isRecording = false

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude

    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    suspend fun record(): ByteArray = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            AppConstants.VOICE_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            AppConstants.VOICE_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
        )

        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(bufferSize)

        audioRecord?.startRecording()
        isRecording = true
        _recordingState.value = RecordingState.RECORDING

        while (isRecording && isActive) {
            val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: break
            if (bytesRead > 0) {
                outputStream.write(buffer, 0, bytesRead)
                _amplitude.value = calculateAmplitude(buffer, bytesRead)
            }
        }

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        _recordingState.value = RecordingState.IDLE
        _amplitude.value = 0f

        addWavHeader(outputStream.toByteArray())
    }

    fun stopRecording() {
        isRecording = false
    }

    private fun calculateAmplitude(buffer: ByteArray, size: Int): Float {
        var sum = 0L
        val samples = size / 2
        for (i in 0 until size - 1 step 2) {
            val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
            sum += sample.toLong() * sample.toLong()
        }
        val rms = Math.sqrt(sum.toDouble() / samples)
        return (rms / Short.MAX_VALUE).toFloat().coerceIn(0f, 1f)
    }

    private fun addWavHeader(pcmData: ByteArray): ByteArray {
        val totalSize = pcmData.size + 44
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalSize - 8)
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1)
        writeShort(header, 22, 1)
        writeInt(header, 24, AppConstants.VOICE_SAMPLE_RATE)
        writeInt(header, 28, AppConstants.VOICE_SAMPLE_RATE * 2)
        writeShort(header, 32, 2)
        writeShort(header, 34, 16)
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        writeInt(header, 40, pcmData.size)

        return header + pcmData
    }

    private fun writeInt(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xFF).toByte()
        data[offset + 1] = ((value shr 8) and 0xFF).toByte()
        data[offset + 2] = ((value shr 16) and 0xFF).toByte()
        data[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun writeShort(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xFF).toByte()
        data[offset + 1] = ((value shr 8) and 0xFF).toByte()
    }

    enum class RecordingState { IDLE, RECORDING }
}
