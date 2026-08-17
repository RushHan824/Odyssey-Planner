package com.example.launchmode

/**
 * standard（标准模式）
 *
 * 每次启动都会创建一个全新的实例并压入当前任务栈，即使栈顶已经是它自己。
 * 观察：连续点击"跳转 standard"，日志中会不断出现新的 onCreate，且 instance 每次都不同。
 */
class StandardActivity : BaseActivity() {
    override fun launchMode(): String = "standard"
}
