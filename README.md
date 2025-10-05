# iSpot - 基于高德地图的社交发现应用

iSpot是一款基于高德地图API的Android社交应用，专注于帮助用户发现附近的人、活动和兴趣点，打造本地化的社交体验。

## 🌟 功能特点

### 核心功能
- **🗺️ 地图显示**：使用高德地图3D SDK显示交互式地图
- **👥 附近的人**：发现并查看附近用户的标记
- **🎯 附近的活动**：浏览本地活动
- **📍 兴趣点**：探索附近的咖啡厅、公园、图书馆等地点
- **🔍 智能过滤**：通过过滤器芯片快速筛选不同类型的标记
- **📍 实时定位**：获取用户当前位置并在地图上显示

### 交互特性
- **多选过滤**：支持同时选择多种类型的标记显示
- **标记详情**：点击标记查看详细信息
- **地图导航**：支持缩放、平移等地图操作
- **一键定位**：快速回到用户当前位置

## 🛠️ 技术栈

### 核心技术
- **Android (Kotlin)** - 主要开发语言
- **高德地图SDK** - 3D地图、定位、搜索服务
- **Android Jetpack** - ViewModel、LiveData、ViewBinding
- **Material Design 3** - 现代化UI设计

### 架构模式
- **MVVM架构** - Model-View-ViewModel模式
- **单Activity多Fragment** - 简化的导航结构
- **响应式编程** - 使用LiveData进行数据绑定

## 📱 应用截图

### 主界面
![主界面](https://github.com/ECHOzdjd/iSpot/blob/main/docs/images/04be05c24df7bf1ecbbc36c06b4269d8.jpg）
*应用主界面，显示地图和过滤选项*

### 标记详情
![标记详情](docs\images\5332789f6a4f379636ecece3869f910f.jpg)
![](docs\images\af1bfa5980e6612b36ef716e5fd45539.jpg)
*点击标记查看详细信息*

### 过滤功能
![过滤功能](docs\images\b6ed342a230e4bbce68bd7d44a4da3a6.jpg)
*使用过滤器筛选不同类型的标记*



---

### 环境要求
- **Android Studio** 4.2+
- **Android SDK** API 21+ (Android 5.0)
- **JDK** 8+
- **高德地图API Key**

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd ispot
   ```

2. **配置高德地图API Key**
   
   在 `app/src/main/AndroidManifest.xml` 中配置：
   ```xml
   <meta-data
       android:name="com.amap.api.v2.apikey"
       android:value="YOUR_AMAP_API_KEY" />
   ```
   
   在 `app/src/main/java/com/example/ispot/ISpotApplication.kt` 中配置：
   ```kotlin
   AMapLocationClient.setApiKey("YOUR_AMAP_API_KEY")
   ```

3. **获取API Key**
   - 访问 [高德开放平台](https://lbs.amap.com/)
   - 注册开发者账号
   - 创建应用并获取API Key


4. **构建运行**
   ```bash
   ./gradlew installDebug
   ```

## 📖 使用指南

### 基本操作

1. **启动应用**
   - 应用启动后自动显示地区地图
   - 默认显示所有类型的标记（人员、活动、兴趣点）

2. **过滤标记**
   - 点击顶部的"附近的人"芯片显示人员标记
   - 点击"附近的活动"芯片显示活动标记
   - 点击"兴趣点"芯片显示地点标记
   - 支持多选，可同时显示多种类型

3. **查看详情**
   - 点击任意标记查看详细信息
   - 标记会显示标题和描述信息

4. **定位功能**
   - 点击右下角的定位按钮
   - 应用会请求位置权限
   - 获取权限后显示用户当前位置

### 权限说明
- **位置权限**：用于获取用户当前位置和显示定位标记
- **网络权限**：用于加载地图数据和API请求

## 🏗️ 项目结构

```
app/
├── src/main/
│   ├── java/com/example/ispot/
│   │   ├── ISpotApplication.kt          # 应用入口，SDK初始化
│   │   ├── ui/
│   │   │   ├── MainActivity.kt          # 主Activity
│   │   │   └── map/
│   │   │       ├── MapFragment.kt       # 地图Fragment
│   │   │       └── MapViewModel.kt      # 地图数据管理
│   │   └── data/
│   │       └── model/
│   │           ├── MapMarker.kt         # 地图标记数据模型
│   │           └── Place.kt             # 地点数据模型
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml        # 主Activity布局
│   │   │   └── fragment_map.xml         # 地图Fragment布局
│   │   ├── drawable/
│   │   │   ├── ic_person_marker.xml     # 人员标记图标
│   │   │   ├── ic_activity_marker.xml   # 活动标记图标
│   │   │   └── ic_spot_marker.xml       # 兴趣点标记图标
│   │   ├── navigation/
│   │   │   └── nav_graph.xml            # 导航图
│   │   ├── menu/
│   │   │   └── bottom_nav_menu.xml      # 底部导航菜单
│   │   ├── values/
│   │   │   ├── strings.xml              # 字符串资源
│   │   │   ├── colors.xml               # 颜色资源
│   │   │   └── themes.xml               # 主题样式
│   │   └── xml/
│   │       └── network_security_config.xml # 网络安全配置
│   └── AndroidManifest.xml              # 应用清单文件
├── build.gradle                         # 模块构建配置
└── proguard-rules.pro                   # 代码混淆规则
```

## 🎯 核心功能实现

### 地图标记系统
- **标记类型**：人员、活动、兴趣点三种类型
- **标记图标**：使用Material Design图标
- **标记交互**：点击显示详情信息

### 过滤系统
- **多选过滤**：支持同时选择多种标记类型
- **实时更新**：过滤条件变化时实时更新地图显示
- **状态保持**：过滤状态在Fragment生命周期中保持

### 定位系统
- **权限管理**：自动请求和管理位置权限
- **位置获取**：使用高德定位SDK获取精确位置
- **位置显示**：在地图上显示用户当前位置标记

## 🔧 配置说明

### 高德地图配置
```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="YOUR_API_KEY" />

<!-- 权限配置 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 网络安全配置
```xml
<!-- network_security_config.xml -->
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">*.amap.com</domain>
    </domain-config>
</network-security-config>
```

## 🐛 故障排除

### 常见问题

1. **地图不显示**
   - 检查API Key是否正确配置
   - 确认网络连接正常
   - 验证高德地图服务是否已开通

2. **定位失败**
   - 检查位置权限是否已授予
   - 确认设备GPS是否开启
   - 验证API Key是否包含定位服务


