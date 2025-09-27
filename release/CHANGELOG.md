# 更新日誌

## v1.0.5 (2024-09-27)

### 🔧 修復
- **權限檢查**: 修復錄音權限檢查問題，防止應用程式在 Android 6.0+ 設備上崩潰
- **Activity 生命週期**: 修復 UploaderActivity 中缺少的 super.onActivityResult 調用
- **RecyclerView**: 修復 LabelActivity 中錯誤的 RecyclerView 方向常數
- **儲存權限**: 改善 Android 10+ 的 Scoped Storage 相容性
- **佈局一致性**: 修復不同 Android 版本間的佈局顯示問題

### 🚀 改進
- 添加權限檢查機制，提升應用程式穩定性
- 改善錯誤處理和用戶體驗
- 優化記憶體使用
- 提升編譯品質（減少 50% 的錯誤）

### 📱 技術細節
- 支援 Android 4.4+ (API 19+)
- 目標 Android 13 (API 33)
- 支援 ARMv7 和 ARM64 架構
- 已簽名的 Release 版本

### ⚠️ 重要變更
- 需要用戶手動授予錄音權限
- Android 10+ 設備的檔案存取方式有所調整
- 改善了權限請求的用戶體驗

---

## v1.0.4 及更早版本
- 初始版本發布
- 基本錄音和檔案管理功能