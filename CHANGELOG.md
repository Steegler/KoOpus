# Changelog

All notable changes to this project will be documented in this file.

## [0.1.0] - 2025-03-26
### Added
- Initial Kotlin wrapper for Opus 1.5.2 (via C++/JNI)
- `OpusEncoder` and `OpusDecoder` classes
- Android `AudioRecord` integration for real-time PCM capture
- `PcmRecorder` and `PcmPlayer` utilities
- Compose UI sample in `koopusdemo`
- Native build script for Android ABIs
- README and BSD-2-Clause LICENSE

### Notes
- Native Opus code built from [https://gitlab.xiph.org/xiph/opus](https://gitlab.xiph.org/xiph/opus) tagged `v1.5.2`
- Compatible with Android NDK 25+, Kotlin 1.9.20+, Jetpack Compose 1.5+

---

Future versions will track improvements and fixes in OpenPTT runtime integration.