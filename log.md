### v0.8: CI/CD 自動化發布修復

- **簽署流程優化 (`release.yml`):**
    - **修復 Keystore 毀損問題:** 修正了 GitHub Actions 中的 Base64 解碼邏輯，從 `echo` 改為 `printf "%s"`，徹底解決 `Tag number over 30 is not supported` 導致的簽署失敗。
    - **增加防錯機制:** 加入解碼後檔案大小校驗，確保 `release.keystore` 正確生成才執行 `assembleRelease`。

### v0.7: 財務邏輯與 UI 深度整合

- **財務系統升級 (`FinanceViewModel.kt`, `UserRepository.kt`):**
    - **動態頭銜系統:** 實作了 `calculateRank` 邏輯，玩家現在會根據「累積答對題數」從「學術難民」晉升至「諾貝爾獎得主」。
    - **Firebase 全自動同步:** 修正了 `UserRepository`，現在所有的餘額變動、債務更新及分析數據都會即時同步至 Firestore (`/users/{uid}`)。
    - **80/20 規則校準:** 確保所有收入路徑（礦場、賭場）均嚴格遵守 80% 自動還債邏輯。
- **UI/UX 優化 (`MainActivity.kt`, `AppNavigation.kt`, `MiningScreen.kt`):**
    - **Edge-to-Edge 支援:** 啟用 `enableEdgeToEdge()` 並在頂部控制台加入 `statusBarsPadding()`，確保 UI 不被系統狀態列遮擋。
    - **控制面板美化:** 頂部狀態欄加入 `HorizontalDivider` 與 `tonalElevation`，提升視覺層次感。
- **架構優化:** 修正了 `AppModule` 的 Hilt 注入，確保 `FirebaseAuth` 被正確傳遞至 Repository 用於資料同步。

### v0.7: Firebase 核心整合 (舊紀錄)
... (其餘內容)
