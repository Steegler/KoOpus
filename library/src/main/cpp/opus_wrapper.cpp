#include <jni.h>
#include <opus.h>
#include <android/log.h>

#define LOG_TAG "KoOpus"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// === ENCODER ===

JNIEXPORT jlong JNICALL
Java_com_steegler_koopus_OpusEncoder_nativeCreateEncoder(
        JNIEnv*, jobject,
        jint sampleRate, jint channels, jint application) {
    int error;
    OpusEncoder* encoder = opus_encoder_create(sampleRate, channels, application, &error);
    if (error != OPUS_OK) {
        LOGE("Failed to create encoder: %s", opus_strerror(error));
        return 0;
    }
    return reinterpret_cast<jlong>(encoder);
}

JNIEXPORT jint JNICALL
Java_com_steegler_koopus_OpusEncoder_nativeEncode(
        JNIEnv* env, jobject,
        jlong encoderPtr, jshortArray pcm, jint frameSize, jbyteArray output) {

    auto* encoder = reinterpret_cast<OpusEncoder*>(encoderPtr);
    if (!encoder) return 0;

    jshort* pcmData = env->GetShortArrayElements(pcm, nullptr);
    jbyte* outData = env->GetByteArrayElements(output, nullptr);

    int result = opus_encode(
            encoder,
            pcmData,
            frameSize,
            reinterpret_cast<unsigned char*>(outData),
            env->GetArrayLength(output)
    );

    env->ReleaseShortArrayElements(pcm, pcmData, 0);
    env->ReleaseByteArrayElements(output, outData, 0);

    if (result < 0) {
        LOGE("Opus encode error: %s", opus_strerror(result));
    }

    return result;
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusEncoder_nativeDestroyEncoder(
        JNIEnv*, jobject, jlong encoderPtr) {
    if (encoderPtr != 0) {
        opus_encoder_destroy(reinterpret_cast<OpusEncoder*>(encoderPtr));
    }
}

// === DECODER ===

JNIEXPORT jlong JNICALL
Java_com_steegler_koopus_OpusDecoder_nativeCreateDecoder(
        JNIEnv*, jobject,
        jint sampleRate, jint channels) {

    int error;
    OpusDecoder* decoder = opus_decoder_create(sampleRate, channels, &error);
    if (error != OPUS_OK) {
        LOGE("Failed to create decoder: %s", opus_strerror(error));
        return 0;
    }
    return reinterpret_cast<jlong>(decoder);
}

JNIEXPORT jint JNICALL
Java_com_steegler_koopus_OpusDecoder_nativeDecode(
        JNIEnv* env, jobject,
        jlong decoderPtr,
        jbyteArray input, jint inputLen,
        jshortArray output, jint frameSize) {

    auto* decoder = reinterpret_cast<OpusDecoder*>(decoderPtr);
    if (!decoder) return 0;

    jbyte* inData = env->GetByteArrayElements(input, nullptr);
    jshort* outData = env->GetShortArrayElements(output, nullptr);

    int result = opus_decode(
            decoder,
            reinterpret_cast<unsigned char*>(inData),
            inputLen,
            outData,
            frameSize,
            0 // decode_fec = false
    );

    env->ReleaseByteArrayElements(input, inData, 0);
    env->ReleaseShortArrayElements(output, outData, 0);

    if (result < 0) {
        LOGE("Opus decode error: %s", opus_strerror(result));
    }

    return result;
}

JNIEXPORT void JNICALL
Java_com_steegler_koopus_OpusDecoder_nativeDestroyDecoder(
        JNIEnv*, jobject, jlong decoderPtr) {
    if (decoderPtr != 0) {
        opus_decoder_destroy(reinterpret_cast<OpusDecoder*>(decoderPtr));
    }
}

}