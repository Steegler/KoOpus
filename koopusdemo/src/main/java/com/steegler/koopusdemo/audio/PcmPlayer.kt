package com.steegler.koopusdemo.audio

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.steegler.koopus.OpusDecoder
import java.util.concurrent.atomic.AtomicBoolean

class PcmPlayer(
    private val sampleRate: Int = 16000,
    private val channels: Int = 1,
    private val frameSize: Int = sampleRate / 50 // 20ms
) {
    private var audioTrack: AudioTrack? = null
    private val isPlaying = AtomicBoolean(false)

    @SuppressLint("NewApi")
    fun play(encodedChunks: List<ByteArray>) {
        if (!OpusDecoder.create(sampleRate, channels)) {
            Log.e("PcmPlayer", "Failed to create Opus decoder")
            return
        }

        val channelOut = if (channels == 1)
            AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            channelOut,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelOut)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        isPlaying.set(true)

        Thread {
            for (chunk in encodedChunks) {
                if (!isPlaying.get()) break
                val decoded = OpusDecoder.decode(chunk, chunk.size, frameSize)
                if (decoded != null) {
                    audioTrack?.write(decoded, 0, decoded.size)
                }
            }
            stop()
        }.start()
    }

    fun stop() {
        isPlaying.set(false)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        OpusDecoder.destroy()
    }
}