# 💳 MonzoBank — Fintech Banking App (Kotlin + Jetpack Compose)

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Dagger_Hilt-009688?logo=dagger&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit_API-3DDC84?logo=android&logoColor=white)
![Tests](https://img.shields.io/badge/Unit_Tests-28_passing-brightgreen?logo=junit5&logoColor=white)

A **Monzo-inspired fintech banking app**: secure auth, accounts, cards, payments (P2P / Bill / Bulk / QR) — built with **MVVM + Clean Architecture**, documented build-in-public on LinkedIn.

> Portfolio project for **Graduate Android Engineer (Fintech)** roles — e.g. Revolut Graduate Programme 2027: Software Engineer (Android).

![Platform Banner](Monzo_Bank_App_Banner.png)

## 👀 Quick scan (30 seconds)

| What you want | Proof in this repo |
|---|---|
| Kotlin + Jetpack Compose, Material 3 | 60+ Compose screens in `presentation/ui/` |
| Coroutines & Flow, MVVM | 8 ViewModels with `StateFlow` + `viewModelScope` |
| Dagger Hilt + Retrofit + Room | `di/NetworkModule.kt`, `di/DatabaseModule.kt`, 10 Retrofit APIs, Room cache |
| Fintech domain | Auth, cards (freeze/block/PIN), P2P / bill / bulk / QR payments |
| Testing mindset (TDD) | 28 JUnit4 tests in `app/src/test/` |
| Ships & communicates | 8-part LinkedIn demo series + screenshots below |

Scale: **~500 Kotlin files · 60+ screens · 10 API services · 70+ repositories · 28 tests.**

## 📱 Screenshots

<table>
  <tr>
    <td align="center"><img src="Part_1.png" width="200"/><br/>Secure Login</td>
    <td align="center"><img src="Part_2.png" width="200"/><br/>Dashboard</td>
    <td align="center"><img src="Part_3.png" width="200"/><br/>Accounts</td>
    <td align="center"><img src="Part_4.png" width="200"/><br/>Card Controls</td>
  </tr>
  <tr>
    <td align="center"><img src="Part_5.png" width="200"/><br/>Bill Payments</td>
    <td align="center"><img src="Part_6.png" width="200"/><br/>Bulk Payments</td>
    <td align="center"><img src="Part_7.png" width="200"/><br/>P2P Transfer</td>
    <td align="center"><img src="Part_8.png" width="200"/><br/>QR Payments</td>
  </tr>
</table>

## ✨ Features

- **🔐 Auth:** login/register, validation, JWT via `TokenManager`, auth/error network interceptors
- **📊 Dashboard:** balance, transactions, API + Room sync
- **🏦 Accounts:** multi-account, create, offline cache
- **💳 Cards:** list, freeze/block, PIN, limits
- **💸 Payments:** bills, P2P, requests — validated success/error states
- **📦 Bulk:** batch payments, per-item status
- **📷 QR:** CameraX scan → instant pay

## ⚙️ Stack fit (Revolut Android → this project)

| Revolut asks | Here |
|---|---|
| Kotlin, Android SDK, Compose | ✅ 100% Kotlin, Compose + Material 3, Navigation, CameraX |
| Coroutines, Dagger, Retrofit, MVVM | ✅ `suspend` APIs, Hilt modules, Retrofit+OkHttp+Moshi, ViewModel+Repository |
| TDD | ✅ 28 JUnit4 tests (`InputValidationTest` 15 + `SecurityUtilsTest` 13) — `./gradlew :app:testDebugUnitTest` |
| RxJava | 🛠️ Roadmap (legacy-stream interop) |

+ Room offline cache, LazyColumn + hoisted `UiState` for smooth recomposition.

## 🏗️ Review in 5 minutes

```
Compose UI → ViewModel (StateFlow) → UseCase → Repository → Retrofit / Room
```

Start here:
1. `di/NetworkModule.kt` — Retrofit + OkHttp + Moshi wiring
2. `data/remote/api/AuthApiService.kt` — `suspend` API style
3. `presentation/viewmodel/AuthViewModel.kt` — StateFlow + validation
4. `utils/InputValidation.kt` + `app/src/test/` — tested fintech validators
5. `data/local/MonzoBankDatabase.kt` — Room cache

## 🎥 Build in public (LinkedIn)

[Part 1 — Login](https://www.linkedin.com/feed/update/urn:li:activity:7441542299500847104/?originTrackingId=gtuPAKLjQqKIqumK3DTlow%3D%3D) ·
[Part 2 — Dashboard](https://www.linkedin.com/feed/update/urn:li:activity:7443314840829501440/?originTrackingId=J5ZfMi97Q6uE5kbn9SMANg%3D%3D) ·
[Part 3 — Accounts](https://www.linkedin.com/feed/update/urn:li:activity:7444387956733149185/?originTrackingId=2lhvKzetTPik22xpi5DNeA%3D%3D) ·
[Part 4 — Cards](https://www.linkedin.com/feed/update/urn:li:activity:7448778143902986240/?originTrackingId=SBKF5YB6TbWD8nXwlyFpsw%3D%3D) ·
[Part 5 — Bills](https://www.linkedin.com/feed/update/urn:li:activity:7449335579231834112/?originTrackingId=MGC6rDlgQZqZWnED9JK31A%3D%3D) ·
[Part 6 — Bulk](https://www.linkedin.com/feed/update/urn:li:activity:7449691663737876480/?originTrackingId=ZrRL4qCESK2BAV1fbPp5Ag%3D%3D) ·
[Part 7 — P2P](https://www.linkedin.com/feed/update/urn:li:activity:7450064159980834816/?originTrackingId=89J%2BMmv%2FQVWgzBkQIhuKAA%3D%3D) ·
[Part 8 — QR](https://www.linkedin.com/feed/update/urn:li:activity:7450422838294986752/?originTrackingId=YJN9Gt5OS9q8fXKhzSFGhg%3D%3D)

## 📦 Run

```bash
git clone https://github.com/Avi6855/MonzoBank.git
```

1. Open in Android Studio, let Gradle sync
2. Set `BASE_URL` in `di/NetworkModule.kt`
3. Run ▶️ on device/emulator

Roadmap: ViewModel tests (Turbine + MockK) · RxJava sample · transfers/notifications/offline+.

## 👨‍💻 Author

**Avinash Patil** — Android Engineer | Kotlin | Jetpack Compose · CodeChef 5⭐ (Global Rank 509)

🔗 [LinkedIn](https://www.linkedin.com/in/avinash-patil-278011228/) · 💻 [GitHub](https://github.com/Avi6855?tab=repositories) · 📧 avinashpatil6855@gmail.com

Open for Graduate Android (Fintech) roles 🚀 — willing to relocate: Poland / Portugal / Spain / UAE / UK (hybrid, 3 days in office).

⭐ Star · 🍴 Fork · 💬 Feedback welcome
