# Palli Sahayak Android App

## Stack (non-negotiable)
- Kotlin only. No Java.
- Jetpack Compose (no XML layouts)
- MVVM + UDF: ViewModel -> StateFlow -> Composable
- Hilt for DI
- Room + SQLCipher + Kotlin Coroutines (no RxJava)
- Retrofit + OkHttp for network
- Coil for images
- Min SDK 26, target SDK 35

## Architecture rules
- One feature = one Gradle module (feature/auth/, feature/home/ etc.)
- ViewModels own no Android framework types except SavedStateHandle
- Repository pattern: no direct Room/Retrofit calls from ViewModels
- All suspend fns run in viewModelScope or dedicated CoroutineScope
- Sealed class for UI state: Loading | Success | Error
- Single Source of Truth: Room is the SSOT. Network writes go to Room first.
- All clinical logic is server-side. The app is a presentation + caching layer.

## Code style
- No magic numbers -- use named constants or resource tokens
- Composables must be preview-able (no side effects in preview)
- Functions > 40 lines should be split

## Voice-first design rules
- Voice button must be accessible from every main screen
- Every text response must have a TTS auto-play option
- Emergency detection runs locally before any server call
- Offline mode must provide cached responses, never a blank screen

## Build
- Run ./gradlew build before declaring a task done
- Run ./gradlew lint and fix all warnings before PR
- APK size must stay under 25MB

## Backend
- Backend repo: https://github.com/inventcures/rag_gci
- Backend docs: https://deepwiki.com/inventcures/rag_gci
- Mobile API: /api/mobile/v1/* endpoints (JWT-authenticated)
