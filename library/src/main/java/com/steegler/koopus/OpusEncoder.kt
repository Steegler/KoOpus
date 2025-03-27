package com.steegler.koopus

object OpusEncoder {
    init {
        System.loadLibrary("koopus")
    }

    private var nativeHandle: Long = 0

    fun create(sampleRate: Int, channels: Int, application: Int): Boolean {
        nativeHandle = nativeCreateEncoder(sampleRate, channels, application)
        return nativeHandle != 0L
    }

    fun encode(pcm: ShortArray, frameSize: Int): ByteArray? {
        if (nativeHandle == 0L) return null

        val outputBuffer = ByteArray(4000) // Max opus packet size
        val result = nativeEncode(nativeHandle, pcm, frameSize, outputBuffer)

        return if (result > 0) outputBuffer.copyOf(result) else null
    }

    fun destroy() {
        if (nativeHandle != 0L) {
            nativeDestroyEncoder(nativeHandle)
            nativeHandle = 0L
        }
    }

    private external fun nativeCreateEncoder(sampleRate: Int, channels: Int, application: Int): Long
    private external fun nativeEncode(handle: Long, pcm: ShortArray, frameSize: Int, output: ByteArray): Int
    private external fun nativeDestroyEncoder(handle: Long)
}
