package com.example.launchmode

/**
 * singleInstance（单实例模式）
 *
 * 全局唯一实例，并且独占一个全新的任务栈，该栈中不会再有其它 Activity。
 * 观察：进入本界面后 taskId 通常与其它界面不同；再从这里跳转到其它界面时，
 * 其它界面会进入另一个任务栈。全局始终只有一个 SingleInstanceActivity 实例。
 */
class SingleInstanceActivity : BaseActivity() {
    override fun launchMode(): String = "singleInstance"
}
