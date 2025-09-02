package com.steegler.koopus

/**
 * Instance-scoped tuner that applies Opus CTLs to an existing OpusEncoder instance.
 * We assume OpusEncoder has a private long 'nativeHandle' field (unchanged).
 *
 * Usage:
 *   val enc = OpusEncoder.create(...)
 *   val tuner = OpusTuning(enc)
 *   tuner.setBitrate(24_000)
 *   ...
 */
class OpusTuning(private val encoder: Any) {
    // We bind native methods as instance methods and look up encoder.nativeHandle in JNI.
    external fun setBitrate(bps: Int)
    external fun setComplexity(level: Int)         // 0..10
    external fun setMaxBandwidth(hz: Int)          // 8000/16000/24000/48000
    external fun setDTX(enabled: Boolean)
    external fun setInbandFEC(enabled: Boolean)
    external fun setCBR(enabled: Boolean)          // true -> VBR off
    external fun setVBRConstraint(enabled: Boolean)
    external fun setSignalVoice()
    external fun setPacketLossPercent(percent: Int)

    companion object { init { System.loadLibrary("koopus") } }
}

data class OpusProfile(
    val bitrate: Int,
    val bandwidthHz: Int,
    val complexity: Int,
    val dtx: Boolean,
    val fec: Boolean,
    val cbr: Boolean,
    val vbrConstraint: Boolean = true,
    val packetLossHint: Int = 5
)

object OpusProfiles {
    val Default = OpusProfile(24_000, 16_000, 5, dtx=false, fec=true, cbr=true)
    val LowPower = OpusProfile(16_000, 16_000, 3, dtx=false, fec=true, cbr=true)
    val HighQuality = OpusProfile(32_000, 48_000, 7, dtx=false, fec=true, cbr=true)
}
