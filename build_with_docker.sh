#!/bin/bash

# Android 16 APK 构建脚本
# 使用Docker容器进行构建

echo "🚀 Android 16 APK 构建脚本"
echo "================================"

# 检查Docker是否可用
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，无法进行构建"
    echo "💡 建议：安装Docker或配置Android SDK"
    exit 1
fi

# 创建Dockerfile用于Android构建
cat > Dockerfile << 'EOF'
FROM openjdk:21-jdk-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /workspace

# 复制项目文件
COPY . .

# 设置Android SDK环境变量
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# 下载并安装Android SDK
RUN mkdir -p $ANDROID_HOME/cmdline-tools/latest && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip commandlinetools-linux-11076708_latest.zip -d $ANDROID_HOME/cmdline-tools/latest && \
    rm commandlinetools-linux-11076708_latest.zip

# 接受Android SDK许可证
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses

# 安装必要的SDK组件
RUN $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-36" \
    "build-tools;34.0.0" \
    "cmdline-tools;latest"

# 构建APK
RUN ./gradlew assembleDebug

# 设置输出目录
VOLUME ["/workspace/app/build/outputs"]
EOF

echo "📦 创建Docker构建环境..."
echo "🐳 使用Docker构建Android APK..."

# 构建Docker镜像
echo "🔨 构建Docker镜像..."
docker build -t android-16-builder .

if [ $? -eq 0 ]; then
    echo "✅ Docker镜像构建成功"
    
    # 运行构建
    echo "🚀 开始构建APK..."
    docker run --rm -v $(pwd)/app/build/outputs:/workspace/app/build/outputs android-16-builder
    
    if [ $? -eq 0 ]; then
        echo "✅ APK构建成功！"
        echo "📁 APK位置: app/build/outputs/apk/debug/app-debug.apk"
        
        # 检查APK文件
        if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
            echo "📱 APK文件信息:"
            ls -lh app/build/outputs/apk/debug/app-debug.apk
            echo "🎉 构建完成！APK已准备好用于发布"
        else
            echo "❌ APK文件未找到"
        fi
    else
        echo "❌ APK构建失败"
    fi
else
    echo "❌ Docker镜像构建失败"
fi

echo "📋 构建完成"