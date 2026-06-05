# TotemIA Chapter Experience Product Pass Implementation Plan

> **For Hermes:** Implement as one coherent product pass, not isolated symptom patches.

**Goal:** Make the in-chapter journey experience feel like a calm guided session with controllable narration, readable layout, reliable microphone flow, and online-first IA behavior with graceful fallback.

**Architecture:** Treat `EpisodePlayerScreen` as a guided session screen with three coordinated subsystems: narration/player state, conversational IA state, and voice input state. `EpisodeViewModel` becomes the session orchestrator; managers remain low-level wrappers for TTS, speech recognition, and ambient sound.

**Tech Stack:** Android Kotlin, Jetpack Compose Material3, Hilt ViewModel, Android TextToSpeech, SpeechRecognizer, Media3 ExoPlayer, Retrofit/OkHttp.

---

## Product Diagnosis

### Current experience problem

The chapter screen currently looks like a technical chat/visualizer screen, not a guided chapter experience. The orb takes too much visual priority, the action button is oversized, body text is small/low contrast, and the user has no clear control over narration.

### Audio problem

There is no real "chapter audio player". The chapter narration is Android `TextToSpeech`; background ambience is `SoundscapeManager`/ExoPlayer. The UI has no pause/resume control for TTS narration. `TextToSpeech` does not expose true pause/resume, so the correct implementation is a small narration controller in `EpisodeViewModel`: split script into segments, track segment index, stop on pause, resume from the current segment.

### IA problem

The APK is currently built without `TOTEM_API_KEY`; `local.properties` has no key and GitHub `gh secret list` shows no TOTEM secret. The online endpoint is healthy, but authenticated calls will fail without a key. Previous fallback copy exposed this implementation detail to the user: "Estou sem resposta online... modo local". That is poor product UX. The UI should be online-first; if offline/unauthorized, fallback should be transparent and phrased as a normal guided reflection, while internally logging/debugging online status.

### Microphone problem

Runtime permission was added, but the flow is still incomplete:
- listening can start while narration/TTS is speaking;
- speech errors are written to `spokenText` but the chapter UI does not display them as status;
- no clear "ouvindo... toque para parar" status;
- no lifecycle-aware cancellation on stage changes beyond ViewModel cleanup;
- partial transcription is not surfaced in the episode input.

---

## Acceptance Criteria

1. User can pause and resume chapter narration from inside the chapter.
2. Starting microphone pauses/stops narration first so TTS does not compete with recognition.
3. The chapter screen is more readable: larger body text, calmer hierarchy, less giant orb, smaller mic/play action, better spacing.
4. The user sees clear session states: Narrando, Pausado, Ouvindo, Pensando, Pronto.
5. The IA never says "não tenho conexão online" to the user unless explicitly useful; fallback copy feels intentional.
6. If `TOTEM_API_KEY` is absent, build still works, but product copy remains polished.
7. Local validation passes: `./gradlew clean test lintDebug assembleDebug --no-daemon --stacktrace`.
8. GitHub Actions passes and APK is published as a new release.

---

## Implementation Tasks

### Task 1: Add narration/session state to ViewModel

**Files:**
- Modify: `app/src/main/java/com/totem/ia/ui/EpisodeViewModel.kt`

**Steps:**
1. Add state fields: `isNarrationPaused`, `isNarrationPlaying`, `narrationSegmentIndex`, `narrationSegments`, `sessionStatus`, `partialSpeechText`, `voiceStatusMessage`.
2. Replace single `ttsManager.speak(script)` with segment-based narration.
3. On TTS completion, advance segment; when finished, start reflection.
4. Implement `toggleNarrationPlayback()`, `pauseNarration()`, `resumeNarration()`.
5. Ensure microphone start pauses narration first.

### Task 2: Improve TTS manager callback semantics

**Files:**
- Modify: `app/src/main/java/com/totem/ia/tts/TextToSpeechManager.kt`

**Steps:**
1. Add optional `onSpeechStarted` if needed.
2. Keep `stop()` as the pause primitive.
3. Ensure `onDone/onError` callbacks are safe for ViewModel orchestration.

### Task 3: Make SoundscapeManager controllable

**Files:**
- Modify: `app/src/main/java/com/totem/ia/audio/SoundscapeManager.kt`

**Steps:**
1. Add `pause()`, `resume()`, `setDucked(Boolean)` semantics.
2. Avoid releasing player when merely pausing the session.
3. Keep `stop()` for exit cleanup.

### Task 4: Improve SpeechInputManager state and errors

**Files:**
- Modify: `app/src/main/java/com/totem/ia/voice/SpeechInputManager.kt`

**Steps:**
1. Add `errorText`/`statusText` StateFlow.
2. Clear errors on start.
3. Use more user-friendly error strings.
4. Cancel/recreate recognizer after terminal errors if needed.

### Task 5: Redesign EpisodePlayerScreen composition

**Files:**
- Modify: `app/src/main/java/com/totem/ia/ui/EpisodePlayerScreen.kt`

**Steps:**
1. Reduce orb size and visual dominance.
2. Add a compact narration control card: title, status, pause/resume button.
3. Increase body/helper text sizes and contrast.
4. Reduce mic button from 72dp to ~52dp.
5. Show partial transcript/status while listening.
6. Use `imePadding()` and `navigationBarsPadding()` around input.
7. Keep actions visible but not overwhelming.

### Task 6: Polish IA fallback copy

**Files:**
- Modify: `app/src/main/java/com/totem/ia/ui/EpisodeViewModel.kt`

**Steps:**
1. Remove "sem resposta online" / "modo local" user-facing copy.
2. Create warm guided reflection fallback: acknowledge user, connect to chapter objective, ask one concrete next question.
3. Optionally append debug-only log, not visible message.

### Task 7: Verify online IA configuration boundary

**Files:**
- Inspect only unless user provides/authorizes key:
  - `local.properties`
  - GitHub secret `TOTEM_API_KEY`
  - `.github/workflows/build.yml`

**Steps:**
1. Confirm whether key exists locally/CI without exposing value.
2. If missing, report clearly: online IA cannot work in distributed APK until `TOTEM_API_KEY` is configured as GitHub secret or equivalent backend session token architecture is introduced.
3. Do not hardcode secrets.

### Task 8: Validate and release

**Commands:**
```bash
export JAVA_HOME=/home/annaa/dev/tools/jdk17-linux
./gradlew clean test lintDebug assembleDebug --no-daemon --stacktrace
```

Then bump version, commit, push, wait for Actions, download APK back, publish GitHub Release.
