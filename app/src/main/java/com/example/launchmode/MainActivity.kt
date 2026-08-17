package com.example.launchmode

/**
 * 应用入口界面，使用默认的 standard 模式。
 * 从这里可以跳转到四种不同 launchMode 的界面进行观察。
 */
class MainActivity : BaseActivity() {
    override fun launchMode(): String = "standard (应用入口)"
}
