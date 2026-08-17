package com.example.launchmode

/**
 * 全局启动日志。
 *
 * 记录所有 Activity 的生命周期与跳转事件，使得在任意界面都能看到完整的
 * 任务栈行为链路，便于直观对比四种 launchMode 的差异。
 */
object LaunchLog {

    private val logs = mutableListOf<String>()

    fun add(line: String) {
        logs.add(line)
    }

    fun clear() {
        logs.clear()
    }

    fun dump(): String {
        if (logs.isEmpty()) {
            return "（暂无记录，点击下方按钮开始跳转观察）"
        }
        return logs.mapIndexed { index, item -> "${index + 1}. $item" }
            .joinToString("\n")
    }
}
