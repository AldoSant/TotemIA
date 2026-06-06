# Real AI Mentor Chat Implementation Plan

> **For Hermes:** Resolve core conversation functionality before any layout work. Follow systematic debugging: prove backend, prove app auth, then improve mentor behavior.

**Goal:** Make the chapter chat consume real AI responses as a mentor/guide instead of repeatedly falling back to local programmed copy.

**Architecture:** The Android chapter screen should continue using `EpisodeViewModel → JourneyRepository.interactWithJourney(...) → POST /totem/ask`. The backend `/totem/ask` is the real AI endpoint and calls the OpenClaw/LLM gateway. The APK must include `BuildConfig.TOTEM_API_KEY` via GitHub Secret/local build env so the OkHttp interceptor sends `X-Totem-Api-Key`.

**Tech Stack:** Android Kotlin/Compose, Hilt, Retrofit/OkHttp, FastAPI backend at `/var/www/totemia-backend`, GitHub Actions secrets, OpenClaw gateway.

---

## Evidence

- `https://veredasinc.com.br/totemia/health` returns `200 {"status":"ok"}`.
- Public `POST /totemia/chat` returns `404`; this is not the active endpoint.
- Public `POST /totemia/totem/ask` without key returns `401 Invalid Totem API key`.
- Server-side authenticated test of `POST http://127.0.0.1:8000/totem/ask` returned `200` with model `openai/gpt-5.4-mini` and a contextual mentor-style answer.
- Android `EpisodeViewModel.handleUserReflection()` calls `repository.interactWithJourney()`, and on failure calls `buildLocalChapterReply()`. This is the repeated programmed answer the user is seeing.
- `.github/workflows/build.yml` already reads `${{ secrets.TOTEM_API_KEY || vars.TOTEM_API_KEY }}` and writes it to `local.properties`, but GitHub was previously missing the secret.

## Root Cause

The real IA backend works when authenticated. The distributed APK is built without `TOTEM_API_KEY`, so authenticated `/totem/ask` calls fail and the app falls back to local programmed copy.

## Acceptance Criteria

1. GitHub repository has `TOTEM_API_KEY` configured as a secret without exposing the value in logs.
2. CI build embeds a non-empty `BuildConfig.TOTEM_API_KEY`.
3. New APK sends `X-Totem-Api-Key` and receives real `/totem/ask` responses.
4. Backend prompt produces concise PT-BR mentor/guide responses:
   - contextual to journey/chapter/user input;
   - no psychological diagnosis;
   - one practical next step;
   - at most one reflective question;
   - good for TTS.
5. Fallback remains available but is not the normal path.
6. Local validation passes: `./gradlew clean test lintDebug assembleDebug --no-daemon --stacktrace`.
7. GitHub Actions passes and release APK is published as `v1.0.4`.

---

## Task 1: Configure GitHub Secret

**Objective:** Make CI builds authenticate against the real Totem backend.

**Files/Systems:**
- GitHub secret: `TOTEM_API_KEY`
- Source of truth: `/var/www/totemia-backend/.env` key `TOTEM_API_KEYS`

**Steps:**
1. Read `TOTEM_API_KEYS` on the server without printing it.
2. Pipe first configured key directly to `gh secret set TOTEM_API_KEY`.
3. Verify only by listing secret names, never value.

**Verification:**
- `gh secret list` includes `TOTEM_API_KEY`.

---

## Task 2: Add Build-Time Guard

**Objective:** CI should clearly prove whether the APK was built with a key, without exposing the key.

**Files:**
- Modify: `.github/workflows/build.yml`

**Steps:**
1. Keep current secret injection.
2. Add a safe check that fails or warns if key is absent for release-producing workflow.
3. Do not echo the key.

**Recommended behavior:**
- For the artifact/release workflow, fail if `TOTEM_API_KEY` is empty because core AI is a primary feature.

**Verification:**
- CI log says key is configured without showing value.

---

## Task 3: Improve Backend Mentor Prompt

**Objective:** Make real AI responses feel like a mentor/guide, not generic chatbot output.

**Files/Systems:**
- Modify on server: `/var/www/totemia-backend/main.py`
- Function: `build_system_prompt()`
- Function: `build_user_prompt()`

**Prompt Rules:**
- Always answer in PT-BR.
- Be warm, clear, practical, and concise.
- Use journey and chapter context.
- Do not diagnose, prescribe treatment, or pretend to be therapist/doctor.
- Avoid generic motivational speech.
- Prefer 2-5 short paragraphs or bullets.
- Include one concrete micro-action.
- Ask at most one follow-up question.
- Make response suitable for spoken TTS.

**Verification:**
- Authenticated server test returns a non-template answer tailored to the user's text.

---

## Task 4: Android Version Bump and Optional Fallback Copy

**Objective:** Prepare release `v1.0.4` and keep fallback graceful.

**Files:**
- Modify: `app/build.gradle.kts`
- Modify: `.github/workflows/build.yml`
- Optional modify: `app/src/main/java/com/totem/ia/ui/EpisodeViewModel.kt`

**Steps:**
1. Bump `versionCode` to `5` and `versionName` to `1.0.4`.
2. Change artifact name to `TotemIA-v1.0.4-debug.apk`.
3. If changing fallback, make it explicitly a temporary guided reflection rather than pretending to be AI.

**Verification:**
- Local build outputs v1.0.4 APK.

---

## Task 5: Validate, Commit, Push, Release

**Objective:** Publish a working APK with real AI chat.

**Commands:**
```bash
export JAVA_HOME=/home/annaa/dev/tools/jdk17-linux
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean test lintDebug assembleDebug --no-daemon --stacktrace
```

**Then:**
1. Commit changes.
2. Push to `main`.
3. Wait for GitHub Actions.
4. Download CI artifact.
5. Publish GitHub Release `v1.0.4`.
6. Sync VPS repo if needed.

**Verification:**
- GitHub Actions success.
- APK release asset exists.
- APK size/hash recorded.
- Server `/totem/ask` remains healthy with real AI.
