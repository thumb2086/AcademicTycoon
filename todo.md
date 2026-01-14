# Academic Tycoon - 開發任務清單 (細化版)

## v0.1: 核心架構與數據流

- [ ] **基礎環境設定**
    - [ ] **Gradle 配置:** 在 `app/build.gradle.kts` 中添加並同步 Compose, Room, Retrofit, Navigation, Coil 的依賴。
- [ ] **網路與資料同步**
    - [ ] **離線優先邏輯:** 在 `SyncRepository` 中實作網路狀態檢查。若無網路，確保 App 使用 Room 中的快取題庫。

---

## v0.2: UI 與核心遊戲循環

- [ ] **主畫面與導航**
    - [ ] **底部導航:** 使用 `NavigationBar` 實現「礦場」、「賭場」、「黑市」、「成就」四個主要畫面的切換。
    - [ ] **全域狀態欄:** 在 App 最頂部建立永久顯示的 Composable，包含 `balance` 和 `debt`。
    - [ ] **債務視覺提示:** 當 `debt > 0` 時，債務數字應顯示為紅色 (#FF0000)，並加入閃爍動畫效果。
- [ ] **功能畫面實作 (Compose UI)**
    - [ ] **研發礦場 UI:**
        - [ ] 建立 `QuestionCard` Composable，以卡片形式顯示題目、選項。
        - [ ] 實作答題邏輯，並在回答後顯示一個包含 `explanation` 的 AlertDialog。
    - [ ] **賭場 UI (21點):**
        - [ ] 繪製 21 點遊戲介面，包括玩家/莊家手牌、下注按鈕。
        - [ ] 實作 21 點核心遊戲邏輯 (發牌、要牌、停牌、結算)。
- [ ] **核心邏輯驗證**
    - [ ] **匯入測試題庫:** 確保 `SyncRepository` 能成功從 GitHub 下載並寫入「機械原理」題庫到 Room。
    - [ ] **還債邏輯測試:** 模擬負債情況，驗證 `FinanceViewModel` 是否精確地將 80% 的收益用於還債。

---

## 未來擴充規劃 (Roadmap)

- [ ] **v0.3: 動態與多樣化**
    - [ ] **雲端賠率系統:**
        - [ ] 擴充 `config.json`，加入賭場賠率欄位。
        - [ ] `FinanceViewModel` 或相關模組讀取此賠率，並應用於賭場勝負結算。
    - [ ] **多學科支持:**
        - [ ] 在 UI 中加入 `Spinner` 或 `DropdownMenu` 讓玩家選擇題庫。
        - [ ] 修改 `SyncRepository` 以根據玩家選擇下載不同的 `bundle.json` (例如 `mechanical.json`, `highschool.json`)。
- [ ] **v0.4: 內容增強**
    - [ ] **題目圖片顯示:** 在 `QuestionCard` 中使用 `Coil` 加載並顯示 `image_url` 對應的圖片。
    - [ ] **成就系統 UI:** 建立一個畫面，根據 `userProfile.correct_count` 顯示對應的頭銜 (學術難民 -> 工科兵 -> 首席機械師)。
