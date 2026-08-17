# 奥德赛 · Odyssey

一款以荷马史诗《奥德赛》为内核的**沉浸式文学故事集** Android App。
以奥德修斯十年返乡的旅程为主线，用严肃考据的内容 + 古希腊美学的界面，
带你重走这段史诗之旅；后续将叠加**端侧 AI 吟游诗人**与**真实地图看点**。

> 仓库：`RushHan824/Odyssey-Planner`

## 产品定位

- **内容内核（严肃考据）**：按旅程顺序组织史诗选段，每篇附人物与地理考据。
- **交互亮点（端侧 AI，规划中）**：与吟游诗人 / 神话角色离线对话。
- **视觉调性**：爱琴海蓝、陶土红、奥林匹斯金、羊皮纸质感，衬线体阅读。
- **轻量地图（规划中）**：故事详情页跳转查看神话地点对应的真实坐标。

## 里程碑

| 里程碑 | 内容 | 状态 |
| --- | --- | --- |
| **M1** | 故事集：列表 + 详情页 + 古希腊美学 UI（纯本地数据） | ✅ 已完成 |
| **M2** | 端侧 AI 吟游诗人：MediaPipe LLM + Gemma，角色对话（模型按需下载） | ⏳ 规划中 |
| **M3** | 轻量地图看点：高德 SDK 展示故事对应的真实地理位置 | ⏳ 规划中 |

## 当前进度（M1）

已内置 7 段考据故事（见 [`StoryRepository`](app/src/main/java/com/odyssey/planner/data/StoryRepository.kt)）：
特洛伊启航 → 食忘忧果者 → 独眼巨人 → 风神皮袋 → 女巫喀耳刻 → 塞壬 → 斯库拉与卡律布狄斯。

## 应用图标

自适应矢量图标（Adaptive Icon）：深爱琴海蓝的渐变海面 + 底部层叠海浪，
金色古希腊双帆船，以及上方一颗指引归乡的星。全部为矢量绘制，无需位图资源。

- 背景：[`ic_launcher_background.xml`](app/src/main/res/drawable/ic_launcher_background.xml)
- 前景：[`ic_launcher_foreground.xml`](app/src/main/res/drawable/ic_launcher_foreground.xml)
- 配置：[`ic_launcher.xml`](app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml)（含 Android 13 主题图标 monochrome 层）

## 技术栈

- **UI**：Jetpack Compose + Material 3
- **导航**：Navigation Compose
- **语言 / 构建**：Kotlin 1.9.24、AGP 8.5.2、compileSdk 34、minSdk 26
- **地图（M3）**：高德地图 Android SDK
- **端侧模型（M2）**：MediaPipe LLM Inference + Gemma（或 AICore + Gemini Nano）

## 代码结构

```
app/src/main/java/com/odyssey/planner/
├── MainActivity.kt          # 入口 Activity，承载 Compose
├── OdysseyApp.kt            # 导航图（列表 -> 详情）
├── data/
│   ├── Story.kt             # 故事模型（预留坐标 / 人物字段）
│   └── StoryRepository.kt   # 内置考据故事数据
└── ui/
    ├── theme/               # 古希腊美学设计系统（Color/Theme/Type）
    └── story/
        ├── StoryListScreen.kt    # 故事集首页
        └── StoryDetailScreen.kt  # 故事详情页
```

- 数据模型：[`Story`](app/src/main/java/com/odyssey/planner/data/Story.kt)
- 列表页：[`StoryListScreen`](app/src/main/java/com/odyssey/planner/ui/story/StoryListScreen.kt)
- 详情页：[`StoryDetailScreen`](app/src/main/java/com/odyssey/planner/ui/story/StoryDetailScreen.kt)
- 主题：[`OdysseyTheme`](app/src/main/java/com/odyssey/planner/ui/theme/Theme.kt)

## 如何运行

1. 用 Android Studio 打开工程根目录。
2. 首次打开会自动同步 Gradle 并补全 Gradle Wrapper。
3. 运行 `app` 模块到模拟器或真机（minSdk 26）。

## 后续注意事项

- **端侧模型体积**：M2 的模型文件通常数百 MB～1GB+，**不能打包进 APK**（Play 有体积限制），
  需在首次启动时按需下载（Play Asset Delivery 或自建 CDN），架构中已为此预留空间。
- **内容准确性**：M2 的 AI 对话建议以「当前故事全文」为上下文（后续升级为本地 RAG），
  确保神话内容忠于考据而非模型臆造。
