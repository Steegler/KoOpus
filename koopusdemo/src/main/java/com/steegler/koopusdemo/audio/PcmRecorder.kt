package com.steegler.koopusdemo.audio

import android.annotation.SuppressLint
import com.steegler.koopus.OpusEncoder
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class PcmRecorder(
    private val sampleRate: Int = 16000,
    private val channels: Int = 1,
    private val application: Int = 2049 // OPUS_APPLICATION_AUDIO
) {
    private val frameSize = sampleRate / 50 // 20ms
    private val isRecording = AtomicBoolean(false)
    private var audioRecord: AudioRecord? = null
    val encodedChunks = mutableListOf<ByteArray>()

    @SuppressLint("MissingPermission")
    fun start() {
        if (!OpusEncoder.create(sampleRate, channels, application)) {
            Log.e("PcmRecorder", "Failed to create Opus encoder")
            return
        }

        val channelConfig = if (channels == 1)
            AudioFormat.CHANNEL_IN_MONO else AudioFormat.CHANNEL_IN_STEREO

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            channelConfig,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        isRecording.set(true)
        audioRecord?.startRecording()

        Thread {
            val buffer = ShortArray(frameSize)
            while (isRecording.get()) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    val encoded = OpusEncoder.encode(buffer, read)
                    if (encoded != null) {
                        encodedChunks.add(encoded)
                    }
                }
            }
        }.start()
    }

    fun stop() {
        isRecording.set(false)
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        OpusEncoder.destroy()
    }
}