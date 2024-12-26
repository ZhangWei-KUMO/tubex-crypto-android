# TubeX 币圈风控模型Android客户端

这是一个原生Android应用，针对加密货币市场，本应用集成了各类统计学模型和机器算法模型；
本项目主要用于统计学学习研究，仅供学习只用，不做任何投资建议。

本项目尚处于开发阶段。

## 全局变量管理

## 版本管理

在`Gradle Scripts`文件夹下的`build.gradle.kts` 中进行版本号修改。
```js
android {
    namespace = "analysis"
    compileSdk = 34

    defaultConfig {
        applicationId = "analysis"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0" // 修改版本号

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```
## 附件

1. [ICONS下载](https://fonts.google.com/icons?selected=Material+Symbols+Outlined:ios_share:FILL@0;wght@400;GRAD@0;opsz@24&icon.size=24&icon.color=%23e8eaed&icon.platform=android)