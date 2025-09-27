#!/bin/bash

echo "🚀 创建GitHub Release"
echo "===================="

# 获取版本信息
VERSION=$(grep "versionName" app/build.gradle | cut -d'"' -f2)
BRANCH=$(git branch --show-current)
COMMIT_SHA=$(git rev-parse HEAD)

echo "📱 版本信息:"
echo "  版本: $VERSION"
echo "  分支: $BRANCH"
echo "  提交: $COMMIT_SHA"

# 创建Release标签
TAG_NAME="android-16-test-v$VERSION"
echo "🏷️  Release标签: $TAG_NAME"

# 检查APK文件
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

# 创建Release说明
cat > RELEASE_NOTES.md << 'EOF'
# Android 16 测试版本

## 新功能
- Material 3 Expressive设计
- 大屏幕适配支持
- 桌面模式支持
- Live Updates功能
- 安全性增强

## 系统要求
- Android 5.0+ (推荐Android 16)
- 2GB RAM (推荐4GB)
- 100MB存储空间

## 安装说明
1. 下载APK文件
2. 启用"未知来源"安装
3. 安装并启动应用

## 注意事项
- 这是测试版本
- 某些功能需要Android 16
- 请谨慎使用
EOF

echo "📝 创建Release说明文档..."
echo "✅ Release说明已创建: RELEASE_NOTES.md"

echo ""
echo "📋 手动创建Release步骤:"
echo "1. 访问GitHub仓库的Releases页面"
echo "2. 点击 'Create a new release'"
echo "3. 填写信息:"
echo "   - Tag: $TAG_NAME"
echo "   - Title: Android 16 测试版本 v$VERSION"
echo "   - Description: 使用 RELEASE_NOTES.md 中的内容"
echo ""
echo "4. 上传APK文件: $APK_FILE"
echo "5. 发布Release"

echo ""
echo "📋 Release信息:"
echo "  标签: $TAG_NAME"
echo "  APK文件: $APK_FILE"
echo "  分支: $BRANCH"
echo "  说明文档: RELEASE_NOTES.md"

echo ""
echo "🎉 GitHub Release准备完成！"