
# KoOpus — Android Opus Codec Wrapper

KoOpus is a lightweight Android library that wraps the native [Opus codec](https://opus-codec.org/) with Kotlin-friendly interfaces. It enables high-quality audio compression and decompression for real-time voice transmission, ideal for voice chat, push-to-talk, or walkie-talkie style applications.

## Features
- ✅ Encode 16-bit PCM to Opus
- ✅ Decode Opus back to PCM
- ✅ JNI-powered native bridge to latest `libopus 1.5.2`
- ✅ Minimal Kotlin API: `OpusEncoder`, `OpusDecoder`
- ✅ Includes sample app (`koopusdemo`) for testing

## Getting Started

### 1. Clone and build the native library
```bash
git clone https://github.com/yourname/KoOpus.git
cd KoOpus
./build_opus_android.sh
```
This builds static .a files for Android ABIs (arm64-v8a, armeabi-v7a, x86, x86_64).

### 2. Import into Android Studio

Open the KoOpus folder in Android Studio. It contains two modules:
	•	library → reusable codec module
	•	koopusdemo → sample demo app (record → encode → decode → play)

### 3. Using the Library
```kotlin
val encoder = OpusEncoder
encoder.create(sampleRate = 16000, channels = 1, application = 2049)
val encoded: ByteArray = encoder.encode(pcmShortArray, frameSize = 320)
encoder.destroy()

val decoder = OpusDecoder
decoder.create(sampleRate = 16000, channels = 1)
val decoded: ShortArray = decoder.decode(encoded, encoded.size, frameSize = 320)
decoder.destroy()
```

# Demo App

The koopusdemo app provides:
•	🎙 Record from mic
•	📦 Encode with Opus
•	🧠 Store in memory
•	🔊 Decode and play via AudioTrack

# License

KoOpus itself is BSD 2-Clause License. It bundles Opus codec from Xiph.org, which is released under BSD-2-Clause license.

⸻

# Credits
•	Opus Codec by Xiph.org
•	Android NDK + AudioTrack/AudioRecord
•	Kotlin/Jetpack Compose for sample UI

---
Let me know if you'd like me to tweak it for public or internal GitHub hosting.