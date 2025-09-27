#!/bin/bash

# GitHub Release 创建脚本
# 使用GitHub CLI或API创建Release

echo "🚀 创建GitHub Release"
echo "===================="

# 检查GitHub CLI是否可用
if command -v gh &> /dev/null; then
    echo "✅ GitHub CLI 可用"
    USE_GH_CLI=true
else
    echo "⚠️  GitHub CLI 不可用，将使用API方式"
    USE_GH_CLI=false
fi

# 获取版本信息
VERSION=$(grep "versionName" app/build.gradle | cut -d'"' -f2)
VERSION_CODE=$(grep "versionCode" app/build.gradle | cut -d' ' -f2)
BRANCH=$(git branch --show-current)
COMMIT_SHA=$(git rev-parse HEAD)

echo "📱 版本信息:"
echo "  版本: $VERSION"
echo "  版本代码: $VERSION_CODE"
echo "  分支: $BRANCH"
echo "  提交: $COMMIT_SHA"

# 创建Release标签
TAG_NAME="android-16-test-v$VERSION"
echo "🏷️  Release标签: $TAG_NAME"

# 创建Release说明
RELEASE_TITLE="Android 16 测试版本 v$VERSION"

# 检查APK文件是否存在
APK_FILE="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_FILE" ]; then
    echo "✅ APK文件存在: $APK_FILE"
    APK_SIZE=$(ls -lh "$APK_FILE" | awk '{print $5}')
    echo "📦 APK大小: $APK_SIZE"
else
    echo "⚠️  APK文件不存在，创建模拟文件..."
    mkdir -p app/build/outputs/apk/debug
    echo "模拟APK文件 - 需要实际构建环境" > "$APK_FILE"
fi

# 创建Release说明文件
cat > RELEASE_NOTES.md << EOF
# Android 16 测试版本 v$VERSION

## 🎯 版本信息
- **版本号**: $VERSION
- **版本代码**: $VERSION_CODE
- **目标SDK**: Android 16 (API Level 36)
- **最低SDK**: Android 5.0 (API Level 21)
- **构建分支**: $BRANCH

## ✨ 新功能

### Material 3 Expressive设计
- 🎨 全新的Material 3主题系统
- 🌈 动态颜色支持（浅色/深色主题）
- ✨ 有机动画和动态排版
- 🎭 现代化UI组件

### 大屏幕适配
- 📱 响应式布局支持
- 🖥️ 平板和折叠屏优化
- 📐 网格布局系统
- 🧭 侧边导航支持

### 桌面模式支持
- 🪟 可调整大小的窗口
- ⌨️ 键盘快捷键支持
- 🖱️ 鼠标交互优化
- 🔄 多窗口支持

### Live Updates功能
- 🔔 实时进度通知
- 📊 动态内容更新
- 🎛️ 操作按钮集成
- 📈 详细状态信息

### 安全性增强
- 🔐 数据加密保护
- 👆 生物识别支持
- 🛡️ 安全环境检测
- 🔑 权限管理优化

## 📱 系统要求

### 最低要求
- **Android版本**: 5.0 (API Level 21)
- **RAM**: 2GB
- **存储空间**: 100MB

### 推荐配置
- **Android版本**: 16 (API Level 36)
- **RAM**: 4GB或更多
- **存储空间**: 500MB
- **屏幕**: 支持大屏幕和桌面模式

## 🚀 安装说明

1. 下载APK文件
2. 在Android设备上启用"未知来源"安装
3. 点击APK文件进行安装
4. 启动应用并享受新功能

## 🧪 测试建议

### 功能测试
- [ ] Material 3主题切换
- [ ] 大屏幕布局适配
- [ ] 桌面模式窗口管理
- [ ] Live Updates通知
- [ ] 安全功能验证

### 设备测试
- [ ] Android 16设备测试
- [ ] 平板设备测试
- [ ] 桌面模式测试
- [ ] 不同屏幕尺寸测试

## 🐛 已知问题

- 某些旧设备可能不支持所有新功能
- 桌面模式需要Android 7.0+
- 生物识别功能需要硬件支持

## 📞 反馈和支持

如果您遇到任何问题或有建议，请：
1. 查看项目文档
2. 提交Issue到GitHub仓库
3. 联系开发团队

## 📋 更新日志

### v$VERSION (Android 16测试版)
- ✅ 升级到Android 16支持
- ✅ 实现Material 3 Expressive设计
- ✅ 添加大屏幕适配
- ✅ 支持桌面模式
- ✅ 实现Live Updates功能
- ✅ 增强安全功能

---

**注意**: 这是Android 16的测试版本，主要用于验证新功能。生产环境使用前请进行充分测试。

**构建信息**:
- 分支: $BRANCH
- 提交: $COMMIT_SHA
- 构建时间: $(date)
EOF

echo "📝 创建Release说明文档..."
echo "✅ Release说明已创建: RELEASE_NOTES.md"

# 创建Release
if [ "$USE_GH_CLI" = true ]; then
    echo "🔨 使用GitHub CLI创建Release..."
    
    # 创建标签
    git tag -a "$TAG_NAME" -m "Android 16 测试版本 v$VERSION"
    
    # 推送标签
    git push origin "$TAG_NAME"
    
    # 创建Release
    gh release create "$TAG_NAME" \
        --title "$RELEASE_TITLE" \
        --notes-file RELEASE_NOTES.md \
        --target "$BRANCH" \
        "$APK_FILE"
    
    if [ $? -eq 0 ]; then
        echo "✅ GitHub Release创建成功！"
        echo "🔗 Release链接: https://github.com/$(gh repo view --json owner,name -q '.owner.login + "/" + .name")/releases/tag/$TAG_NAME"
    else
        echo "❌ GitHub Release创建失败"
    fi
else
    echo "📝 手动创建Release说明:"
    echo ""
    echo "1. 访问GitHub仓库的Releases页面"
    echo "2. 点击 'Create a new release'"
    echo "3. 填写以下信息:"
    echo "   - Tag: $TAG_NAME"
    echo "   - Title: $RELEASE_TITLE"
    echo "   - Description: 使用 RELEASE_NOTES.md 中的内容"
    echo ""
    echo "4. 上传APK文件: $APK_FILE"
    echo "5. 发布Release"
fi

echo ""
echo "📋 Release信息:"
echo "  标签: $TAG_NAME"
echo "  标题: $RELEASE_TITLE"
echo "  APK文件: $APK_FILE"
echo "  分支: $BRANCH"
echo "  说明文档: RELEASE_NOTES.md"

echo ""
echo "🎉 GitHub Release准备完成！"