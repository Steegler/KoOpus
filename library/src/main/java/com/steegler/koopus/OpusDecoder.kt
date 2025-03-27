package com.steegler.koopus

object OpusDecoder {
    init {
        System.loadLibrary("koopus")
    }

    private var nativeHandle: Long = 0

    fun create(sampleRate: Int, channels: Int): Boolean {
        nativeHandle = nativeCreateDecoder(sampleRate, channels)
        return nativeHandle != 0L
    }

    fun decode(encoded: ByteArray, length: Int, frameSize: Int): ShortArray? {
        if (nativeHandle == 0L) return null

        val outputBuffer = ShortArray(frameSize * 2) // large enough for stereo
        val result = nativeDecode(nativeHandle, encoded, length, outputBuffer, frameSize)

        return if (result > 0) outputBuffer.copyOf(result) else null
    }

    fun destroy() {
        if (nativeHandle != 0L) {
            nativeDestroyDecoder(nativeHandle)
            nativeHandle = 0L
        }
    }

    private external fun nativeCreateDecoder(sampleRate: Int, channels: Int): Long
    private external fun nativeDecode(handle: Long, input: ByteArray, inputLen: Int, output: ShortArray, frameSize: Int): Int
    private external fun nativeDestroyDecoder(handle: Long)
}