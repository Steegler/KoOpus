// File: library/src/main/cpp/opus_repacketizer_jni.cpp
#include <jni.h>
#include <opus.h>
#include <android/log.h>

static void throwISE(JNIEnv* env, const char* msg) {
    jclass ex = env->FindClass("java/lang/IllegalStateException");
    if (ex) env->ThrowNew(ex, msg);
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_steegler_koopus_OpusRepacketizer_nativeCreate(JNIEnv*, jclass) {
    return (jlong)(intptr_t)opus_repacketizer_create();
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusRepacketizer_nativeDestroy(JNIEnv*, jclass, jlong h) {
    if (h) opus_repacketizer_destroy(reinterpret_cast<OpusRepacketizer*>((intptr_t)h));
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusRepacketizer_nativeInit(JNIEnv*, jclass, jlong h) {
    auto* rp = reinterpret_cast<OpusRepacketizer*>((intptr_t)h);
    opus_repacketizer_init(rp);
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusRepacketizer_nativeCat(JNIEnv* env, jclass, jlong h, jbyteArray pkt, jint len) {
    auto* rp = reinterpret_cast<OpusRepacketizer*>((intptr_t)h);
    jbyte* p = (jbyte*)env->GetPrimitiveArrayCritical(pkt, nullptr);
    int rc = opus_repacketizer_cat(rp, (const unsigned char*)p, len);
    env->ReleasePrimitiveArrayCritical(pkt, p, 0);
    if (rc < 0) throwISE(env, "opus_repacketizer_cat failed");
}

JNIEXPORT jint JNICALL
Java_com_steegler_koopus_OpusRepacketizer_nativeOut(JNIEnv* env, jclass, jlong h, jbyteArray out, jint maxLen) {
    auto* rp = reinterpret_cast<OpusRepacketizer*>((intptr_t)h);
    jbyte* o = (jbyte*)env->GetPrimitiveArrayCritical(out, nullptr);
    opus_int32 rc = opus_repacketizer_out(rp, (unsigned char*)o, maxLen);
    env->ReleasePrimitiveArrayCritical(out, o, 0);
    if (rc < 0) { throwISE(env, "opus_repacketizer_out failed"); return -1; }
    return (jint)rc;
}

} // extern "C"
