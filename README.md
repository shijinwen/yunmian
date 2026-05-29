# 云纪念 Android App

封装云纪念宠物纪念馆 Web 应用的 Android 原生 App。

## 功能

- WebView 加载 `http://47.96.125.42`
- 启动页 (SplashScreen) 1.5 秒后进入主页
- 下拉刷新页面
- 顶部加载进度条
- 网络错误页面 + 一键重试
- 返回键正确处理（页面返回 → 退出）
- Cookie 持久化（保持登录状态）

## 项目结构

```
app/src/main/
├── AndroidManifest.xml
├── java/com/yunmemorial/app/
│   ├── SplashActivity.kt   # 启动页
│   └── MainActivity.kt     # WebView 主页
└── res/
    ├── layout/
    │   ├── activity_splash.xml
    │   └── activity_main.xml
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── mipmap-*/           # 各密度图标
```

## 打包方式

### 方式一：GitHub Actions（无需安装 Android SDK）

1. 在 GitHub 创建仓库
2. 将本项目推到仓库
3. Actions 自动构建，产物在 Actions → Artifacts 下载

### 方式二：Android Studio

1. 安装 [Android Studio](https://developer.android.google.cn/studio)
2. File → Open → 选择本目录
3. Build → Build Bundle(s)/APK(s) → Build APK(s)

## 修改服务器地址

编辑 `app/src/main/java/com/yunmemorial/app/MainActivity.kt`：

```kotlin
const val BASE_URL = "http://47.96.125.42"  // 改为你的域名或 IP
```
