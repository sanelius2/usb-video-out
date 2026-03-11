# 🔧 修复Gradle下载超时错误

## 错误信息
```
Could not install Gradle distribution from 'https://services.gradle.org/distributions/gradle-8.0-bin.zip'
Reason: java.net.SocketTimeoutException: Read timed out
```

## 原因
无法从Gradle官方服务器下载，通常是网络问题。

---

## ✅ 解决方案（3选1）

### 方案1：手动下载Gradle（最快）⭐⭐⭐⭐⭐

#### 步骤1：下载Gradle

**使用国内镜像下载：**

**腾讯云镜像：**
```
https://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
```

**阿里云镜像：**
```
https://mirrors.aliyun.com/gradle/gradle-8.0-bin.zip
```

**华为云镜像：**
```
https://mirrors.huaweicloud.com/gradle/gradle-8.0-bin.zip
```

**使用浏览器下载以上任意一个链接**

#### 步骤2：放置Gradle

将下载的 `gradle-8.0-bin.zip` 文件复制到：

```
C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\自动生成的随机文件夹\
```

或者更简单的方法：

1. 创建文件夹：
   ```
   C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\cache\
   ```

2. 将 `gradle-8.0-bin.zip` 复制到：
   ```
   C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\cache\
   ```

#### 步骤3：解压Gradle

1. 将 `gradle-8.0-bin.zip` 解压到：
   ```
   C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\cache\
   ```

2. 解压后会得到 `gradle-8.0` 文件夹

#### 步骤4：重新编译

```cmd
cd scrcpy/AndroidApp
gradlew.bat assembleDebug
```

---

### 方案2：修改Gradle镜像源

#### 步骤1：修改Gradle Wrapper属性

编辑 `AndroidApp/gradle\wrapper\gradle-wrapper.properties`：

将：
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
```

改为（腾讯云镜像）：
```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
```

或（阿里云镜像）：
```properties
distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-8.0-bin.zip
```

#### 步骤2：清理并重新编译

```cmd
cd scrcpy/AndroidApp
gradlew.bat clean
gradlew.bat assembleDebug
```

---

### 方案3：使用离线模式（如果有旧版本Gradle）

#### 如果之前下载过Gradle

1. 检查是否已有Gradle：
   ```cmd
   dir C:\Users\sanelius\.gradle\wrapper\dists\
   ```

2. 如果有旧版本，修改 `gradle-wrapper.properties` 使用旧版本

3. 使用离线模式：
   ```cmd
   gradlew.bat assembleDebug --offline
   ```

---

## 🚀 推荐方法：自动修复脚本

我为你准备了自动修复脚本：

**双击运行：** `FIX_GRADLE_ERROR.bat`

---

## 💡 使用代理（如果有VPN）

### 方法1：设置系统代理

编辑 `AndroidApp/gradle.properties`，添加：

```properties
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7890
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

### 方法2：在下载工具中使用代理

使用下载工具（如IDM、Thunder）配置代理后下载

---

## ⚡ 最快解决方法

### 方法A：使用GitHub Actions（完全跳过Gradle问题）

**不需要下载Gradle！**

查看：`GITHUB_UPLOAD_GUIDE.md`

5分钟搞定APK！

---

### 方法B：手动下载Gradle（10分钟）

1. **下载Gradle**（选择一个镜像）：
   - 腾讯云：https://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
   - 阿里云：https://mirrors.aliyun.com/gradle/gradle-8.0-bin.zip

2. **放置文件**：
   - 复制到：`C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\cache\`

3. **解压**：
   - 解压到同一目录

4. **编译**：
   ```cmd
   cd scrcpy/AndroidApp
   gradlew.bat assembleDebug
   ```

---

## ❓ 验证修复

### 测试Gradle是否正确安装

```cmd
cd scrcpy/AndroidApp
gradlew.bat --version
```

如果显示Gradle版本信息，说明安装成功。

### 测试编译

```cmd
gradlew.bat assembleDebug
```

如果显示 "BUILD SUCCESSFUL"，说明成功。

---

## 📁 相关文件

| 文件 | 说明 |
|------|------|
| FIX_GRADLE_ERROR.bat | 自动修复Gradle错误脚本 |
| FIX_GRADLE_ERROR.md | 本文档 |
| GITHUB_UPLOAD_GUIDE.md | GitHub Actions在线编译（推荐） |
| GET_APK_NOW.bat | 快速获取APK指南 |

---

## 🎯 完整操作流程（推荐）

```
方案1: 手动下载Gradle（推荐）

1. 打开浏览器
2. 访问: https://mirrors.cloud.tencent.com/gradle/gradle-8.0-bin.zip
3. 下载 gradle-8.0-bin.zip
4. 创建文件夹: C:\Users\sanelius\.gradle\wrapper\dists\gradle-8.0-bin\cache\
5. 复制文件到该文件夹
6. 解压该文件
7. 打开CMD
8. cd scrcpy/AndroidApp
9. gradlew.bat assembleDebug
10. ✅ 得到APK
```

---

## 🚨 如果所有方法都失败

### 使用GitHub Actions（最简单）

**完全不需要下载Gradle、Java、SDK！**

1. 注册GitHub
2. 创建仓库
3. 上传代码
4. 自动编译
5. 下载APK

详细教程：`GITHUB_UPLOAD_GUIDE.md`

---

## 💡 提示

1. **推荐镜像：** 腾讯云镜像最快
2. **下载工具：** 使用IDM、Thunder等下载工具
3. **网络问题：** 换个时间段下载
4. **最快方法：** GitHub Actions

---

**开始解决吧！** 🚀

**最快：** 直接用GitHub Actions
**手动：** 手动下载Gradle（10分钟）
**自动：** 运行 `FIX_GRADLE_ERROR.bat`
