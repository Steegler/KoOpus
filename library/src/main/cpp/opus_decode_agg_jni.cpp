// File: library/src/main/cpp/opus_decode_agg_jni.cpp
#include <jni.h>
#include <opus.h>
#include <android/log.h>

// Helper: fetch private long field "nativeHandle" from Kotlin OpusDecoder
static opus_decoder* get_dec(JNIEnv* env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "nativeHandle", "J");
    if (!fid) return nullptr;
    jlong h = env->GetLongField(thiz, fid);
    return reinterpret_cast<opus_decoder*>((intptr_t)h);
}

static void throwISE(JNIEnv* env, const char* msg) {
    jclass ex = env->FindClass("java/lang/IllegalStateException");
    if (ex) env->ThrowNew(ex, msg);
}

extern "C" {

JNIEXPORT jint JNICALL
Java_com_steegler_koopus_OpusDecoder_decodeAggregated(JNIEnv* env, jobject thiz,
    jbyteArray payload, jint length, jint maxFrameSize, jshortArray outPcm) {

    auto* dec = get_dec(env, thiz);
    if (!dec) { throwISE(env, "decoder null"); return -1; }

    jbyte* pay = (jbyte*)env->GetPrimitiveArrayCritical(payload, nullptr);
    jshort* pcm = (jshort*)env->GetPrimitiveArrayCritical(outPcm, nullptr);
    if (!pay || !pcm) { if (pay) env->ReleasePrimitiveArrayCritical(payload, pay, 0);
                        if (pcm) env->ReleasePrimitiveArrayCritical(outPcm, pcm, 0);
                        throwISE(env, "OOM"); return -1; }

    const unsigned char* data = (const unsigned char*)pay;
    int toc = 0, frames = 0;
    const unsigned char* fptrs[48]; // upper bound across modes
    opus_int16 sizes[48];

    // Parse packet into individual frames
    int rc = opus_packet_parse(data, length, &toc, fptrs, sizes, &frames);
    if (rc < 0) { env->ReleasePrimitiveArrayCritical(outPcm, pcm, 0);
                  env->ReleasePrimitiveArrayCritical(payload, pay, 0);
                  throwISE(env, "opus_packet_parse failed");
                  return -1; }

    int samplesTotal = 0;
    for (int i = 0; i < frames; ++i) {
        int decSamples = opus_decode(dec, fptrs[i], sizes[i], pcm + samplesTotal, maxFrameSize, 0);
        if (decSamples < 0) { env->ReleasePrimitiveArrayCritical(outPcm, pcm, 0);
                              env->ReleasePrimitiveArrayCritical(payload, pay, 0);
                              throwISE(env, "opus_decode failed"); return -1; }
        samplesTotal += decSamples;
    }

    env->ReleasePrimitiveArrayCritical(outPcm, pcm, 0);
    env->ReleasePrimitiveArrayCritical(payload, pay, 0);
    return samplesTotal;
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusDecoder_reset(JNIEnv* env, jobject thiz) {
    auto* dec = get_dec(env, thiz);
    if (!dec) { throwISE(env, "decoder null"); return; }
    opus_decoder_ctl(dec, OPUS_RESET_STATE);
}

} // extern "C"
