### v0.7: Firebase 核心整合

- **Gradle 配置 (`build.gradle.kts`, `libs.versions.toml`):**
    - **依賴管理:** 在 `libs.versions.toml` 中新增了 `firebase-bom` 以及 `firebase-auth`、`firebase-firestore`、`firebase-config`、`firebase-analytics` 的函式庫定義。
    - **插件整合:** 成功在專案和 App 層級的 `build.gradle.kts` 中加入了 `com.google.gms.google-services` 插件，並完成 Gradle 同步。
- **初始化 (`AcademicTycoonApplication.kt`):**
    - 在自定的 `Application` 類別的 `onCreate` 方法中呼叫了 `FirebaseApp.initializeApp(this)`，確保 Firebase 在 App 啟動時就已準備就緒。
- **專案狀態:** 已成功將 Firebase 核心 SDK 整合到專案中，並完成了初始化，為後續的雲端功能（如玩家登入、資料同步、遠端配置）打下了基礎。

### v0.3: 考題資料集自動下載熱更新

- **版本控制與自動更新 (`SyncRepository.kt`, `PreferencesRepository.kt`):**
    - **DataStore 整合:** 將 `datastore-preferences` 依賴項加入 `build.gradle.kts`，並建立 `PreferencesRepository` 來儲存本地 `bundle.json` 的版本號。
    - **版本比對邏輯:** 擴充 `SyncRepository`，使其在 App 啟動時自動從遠端 `config.json` 獲取最新的 `bundle_version`，並與本地版本比對。
    - **自動下載與更新:** 當遠端版本較新時，自動觸發下載 `bundle.json`，清空並重新寫入 Room 資料庫，確保題庫內容保持最新。
- **啟動時同步 (`MiningViewModel.kt`):
    - **自動化:** 在 `MiningViewModel` 的 `init` 區塊中呼叫 `syncConfig()` 與 `syncQuestions()`，確保 App 一啟動就自動檢查並更新題庫。
    - **簡化 API:** 移除了 `loadQuestionsFromUrl` 方法，改為全自動的同步流程。

### v0.5: 背景同步與賭場擴充

- **核心邏輯重構 (`FinanceViewModel.kt`):**
    - **關鍵修復:** 透過建立專用的 `applyIncomeWithDebtRule` 函數，將學術獎勵 (`correct_count`) 與通用的 80/20 收入規則脫鉤。這可以防止賭場的贏利錯誤地增加學術統計數據。
    - **強化的財務 API:** 新增 `returnBet` 函數以處理紙牌遊戲中的「平局」情況，確保賭注在不觸發收入邏輯的情況下被正確退還。
- **背景同步 (`WorkManager`, `UpdateWorker.kt`):
    - **架構:** 整合 `WorkManager` 和 `Hilt-Work` 以實現可靠的、週期性的背景任務。
    - **實作:** 建立了一個 `UpdateWorker`，每 12 小時會獲取遠端的 `config.json` 和對應的題庫 `bundle`。
    - **排程:** 在 `AcademicTycoonApplication` 中設定了排程，以執行這個唯一的、受網路限制的任務，確保 App 內容自動保持最新。
- **賭場擴充 (`RouletteScreen.kt`, `CasinoScreen.kt`):
    - **新遊戲模式:** 開發了完整的 `RouletteScreen`，包含 UI、下注邏輯和輸贏條件。
    - **整合:** 將輪盤遊戲完全整合到 App 的導航圖中，可從賭場主畫面進入。
    - **邏輯統一:** 重構了 21 點 (`CasinoScreen`) 和輪盤遊戲，使其使用 `FinanceViewModel` 中集中的、穩健的函數 (`handleCasinoWin`, `returnBet`, `deductBet`)，確保所有博弈活動都遵守相同的財務規則和遠端賠率。
- **專案狀態:** v0.5 已完成。App 現在更加穩健，具備背景內容更新和一個擴充的、規則一致的賭場。

### v0.4: 內容增強

- **圖片顯示 (`MiningScreen.kt`):**
    - **驗證:** 確認了 `QuestionCard` Composable 已包含使用 `AsyncImage` (Coil) 從 URL 顯示圖片的邏輯。由於該功能已實現，因此無需更改。
- **成就 UI (`AchievementsScreen.kt`):
    - **新畫面:** 建立了一個新畫面，根據玩家的 `correct_count` 顯示其頭銜（例如「學術難民」、「工科兵」）。
    - **UI 設計:** 設計了一個清晰且視覺上吸引人的卡片式 UI，用於顯示當前頭銜和總答對題數。
- **導航 (`AppNavigation.kt`, `MainScreen.kt`):
    - **整合驗證:** 確認 `AchievementsScreen` 已完全整合到主底部導航欄中，玩家可以方便地進入。

### v0.3: 動態內容與賠率

- **遠端配置 (`ApiService`, `SyncRepository`):**
    - **遠端讀取:** 實作了 `getConfig` 以獲取遠端的 `config.json`。
    - **快取機制:** 建立了一個單例的 `SyncRepository` 來快取遠端配置，使其在 App 的整個生命週期內都可存取。
- **動態賭場賠率 (`FinanceViewModel`):
    - **動態獎勵:** ViewModel 現在會從快取的遠端配置中獲取 `reward_multiplier`。
    - **獎勵計算:** 一個新的 `handleCasinoWin` 函數使用這個動態乘數來計算支付金額，確保賭場獎勵自動遵守 80/20 的還債規則。
- **多學科支援 (`MiningViewModel`, `MiningScreen`):
    - **重構:** 將重複的 ViewModel (`MineViewModel` 和 `MiningViewModel`) 合併為一個單一、穩定的 `MiningViewModel`。
    - **UI 與邏輯:** 在 `MiningScreen` 中新增了一個下拉選單，允許使用者選擇不同的學科。
    - **動態載入:** 實作了根據使用者選擇從網路動態載入不同題庫 (`mechanical.json`, `highschool.json`) 的邏輯。

### v0.2: 核心邏輯驗證

- **測試與驗證:**
    - **題庫匯入:** 修改 `MiningViewModel.kt` 指向 `mechanical.json` 以完成 `todo.md` 中的測試案例。
    - **還債邏輯:** 在 `FinanceViewModel.kt` 中手動模擬了 `processReward` 函數，以驗證 80/20 的還債規則被正確且精確地實作。

### v0.1: 初始專案設定

- **核心邏輯:** 建立了 `UserProfile` 和 `Question` 實體的 Room 資料庫結構。
- **架構:** 實作了帶有初始 80/20 還債邏輯的 `FinanceViewModel`。
- **資料層:** 建立了 `AppDatabase`、DAOs 和 `UserRepository` 作為資料管理的基礎。
