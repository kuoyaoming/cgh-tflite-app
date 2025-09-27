# Android 16 测试构建说明

## 🚀 快速开始

### 1. 环境要求
- **Java**: OpenJDK 21 或更高版本
- **Android SDK**: API Level 36 (Android 16)
- **Gradle**: 8.9
- **Android Gradle Plugin**: 8.7.3

### 2. 设置Android SDK

#### 方法一：使用Android Studio
1. 打开Android Studio
2. 进入 File -> Settings -> Appearance & Behavior -> System Settings -> Android SDK
3. 复制SDK Location路径
4. 更新 `local.properties` 文件中的 `sdk.dir` 路径

#### 方法二：手动安装Android SDK
```bash
# 下载Android SDK Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# 解压到指定目录
unzip commandlinetools-linux-11076708_latest.zip -d ~/android-sdk

# 设置环境变量
export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# 安装必要的SDK组件
sdkmanager "platform-tools" "platforms;android-36" "build-tools;34.0.0"
```

### 3. 构建APK

#### 清理项目
```bash
./gradlew clean
```

#### 构建Debug版本
```bash
./gradlew assembleDebug
```

#### 构建Release版本
```bash
./gradlew assembleRelease
```

#### 安装到设备
```bash
./gradlew installDebug
```

### 4. 测试分支信息

当前测试分支：`android-16-test-build`

#### 主要改进
- ✅ 更新到Android 16 (API Level 36)
- ✅ Material 3 Expressive设计
- ✅ 大屏幕适配支持
- ✅ 桌面模式支持
- ✅ Live Updates功能
- ✅ 安全性增强

#### 新增文件
- `app/src/main/res/values/themes.xml` - Material 3主题
- `app/src/main/res/values/colors.xml` - 动态颜色方案
- `app/src/main/res/layout-sw600dp/activity_main.xml` - 大屏幕布局
- `app/src/main/res/xml/desktop_config.xml` - 桌面模式配置
- `app/src/main/java/com/cgh/org/audio/Interface/LiveUpdatesManager.java` - 实时通知
- `app/src/main/java/com/cgh/org/audio/Interface/SecurityManager.java` - 安全功能

### 5. 构建输出

构建成功后，APK文件将位于：
- Debug版本：`app/build/outputs/apk/debug/app-debug.apk`
- Release版本：`app/build/outputs/apk/release/app-release.apk`

### 6. 测试建议

#### 功能测试
- [ ] Material 3主题切换
- [ ] 大屏幕布局适配
- [ ] 桌面模式窗口管理
- [ ] Live Updates通知
- [ ] 安全功能验证

#### 设备测试
- [ ] Android 16设备测试
- [ ] 平板设备测试
- [ ] 桌面模式测试
- [ ] 不同屏幕尺寸测试

### 7. 故障排除

#### 常见问题

**问题1**: `SDK location not found`
**解决**: 更新 `local.properties` 文件中的 `sdk.dir` 路径

**问题2**: `Minimum supported Gradle version is 8.9`
**解决**: 已更新到Gradle 8.9

**问题3**: `Chaquopy requires minSdk version 21`
**解决**: 已更新minSdkVersion到21

**问题4**: `Namespace not specified`
**解决**: 已在build.gradle中添加namespace

### 8. 性能优化

#### Gradle配置优化
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx4608m
```

#### 构建优化
- 启用增量构建
- 使用Gradle缓存
- 并行构建
- 按需配置

### 9. 部署清单

#### 构建前检查
- [ ] Android SDK已安装
- [ ] local.properties配置正确
- [ ] 所有依赖已更新
- [ ] 权限配置正确

#### 构建后验证
- [ ] APK文件生成成功
- [ ] 签名配置正确
- [ ] 版本号正确
- [ ] 功能测试通过

### 10. 联系支持

如果遇到构建问题，请检查：
1. Android SDK版本是否支持API Level 36
2. Java版本是否为21或更高
3. Gradle版本是否为8.9
4. 所有依赖是否已更新

## 📱 测试APK下载

构建成功后，您可以在以下位置找到APK文件：
- **Debug版本**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release版本**: `app/build/outputs/apk/release/app-release.apk`

## 🎯 下一步

1. **安装APK**: 将APK安装到Android 16设备上测试
2. **功能验证**: 测试所有新功能是否正常工作
3. **性能测试**: 监控应用性能和电池消耗
4. **用户反馈**: 收集用户对新功能的反馈
5. **持续改进**: 根据测试结果优化应用