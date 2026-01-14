# 《賭場：學術大亨》專案開發執行計畫書 (完整版)

## 壹、 專案願景與核心邏輯

**專案名稱：** 賭場：學術大亨 (Academic Tycoon)

**核心目標：** 一款結合「硬派工科知識」與「高壓博弈心理」的教育型 App。

**遊戲循環：**

*   **研發礦場 (挖礦)：** 玩家透過回答學科題目（首發：機械工程）賺取虛擬貨幣。
*   **借貸黑市 (壓力)：** 餘額不足時可借貸，但負債狀態下，所有收益的 80% 將被強制扣除還債。
*   **核心賭場 (分配)：** 透過 21 點或高低牌博弈，實現財富快速翻倍或瞬間破產。

## 貳、 技術堆棧 (Tech Stack)

*   **開發語言：** Kotlin
*   **UI 框架：** Jetpack Compose (採用工業黑 #121212 與 螢光綠 #00FF00 配色)
*   **本地資料庫：** Room (存放題庫、使用者狀態、快取)
*   **網路請求：** Retrofit + Coil (圖片加載)
*   **架構模式：** MVVM + Repository Pattern
*   **內容更新：** GitHub Repository 作為雲端插件層 (動態加載 JSON 題庫)

## 叁、 系統架構設計

### 1. 資料模型 (Data Models)

*   `UserProfile`: `balance` (Long), `debt` (Long), `correct_count` (Int), `rank` (String).
*   `Question`: `id`, `subject`, `q` (題目), `options` (List), `a` (正確索引), `reward`, `explanation`, `image_url`.
*   `Config`: `odds_multiplier`, `announcement`, `version`.

### 2. 核心模組功能

*   **還債演算法：** `if (debt > 0) { pay_back = reward * 0.8; pocket = reward * 0.2 }`
*   **熱更新機制：** 啟動時比對 GitHub 上的 `config.json` 版本號，若有更新則背景下載 `bundle.json` 並覆寫 Room 資料庫。
*   **成就系統：** 根據 `correct_count` 自動更新頭銜（學術難民 -> 工科兵 -> 首席機械師）。

## 肆、 資料標準格式 (JSON Standard)

請 AI 代理依此格式解析雲端題庫：

```json
{
  "bundle_id": "MECH_V1",
  "questions": [
    {
      "id": "M01",
      "subject": "機械原理",
      "q": "兩齒輪之周節(Pc)與徑節(Pd)之乘積為何？",
      "image_url": "https://raw.githubusercontent.com/user/repo/main/img/m01.png",
      "options": ["1", "π", "2π", "0.5"],
      "a": 1,
      "reward": 20,
      "explanation": "周節 Pc = πd / T，徑節 Pd = T / d。相乘結果為 π。"
    }
  ]
}
```

## 伍、 開發實作路徑 (Milestones)

### 第一階段：基礎環境與 Room 資料庫

1.  配置 `build.gradle` (Compose, Room, Retrofit, Navigation)。
2.  建立 `AppDatabase` 與實體類（Entity）。
3.  實作 DataStore 或 Room 儲存玩家資產狀態。

### 第二階段：網路層與 GitHub 同步

1.  建立 `ApiService` 讀取 GitHub 上的 JSON。
2.  實作 `SyncRepository`：下載 JSON -> 映射至 Entity -> 存入 Room。
3.  實作離線模式：若無網路，優先讀取 Room 內的舊題庫。

### 第三階段：UI 實作 (Jetpack Compose)

1.  主導航介面：底部導航（礦場、賭場、黑市、成就）。
2.  研發礦場 UI：卡片式題目設計，答題後顯示解析彈窗。
3.  賭場 UI：簡單的 21 點邏輯與動畫。
4.  全域狀態顯示：頂部永久顯示當前餘額與債務（若有債務，UI 改為深紅色）。

### 第四階段：內容與邏輯測試

1.  匯入「機械原理」測試題庫。
2.  驗證 80/20 還債邏輯是否精確扣款。

## 陸、 給 Android Studio AI 代理的啟動指令

請將下方這段指令貼給你的 AI 助手，它將開始工作：

「請作為我的 Android 開發專家，協助我開發《學術大亨》App。

**Step 1: 專案初始化**
請幫我寫出基礎架構代碼：

*   定義 `UserProfile` 與 `Question` 的 Room Entity。
*   建立 `AppDatabase` 並提供 Hilt 注入或 Singleton 實作。
*   設定一個 `FinanceViewModel` 負責處理餘額增減與 80/20 還債邏輯。

**Step 2: 網路同步**
請寫出一個 Repository，使用 Retrofit 下載指定的 GitHub JSON 網址，並將解析後的題庫插入 Room 資料庫。

**Step 3: UI 視覺**
使用 Jetpack Compose 建立主畫面。配色採用黑色背景 (#121212) 與螢光綠文字 (#00FF00)。頂部需顯示玩家餘額與債務（若債務 > 0，數字顯示為紅色並帶有閃爍感）。

我們先從 Step 1 的 Room 資料庫結構開始，請提供 Kotlin 代碼。」

## 柒、 未來擴充規劃 (Roadmap)

*   動態賠率系統：由雲端控制賭場勝率。
*   多類群支持：下拉選單切換 `mechanical.json` 或 `highschool.json`。
*   工科圖庫：支持大量的機構受力圖與公差配合標註圖。
