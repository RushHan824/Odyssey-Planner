# Activity 四种启动模式 Demo

一个用于直观演示 Android `Activity` 四种启动模式（`launchMode`）的可运行 Demo。
通过界面上的**实例信息**（taskId、instance hash）和**全局日志**（onCreate / onNewIntent），
可以清楚地对比每种模式在任务栈中的行为差异。

## 四种启动模式

| 模式 | 是否复用实例 | 复用时回调 | 任务栈行为 |
| --- | --- | --- | --- |
| `standard` | 否，每次都新建 | 无（总是 onCreate） | 新实例压入当前栈 |
| `singleTop` | 仅当已在**栈顶**时复用 | `onNewIntent` | 栈顶复用，否则新建 |
| `singleTask` | 栈内存在即复用 | `onNewIntent` | 复用并清除其上方所有 Activity |
| `singleInstance` | 全局唯一实例 | `onNewIntent` | 独占一个全新任务栈 |

## 代码结构

- [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml)：为四个 Activity 分别声明不同的 `android:launchMode`。
- [`BaseActivity`](app/src/main/java/com/example/launchmode/BaseActivity.kt)：公共基类，负责展示实例信息、记录事件、提供跳转按钮。
- [`LaunchLog`](app/src/main/java/com/example/launchmode/LaunchLog.kt)：全局日志，跨 Activity 记录完整的跳转链路。
- 四个演示界面：
  - [`StandardActivity`](app/src/main/java/com/example/launchmode/StandardActivity.kt) — `standard`
  - [`SingleTopActivity`](app/src/main/java/com/example/launchmode/SingleTopActivity.kt) — `singleTop`
  - [`SingleTaskActivity`](app/src/main/java/com/example/launchmode/SingleTaskActivity.kt) — `singleTask`
  - [`SingleInstanceActivity`](app/src/main/java/com/example/launchmode/SingleInstanceActivity.kt) — `singleInstance`
- [`MainActivity`](app/src/main/java/com/example/launchmode/MainActivity.kt)：应用入口（默认 `standard`）。

## 如何运行

1. 用 Android Studio 打开本工程根目录。
2. 首次打开会自动同步 Gradle 并生成 Gradle Wrapper（如提示缺少 `gradle-wrapper.jar`，点击 Sync 即可自动补全）。
3. 直接运行 `app` 模块到模拟器或真机。

## 观察方法（关键）

界面上会显示三项信息：
- `launchMode`：当前界面声明的启动模式；
- `taskId`：所属任务栈 ID，**变化说明进入了不同的任务栈**；
- `instance`：实例身份 hash，**不变说明复用了旧实例**（同时日志会打印 `onNewIntent`）。

### 建议的对比实验

1. **standard**：连续点击「跳转 standard」多次
   → 日志持续出现 `onCreate`，`instance` 每次都不同。

2. **singleTop**：先进入 `SingleTopActivity`，再连续点击「跳转 singleTop」
   → 日志出现 `onNewIntent`（而非 `onCreate`），`instance` 保持不变（因为它已在栈顶）。

3. **singleTask**：`Main → SingleTask → Standard`，然后在 Standard 界面点击「跳转 singleTask」
   → `SingleTask` 回调 `onNewIntent`，并清除其上方的 `Standard`（clearTop 效果）。

4. **singleInstance**：进入 `SingleInstanceActivity`
   → 观察其 `taskId` 通常与其它界面不同（独占任务栈）；无论从哪里进入，全局始终只有一个实例。

> 也可以在 Logcat 中过滤 tag `LaunchModeDemo` 查看更详细的生命周期日志。
