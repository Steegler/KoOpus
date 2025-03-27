cmake_minimum_required(VERSION 3.10)
project(koopus)

set(CMAKE_CXX_STANDARD 17)

# ABI-specific prebuilt libopus.a path
set(OPUS_ROOT "${CMAKE_SOURCE_DIR}/src/main/cpp/prebuilt/${ANDROID_ABI}")

# Opus header path (e.g. KoOpus/opus/include/opus/opus.h)
set(OPUS_INCLUDE_DIR "${CMAKE_SOURCE_DIR}/../opus/include")

# Create our native libkoopus.so
add_library(koopus SHARED
        src/main/cpp/opus_wrapper.cpp
)

# Declare prebuilt Opus static library
add_library(opus STATIC IMPORTED)
set_target_properties(opus PROPERTIES
        IMPORTED_LOCATION "${OPUS_ROOT}/libopus.a"
)

# Ensure the compiler finds opus/opus.h
target_include_directories(koopus PRIVATE "${OPUS_INCLUDE_DIR}")

# Link against libopus and Android log
target_link_libraries(
        koopus
        opus
        log
)