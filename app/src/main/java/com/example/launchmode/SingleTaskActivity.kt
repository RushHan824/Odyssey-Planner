package com.example.launchmode

/**
 * singleTask（栈内复用模式）
 *
 * 任务栈中只允许存在一个实例。若栈中已存在，则复用该实例并回调 onNewIntent，
 * 同时清除其上方的所有 Activity（clearTop 效果）。
 * 观察：Main -> SingleTask -> Standard -> 再次跳转 SingleTask，
 * 上方的 Standard 会被销毁，SingleTask 回调 onNewIntent 回到栈中原位置。
 */
class SingleTaskActivity : BaseActivity() {
    override fun launchMode(): String = "singleTask"
}
