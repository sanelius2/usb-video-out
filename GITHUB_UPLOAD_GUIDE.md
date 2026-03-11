# 🚀 5分钟编译APK - GitHub Actions方法

这是**最简单、最快、完全免费**的方法，无需安装任何软件！

---

## 📝 准备工作

### 1. 注册GitHub账号（1分钟）
- 访问：https://github.com/signup
- 填写信息，验证邮箱
- 完成注册

### 2. 创建仓库（1分钟）
1. 登录GitHub
2. 点击右上角 "+" → "New repository"
3. 填写：
   - Repository name: `usb-video-out`
   - Description: `Android USB视频输出应用`
   - 选择 `Public`（公开，免费）
4. 点击 "Create repository"

---

## 📤 上传代码到GitHub（2分钟）

### 方法A：通过GitHub网页上传（最简单）

1. 在新创建的仓库页面
2. 点击 "uploading an existing file"
3. 选择 `scrcpy/AndroidApp` 文件夹内的**所有文件**
4. 等待上传完成
5. 在底部输入提交信息：
   ```
   Initial commit - Android USB Video Output App
   ```
6. 点击 "Commit changes"

### 方法B：使用Git命令行（推荐给有经验的用户）

```bash
cd c:/Users/sanelius/Desktop/安卓转c/scrcpy

git init
git add AndroidApp
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/你的用户名/usb-video-out.git
git push -u origin main
```

---

## 🔄 自动触发编译（无需任何操作！）

上传完成后，GitHub会**自动开始编译**！

### 查看编译进度
1. 进入你的仓库页面
2. 点击顶部的 "Actions" 标签
3. 你会看到一个正在运行的工作流
4. 等待2-5分钟

---

## 📥 下载APK（1分钟）

编译完成后：

1. 在 "Actions" 页面
2. 点击最新的工作流（通常是 "Build Android APK"）
3. 滚动到底部 "Artifacts" 部分
4. 你会看到两个APK：
   - **app-debug-apk** - 调试版本（推荐用于测试）
   - **app-release-apk** - 发布版本

5. 点击 "app-debug-apk"
6. 解压下载的zip文件
7. 得到 `app-debug.apk`

---

## ✅ 完成！

现在你有了APK文件，可以安装到手机了！

### 安装方法

**方法1：ADB安装**
```bash
adb devices
adb install app-debug.apk
```

**方法2：直接安装**
1. 将APK文件发送到手机（微信、QQ、数据线等）
2. 在手机上点击APK
3. 允许安装未知来源应用
4. 完成安装

---

## 🎯 完整流程总结

```
注册GitHub → 创建仓库 → 上传代码 → 自动编译 → 下载APK → 安装到手机
    1分钟      1分钟        2分钟      2-5分钟    1分钟        1分钟
                     总计：约8-10分钟
```

---

## 🔧 手动触发编译（可选）

如果想重新编译：

1. 进入仓库 → Actions 标签
2. 选择 "Build Android APK" 工作流
3. 点击右侧 "Run workflow"
4. 选择分支，点击 "Run workflow" 按钮
5. 等待编译完成
6. 下载新的APK

---

## 💡 提示

1. **编译速度**：首次编译需要下载依赖，约5分钟；后续只需1-2分钟
2. **APK大小**：Debug版约5-8MB，包含调试信息
3. **自动编译**：每次推送代码都会自动编译
4. **免费使用**：GitHub Actions对公开仓库完全免费
5. **保留30天**：APK会自动保存30天，过期后需要重新编译

---

## ❓ 常见问题

**Q: 上传太慢怎么办？**
A: 使用Git命令行上传更快，或者压缩后上传再解压

**Q: 编译失败了？**
A: 查看Actions页面红色叉号的详细日志，检查代码是否完整

**Q: 找不到Actions标签？**
A: Actions可能需要几秒钟才能出现，刷新页面

**Q: 可以私有仓库吗？**
A: 可以，但私有仓库每月有免费额度限制（2000分钟）

**Q: APK安装失败？**
A: 确保手机允许安装未知来源应用，Android 11+需要额外授权

---

## 🎉 恭喜！

你现在已经学会了使用GitHub Actions编译Android APK！

这个方法：
- ✅ 完全免费
- ✅ 无需安装软件
- ✅ 自动化编译
- ✅ 随时重新编译
- ✅ 版本管理

享受你的APK吧！📱✨
