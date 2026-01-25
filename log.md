### v0.8.3: 修正 CI/CD 發布權限 (403 Forbidden)

- **Workflow 權限提升 (`release.yml`):**
    - **明確授權:** 在 YAML 中加入了 `permissions: contents: write`，解決 `Resource not accessible by integration` 錯誤。現在編譯完成後，Workflow 將有權限自動在 GitHub 建立 Release 並附加 APK。

### v0.8.2: 修正簽署屬性寫入邏輯
...
