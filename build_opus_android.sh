#!/bin/bash

set -e

#######################################
# CONFIG
#######################################
OPUS_DIR="$(pwd)/opus"
BUILD_DIR="$(pwd)/build_opus"
OUTPUT_DIR="$(pwd)/library/src/main/cpp/prebuilt"
NDK_PATH="${ANDROID_NDK_HOME:-$HOME/Library/Android/sdk/ndk/25.1.8937393}"
TOOLCHAIN="$NDK_PATH/toolchains/llvm/prebuilt/$(uname -s | tr '[:upper:]' '[:lower:]')-x86_64"

ABIS=("armeabi-v7a" "arm64-v8a" "x86" "x86_64")

#######################################
# VALIDATION
#######################################
if [ ! -d "$NDK_PATH" ]; then
    echo "❌ ANDROID_NDK_HOME is not set or NDK not found at: $NDK_PATH"
    exit 1
fi

if [ ! -f "$OPUS_DIR/configure" ]; then
    echo "❌ Opus not configured. Run './autogen.sh' in the 'opus' folder first."
    exit 1
fi

#######################################
# CLEAN OUTPUT
#######################################
rm -rf "$BUILD_DIR" "$OUTPUT_DIR"
mkdir -p "$BUILD_DIR" "$OUTPUT_DIR"

#######################################
# BUILD LOOP
#######################################
for ABI in "${ABIS[@]}"; do
    echo "🔧 Building for $ABI..."

    case $ABI in
        armeabi-v7a)
            TARGET=armv7a-linux-androideabi
            HOST=arm-linux-androideabi
            API=21
            ;;
        arm64-v8a)
            TARGET=aarch64-linux-android
            HOST=aarch64-linux-android
            API=21
            ;;
        x86)
            TARGET=i686-linux-android
            HOST=i686-linux-android
            API=21
            ;;
        x86_64)
            TARGET=x86_64-linux-android
            HOST=x86_64-linux-android
            API=21
            ;;
    esac

    ABI_BUILD_DIR="$BUILD_DIR/$ABI"
    ABI_OUTPUT_DIR="$OUTPUT_DIR/$ABI"
    mkdir -p "$ABI_BUILD_DIR" "$ABI_OUTPUT_DIR"

    pushd "$ABI_BUILD_DIR"

    "$OPUS_DIR/configure" \
        --host="$HOST" \
        --prefix="$ABI_OUTPUT_DIR" \
        --disable-shared \
        --enable-static \
        --with-pic \
        CC="$TOOLCHAIN/bin/${TARGET}${API}-clang" \
        AR="$TOOLCHAIN/bin/llvm-ar" \
        RANLIB="$TOOLCHAIN/bin/llvm-ranlib"

    make -j$(getconf _NPROCESSORS_ONLN)
    make install

    # 🛠 Explicitly copy libopus.a to expected location
    FOUND_LIB="$(find "$ABI_OUTPUT_DIR" -name "libopus.a" | head -n1)"
    if [ -f "$FOUND_LIB" ]; then
        cp "$FOUND_LIB" "$ABI_OUTPUT_DIR/libopus.a"
        echo "✅ Copied libopus.a for $ABI"
    else
        echo "❌ Failed to find libopus.a for $ABI"
        exit 1
    fi

    popd
done

#######################################
# DONE
#######################################
echo ""
echo "✅ All ABI builds completed."
echo "📦 Static libs installed to:"
for ABI in "${ABIS[@]}"; do
    echo "   - $OUTPUT_DIR/$ABI/libopus.a"
done