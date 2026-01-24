### v0.8.1: CI/CD 安全性增強與中斷排除

- **自動化檢查機制 (`release.yml`):**
    - **強化 Secret 校驗:** 實作了嚴格的 `[ -z "$VAR" ]` 檢查。目前的 Build 失敗是由於 GitHub 端的 `RELEASE_KEYSTORE_BASE64` 遺失或未正確載入，已成功防止產生損壞的 APK。
- **文件維護:** 更新 `AGENT.md` 規範，確保未來所有 Secret 處理均包含判空邏輯。

### v0.8: CI/CD 自動化發布修復

- **簽署流程優化:** 修復了 `Tag number over 30` 的 ASN.1 解碼錯誤。
...
