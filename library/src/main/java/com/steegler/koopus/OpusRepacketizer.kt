package com.steegler.koopus

/**
 * Thin wrapper over Opus repacketizer to merge multiple encoded frames into one payload
 * (e.g., 2×20ms → 40ms), reducing packets/sec and radio wakeups.
 */
class OpusRepacketizer private constructor(private val handle: Long) {

    fun init(): OpusRepacketizer { nativeInit(handle); return this }
    fun cat(packet: ByteArray, length: Int = packet.size) = nativeCat(handle, packet, length)
    fun out(dest: ByteArray, maxLen: Int = dest.size): Int = nativeOut(handle, dest, maxLen)

    fun destroy() = nativeDestroy(handle)

    companion object {
        init { System.loadLibrary("koopus") }
        @JvmStatic fun create(): OpusRepacketizer = OpusRepacketizer(nativeCreate())
        @JvmStatic private external fun nativeCreate(): Long
        @JvmStatic private external fun nativeDestroy(handle: Long)
        @JvmStatic private external fun nativeInit(handle: Long)
        @JvmStatic private external fun nativeCat(handle: Long, packet: ByteArray, length: Int)
        @JvmStatic private external fun nativeOut(handle: Long, out: ByteArray, maxLen: Int): Int
    }
}
