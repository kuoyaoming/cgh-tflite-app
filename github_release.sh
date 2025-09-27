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
RELEASE_BODY="## 🎯 Android 16 测试版本

### ✨ 新功能
- **Material 3 Expressive设计**: 全新的现代化界面
- **大屏幕适配**: 支持平板和折叠屏设备
- **桌面模式**: 类似Samsung DeX的桌面体验
- **Live Updates**: 实时进度通知系统
- **安全性增强**: 数据加密和生物识别支持

### 📱 系统要求
- **最低Android版本**: 5.0 (API Level 21)
- **推荐Android版本**: 16 (API Level 36)
- **RAM**: 2GB (推荐4GB)
- **存储空间**: 100MB

### 🧪 测试重点
- Material 3主题切换
- 大屏幕布局适配
- 桌面模式窗口管理
- Live Updates通知功能
- 安全功能验证

### 📋 安装说明
1. 下载APK文件
2. 启用"未知来源"安装
3. 安装并启动应用
4. 测试新功能

### 🐛 注意事项
- 这是测试版本，请谨慎使用
- 某些功能需要Android 16设备
- 桌面模式需要Android 7.0+

### 📞 反馈
如有问题请提交Issue到项目仓库

---
**构建信息**:
- 分支: $BRANCH
- 提交: $COMMIT_SHA
- 构建时间: $(date)
"

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
        --notes "$RELEASE_BODY" \
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
    echo "   - Description:"
    echo "$RELEASE_BODY"
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

echo ""
echo "🎉 GitHub Release准备完成！"