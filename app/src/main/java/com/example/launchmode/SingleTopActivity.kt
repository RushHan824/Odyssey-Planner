package com.example.launchmode

/**
 * singleTop（栈顶复用模式）
 *
 * 如果目标 Activity 已经位于任务栈顶，则复用该实例并回调 onNewIntent，不再创建新实例；
 * 如果不在栈顶（哪怕栈中存在），仍会创建新实例。
 * 观察：在本界面连续点击"跳转 singleTop"，会看到 onNewIntent 而非 onCreate，instance 保持不变。
 */
class SingleTopActivity : BaseActivity() {
    override fun launchMode(): String = "singleTop"
}
