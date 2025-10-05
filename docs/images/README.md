# 图片资源说明

这个目录用于存放项目文档中的图片资源。

## 📁 目录结构

```
docs/images/
├── main_screen.png          # 应用主界面截图
├── marker_details.png       # 标记详情页面截图
├── filter_options.png       # 过滤功能截图
├── location_feature.png     # 定位功能截图
└── README.md               # 本说明文件
```

## 📸 如何添加图片

### 1. 准备图片
- 推荐格式：PNG、JPG、GIF
- 推荐尺寸：宽度 800-1200px
- 文件大小：建议小于 1MB

### 2. 添加图片到文档
在 README.md 中使用以下语法：

```markdown
![图片描述](docs/images/图片文件名.png)
*图片说明文字*
```

### 3. 图片命名规范
- 使用英文和数字
- 使用下划线分隔单词
- 描述性命名，如：`main_screen.png`

## 🎯 建议的截图内容

1. **main_screen.png** - 应用主界面
   - 显示地图和所有标记
   - 显示过滤芯片
   - 显示定位按钮

2. **marker_details.png** - 标记详情
   - 点击标记后的信息窗口
   - 显示标记的详细信息

3. **filter_options.png** - 过滤功能
   - 显示不同过滤器的效果
   - 展示标记的筛选结果

4. **location_feature.png** - 定位功能
   - 显示用户位置标记
   - 展示定位按钮的使用

## 📱 截图工具推荐

- **Android Studio** - 内置模拟器截图
- **adb** - 命令行截图工具
- **手机截图** - 直接在真机上截图

## 🔧 使用 adb 截图

```bash
# 截图到设备
adb shell screencap -p /sdcard/screenshot.png

# 下载到电脑
adb pull /sdcard/screenshot.png docs/images/

# 重命名
mv docs/images/screenshot.png docs/images/main_screen.png
```
