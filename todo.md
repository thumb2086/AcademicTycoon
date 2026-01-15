# Academic Tycoon - 開發任務清單 (細化版)

## v0.1: 核心架構與數據流

- [x] 基礎環境設定
    - [x] Gradle 配置：在 app/build.gradle.kts 中添加並同步 Compose, Room, Retrofit, Navigation, Coil 的依賴。

- [x] 網路與資料同步
    - [x] 離線優先邏輯：在 SyncRepository 中實作網路狀態檢查。若無網路，確保 App 使用 Room 中的快取題庫。

## v0.2: UI 與核心遊戲循環

- [x] 主畫面與導航
    - [x] 底部導航：使用 NavigationBar 實現「礦場」、「賭場」、「黑市」、「成就」四個主要畫面的切換。
    - [x] 全域狀態欄：在 App 最頂部建立永久顯示的 Composable，包含 balance 和 debt。
    - [x] 債務視覺提示：當 debt > 0 時，債務數字應顯示為紅色 (#FF0000)，並加入閃爍動畫效果。

- [x] 功能畫面實作 (Compose UI)
    - [x] 研發礦場 UI：
        - [x] 建立 QuestionCard Composable，以卡片形式顯示題目、選項。
        - [x] 實作答題邏輯，並在回答後顯示一個包含 explanation 的 AlertDialog。
    - [x] 賭場 UI (21點)：
        - [x] 繪製 21 點遊戲介面，包括玩家/莊家手牌、下注按鈕。
        - [x] 實作 21 點核心遊戲邏輯 (發牌、要牌、停牌、結算)。

- [x] 軟體用 tag 觸發自動建置流程
    - [x] GitHub Actions workflow 優化：push tag v* 時自動 build signed APK → 建立 Release → 上傳 APK asset。
    - [x] 整合熱更新檢查：App 內 GET /releases/latest API → 比對 versionCode → 提示下載新 APK。
    - [x] Release 說明：加 APK checksum + 安裝教學截圖。

- [ ] 核心邏輯驗證
    - [ ] 匯入測試題庫：確保 SyncRepository 能成功從 GitHub 下載並寫入「機械原理」題庫到 Room。
    - [ ] 還債邏輯測試：模擬負債情況，驗證 FinanceViewModel 是否精確地將 80% 的收益用於還債。

## 未來擴充規劃 (Roadmap)

### v0.3: 動態與多樣化
- [ ] 雲端賠率系統：
    - [ ] 擴充 config.json，加入賭場賠率欄位。
    - [ ] FinanceViewModel 或相關模組讀取此賠率，並應用於賭場勝負結算。
- [ ] 多學科支持：
    - [ ] 在 UI 中加入 Spinner 或 DropdownMenu 讓玩家選擇題庫。
    - [ ] 修改 SyncRepository 以根據玩家選擇下載不同的 bundle.json (例如 mechanical.json, highschool.json)。
- [ ] 考題資料集自動下載熱更新
    - [ ] bundle.json 格式定義：包含 id, subject, q, options, a, reward, explanation, image_url 等欄位。
    - [ ] SyncRepository 擴充：App 啟動或背景時，Retrofit GET bundle.json → 比對 local version → 清空舊 Question table → insert 新資料。
    - [ ] 分科別 JSON 支持（e.g., mech_bundle.json），並在 UI 選擇時動態下載。

### v0.4: 內容增強
- [ ] 題目圖片顯示：在 QuestionCard 中使用 Coil 加載並顯示 image_url 對應的圖片。
- [ ] 成就系統 UI：
    - [ ] 建立一個畫面，根據 userProfile.correct_count 顯示對應的頭銜 (學術難民 -> 工科兵 -> 首席機械師)。

### v0.5: 背景功能與賭場擴充
- [ ] 背景功能（後台）
    - [ ] 用 WorkManager 設定背景任務：每 1-24 小時檢查 GitHub config.json / bundle.json version。
    - [ ] 任務邏輯：呼叫 Retrofit 下載新版 → 更新 Room 題庫 / 設定（僅 WiFi 時執行）。
    - [ ] 負債背景累積（選項）：如果有利息，每小時 debt += debt * 0.01。
- [ ] 機率熱更新發布自動化流程
    - [ ] config.json 擴充：加 "casino_probs" 物件（e.g., {"blackjack_house_edge": 0.05, "roulette_house_edge": 2.7, "reward_multiplier": 1.5}）。
    - [ ] SyncRepository：下載後解析並套用到 FinanceViewModel / 賭場邏輯。
    - [ ] 發布自動化：GitHub Actions – push main 時，自動 commit 新 config.json，或手動 upload 觸發 App 熱更新。
- [ ] 賭場輪盤模式（純運氣補充 21 點）
    - [ ] 輪盤 UI：Compose Canvas 畫輪盤（0-36，紅黑交替） + 簡單旋轉動畫 + 結果顯示。
    - [ ] 投注選項：紅/黑、單/雙、1-18/19-36、單號 (Straight up)。
    - [ ] 輪盤邏輯：隨機產生 0-36，計算 payout（從 config.json 讀 house edge / 賠率）。
    - [ ] 整合 FinanceViewModel：下注扣款、贏錢加款（適用 80/20 還債規則）。
    - [ ] 測試：模擬 100 次 spin，驗證 house edge 接近設定值 + 遊戲平衡。

### v0.6: 發布與優化

### v0.7: 後台管理系統（整合 Firebase） - 管理員權限、後台調整參數、查看玩家紀錄
- [ ] 建立 Firebase 專案並整合到 App
    - [ ] 去 https://console.firebase.google.com 建立新專案，註冊 Android App（輸入 package name），下載 google-services.json 放 app/ 目錄。
    - [ ] 在 project-level build.gradle.kts 加 Google Services plugin。
    - [ ] 在 app/build.gradle.kts 加 Firebase BoM + 必要 SDK（firebase-auth, firebase-firestore, firebase-remote-config, firebase-analytics）。
    - [ ] 在 Application class 或 MainActivity 初始化 FirebaseApp。

- [ ] 玩家登入與資料雲端化
    - [ ] 啟用 Firebase Authentication：Email/Password + Google Sign-In。
    - [ ] 玩家登入後，用 uid 作為 doc ID，將 UserProfile (balance, debt, correct_count, rank 等) 存到 Firestore collection "users"。
    - [ ] 遊戲行為（答題、賭博）後自動 sync 到 Firestore（取代純 local Room，或 hybrid 使用）。
    - [ ] 啟用 Firestore offline persistence（自動處理離線）。

- [ ] 管理員權限檢查
    - [ ] 在 users doc 加 "role" 欄位（e.g., "admin"），或用 Firebase Admin SDK 設定 custom claims。
    - [ ] 登入後檢查 role / claims，若為 admin 則顯示隱藏 Admin 入口（e.g., 長按某按鈕或隱藏導航）。

- [ ] 後台調整機率/參數（雲端可調）
    - [ ] 用 Firebase Remote Config 建立參數：
        - blackjack_house_edge, roulette_house_edge, debt_repay_ratio, reward_multiplier 等（預設值）。
    - [ ] App 啟動/背景 fetch Remote Config → 套用到 FinanceViewModel / 賭場邏輯。
    - [ ] 管理員在 Firebase Console 直接修改參數 → 全玩家即時熱更新（無需新 APK）。

- [ ] 查看所有玩家遊玩紀錄（Admin 功能）
    - [ ] 自建 AdminScreen（Compose）：登入 admin 後顯示 users collection 列表。
    - [ ] 顯示內容：每個玩家 uid, balance, debt, correct_count, rank, 最後更新時間等。
    - [ ] 簡單統計：總玩家數、平均 correct_count、總 debt、高債務玩家列表。
    - [ ] 先用 Firebase Console 手動查 Firestore（快速起步），之後再寫 App 內 dashboard。

- [ ] **詳細輸贏日誌記錄與查詢 (Admin 功能)**
    - [ ] **設計事件日誌資料結構:** 在 Firestore 中建立新的 collection "game_events"。定義 document 格式，應包含 `userId`, `timestamp`, `eventType` (e.g., "blackjack_win", "question_reward", "debt_payment"), `amount`, `balance_before`, `balance_after` 等欄位。
    - [ ] **客戶端事件上報:** 在 `FinanceViewModel` 或相關邏輯中，每當有金融交易（贏錢、輸錢、答題獎勵、還債）發生時，異步將一筆 event document 寫入 "game_events" collection。
    - [ ] **擴充 AdminScreen:** 在管理員介面中，新增一個頁面或功能，允許根據 `userId` 或 `eventType` 查詢 "game_events" collection 的內容。
    - [ ] **日誌顯示:** 將查詢到的日誌以列表形式清晰地展示出來，包含時間、事件類型、金額等關鍵資訊，方便管理者追蹤玩家的詳細活動。

- [ ] 安全性與規則
    - [ ] Firestore Security Rules：一般玩家只能讀寫自己 doc；admin 角色可讀全部 users。
    - [ ] 加隱私政策說明（資料只用於遊戲改善、不外洩）。

## 額外待辦 / 優化想法（可視情況加入）
- [ ] 支援中英文介面 (Internationalization - i18n)
    - [ ] 建立多語言資源檔：建立 `values/strings.xml` (預設/英文) 和 `values-zh-rCN/strings.xml` (簡體中文)、`values-zh-rTW/strings.xml` (繁體中文)。
    - [ ] UI 組件文字抽離：將所有 Composable 中的硬編碼字串替換為 `stringResource(R.string.your_string_id)`。
    - [ ] 動態內容翻譯：規劃如何處理從 `bundle.json` 下載的題庫內容的國際化問題 (例如，在 JSON 中提供多語言版本)。
- [ ] 題庫品質：機械原理至少 50–100 題 + 完整解析
- [ ] 平衡調整：負債機制（低債務時降低扣款比例？）
- [ ] App 內檢查新版 APK (從 GitHub Releases API 比對)

最後更新：2026-01-14
下一個重點：完成 v0.1 階段 → 產生可跑的 debug APK 自己測試核心循環
長期目標：v0.7 Firebase 後台，讓機率可遠端調整 + 查看玩家數據