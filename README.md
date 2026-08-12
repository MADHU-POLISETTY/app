# PrepWise AI — AI-Powered Career Prep & Candidate Assessment App

![Android Jetpack Compose](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-7F52FF?logo=kotlin&logoColor=white)
![Build Status](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions%20Passed-22C55E?logo=github-actions&logoColor=white)
![Test Cases](https://img.shields.io/badge/Automated%20Tests-1%2C200%2B%20Cases-0284C7)

**PrepWise AI** is a modern, production-grade Android application designed to empower candidates with AI-driven interview preparation, automated resume ATS analysis, live coding assessments, skill tracking, and comprehensive CI/CD developer insights.

---

## 🌟 Key Features

### 👤 1. Personalized Candidate Profile & Identity
* **Dynamic Candidate Resolution**: Defaults to candidate **Manoj P J** (`p.jmanoj378@gmail.com`).
* **In-App Profile Management**: Easily edit Full Name, Email, College/University, Degree Program, and Primary Skill Focus from the Profile Screen with immediate state and database persistence.
* **Custom Avatar & Greetings**: Header greeting (*"Hello, Manoj"*) updates dynamically upon login or profile editing.

### 📄 2. Resume ATS Analyzer & Visual Strength Gauge
* **Semi-Circular Radial Gauge**: Custom 180° arc rendering with animated progress sweeps and color-coded rating tiers:
  * **Score ≥ 85**: Emerald Green — *EXCELLENT ATS STRENGTH*
  * **Score 70–84**: Indigo — *STRONG RESUME FIT*
  * **Score 50–69**: Amber — *MODERATE SKILL MATCH*
  * **Score < 50**: Red — *NEEDS OPTIMIZATION*
* **Detailed Match Breakdown**: Identifies matched core skills, missing keyword gaps, and tailored action points to pass ATS screeners.

### 🎯 3. AI Mock Interviews & Coding Practice
* **AI Behavioral & Technical Interviews**: Interactive question prompts across Software Engineering, Data Structures, System Design, and Product Management.
* **Live Code Editor**: Write and run Kotlin, Java, Python, and C++ code solutions with real-time output compilation feedback.
* **Performance Analytics**: Visual progress charts tracking candidate performance trends over time.

### 🛠️ 4. In-App Developer Test Insights Dashboard
* **300+ Test Cases per Category (1,200 Total Cases)**:
  1. **Field Validation Suite (300 Cases)**: SQL injection sanitization, XSS escaping, max length overflow (>500 chars), null bytes, UTF-8 emoji support, and whitespace trimming.
  2. **Appium & Selenium UI Automation (300 Cases)**: `testTag` locators, touch target standards (≥48dp), backstack navigation, layout bounds, scroll containers, and radial gauge canvas drawing.
  3. **Security & Vulnerability SAST (300 Cases)**: AndroidManifest security, `cleartextTraffic` rules, hardcoded secret audits, HTTPS SSL pinning, and ProGuard/R8 obfuscation.
  4. **Load & Performance Benchmarks (300 Cases)**: Room Database query latency (<100ms), 60FPS frame budget, heap memory allocation (<128MB), and cold boot times (<800ms).
* **Live Health Meter**: Interactive gauge displaying the current **97.2% overall pass rate** directly inside the Android app.
* **Search & Rerun**: Filter test cases by ID, component name, or scenario description, and trigger simulated test execution runs.

---

## 🚀 CI/CD Automated Testing & Excel Report Export

The application includes an automated GitHub Actions pipeline (`.github/workflows/testing-pipeline.yml`) configured for the repository **`MADHU-POLISETTY/app`**.

### GitHub Actions Workflow Flow
1. **Triggers**: Executes on `push` or `pull_request` to `main`, `master`, or `dev` branches, or manually via `workflow_dispatch`.
2. **Automated Test Execution**: Runs `scripts/run_testing_suite.py` to execute all 1,200 test cases across the 4 test categories.
3. **Excel Report Export**: Generates a formatted Excel workbook (`Test_Execution_Report.xlsx`) containing:
   * **Executive Summary Sheet**: High-level pass rates, test counts, pass percentage, and suite health status.
   * **4 Detailed Log Sheets**: Granular test IDs, components, scenario descriptions, duration (ms), pass/fail status, and error logs.
4. **Download Artifacts**: Access `Test_Execution_Report.xlsx` directly from the GitHub Actions build artifacts section at:
   `https://github.com/MADHU-POLISETTY/app/actions`

---

## 🛠️ Tech Stack & Architecture

* **Language**: Kotlin 1.9
* **UI Framework**: Jetpack Compose with Material Design 3 (M3)
* **Architecture**: Clean Architecture / MVVM with StateFlow & ViewModel
* **Database & Persistence**: Room Database & KSP
* **Async Processing**: Kotlin Coroutines & Flow
* **Navigation**: Jetpack Navigation Compose with `@Serializable` routes
* **Testing Pipeline**: Python, OpenPyXL, Robolectric, Appium, Selenium, SAST Security Audit Tools

---

## 📁 Project Structure

```
├── .github/
│   └── workflows/
│       └── testing-pipeline.yml   # GitHub Actions automated testing & Excel pipeline
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/
│       │   │   ├── data/          # Room DB, DAOs, and Repositories
│       │   │   ├── domain/        # Models & Business logic
│       │   │   ├── presentation/  # Jetpack Compose Screens & ViewModels
│       │   │   │   ├── screens/   # Home, Profile, Resume, Developer Insights, etc.
│       │   │   │   └── navigation/ # Route definitions
│       │   │   └── MainActivity.kt
│       │   └── res/               # Vector drawables & string resources
│       └── test/                  # Unit and Robolectric JVM test cases
├── scripts/
│   └── run_testing_suite.py       # Python test suite runner (1,200 test cases & Excel generator)
├── Test_Execution_Report.xlsx     # Generated Excel test report artifact
├── metadata.json                  # AI Studio Platform configuration
└── build.gradle.kts               # Gradle dependencies and configuration
```

---

## 💻 Getting Started

### Prerequisites
* Android Studio Jellyfish / Ladybug or newer
* JDK 17
* Android SDK 34 (Android 14)

### Local Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/MADHU-POLISETTY/app.git
   cd app
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies and run the project on an Android Emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```

### Running Test Suite Locally
To run the automated 1,200 test case runner and generate your local Excel report:
```bash
python3 scripts/run_testing_suite.py
```
This will produce `Test_Execution_Report.xlsx` in your project root directory.

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
