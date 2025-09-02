package com.steegler.koopus


fun OpusTuning.apply(profile: OpusProfile) {
    setSignalVoice()
    setMaxBandwidth(profile.bandwidthHz)
    setBitrate(profile.bitrate)
    setComplexity(profile.complexity)
    setInbandFEC(profile.fec)
    setDTX(profile.dtx)
    setCBR(profile.cbr)
    setVBRConstraint(profile.vbrConstraint)
    setPacketLossPercent(profile.packetLossHint)
}
