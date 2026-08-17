package com.example.launchmode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * 所有演示界面的基类。
 *
 * 统一负责：
 * 1. 展示当前实例的关键信息（launchMode / taskId / 实例 hash）；
 * 2. 记录 onCreate、onNewIntent 等关键事件到 [LaunchLog]；
 * 3. 提供跳转到四种启动模式的按钮。
 *
 * 观察要点：
 * - **taskId** 变化说明进入了不同的任务栈（singleInstance 常见）。
 * - **instance hash** 未变化说明是复用了旧实例，同时会回调 onNewIntent。
 */
abstract class BaseActivity : AppCompatActivity() {

    /** 子类返回自身声明的 launchMode，仅用于界面展示。 */
    abstract fun launchMode(): String

    private var logView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_mode)

        val name = javaClass.simpleName
        LaunchLog.add("[$name] onCreate  (task=$taskId, instance=${instanceTag()}) —— 创建新实例")
        Log.d(TAG, "[$name] onCreate task=$taskId instance=${instanceTag()}")

        findViewById<TextView>(R.id.tv_title).text = name
        findViewById<TextView>(R.id.tv_info).text = buildString {
            append("launchMode = ").append(launchMode()).append('\n')
            append("taskId = ").append(taskId).append('\n')
            append("instance = ").append(instanceTag())
        }

        logView = findViewById(R.id.tv_log)

        bindJump(R.id.btn_standard, StandardActivity::class.java)
        bindJump(R.id.btn_single_top, SingleTopActivity::class.java)
        bindJump(R.id.btn_single_task, SingleTaskActivity::class.java)
        bindJump(R.id.btn_single_instance, SingleInstanceActivity::class.java)
        bindJump(R.id.btn_main, MainActivity::class.java)

        findViewById<Button>(R.id.btn_clear_log).setOnClickListener {
            LaunchLog.clear()
            refreshLog()
        }

        refreshLog()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val name = javaClass.simpleName
        LaunchLog.add("[$name] onNewIntent  (task=$taskId, instance=${instanceTag()}) —— 复用已有实例")
        Log.d(TAG, "[$name] onNewIntent task=$taskId instance=${instanceTag()}")
        refreshLog()
    }

    override fun onResume() {
        super.onResume()
        // 回到界面时刷新日志，方便观察最新的任务栈变化。
        refreshLog()
    }

    private fun bindJump(id: Int, target: Class<*>) {
        findViewById<Button>(id)?.setOnClickListener {
            startActivity(Intent(this, target))
        }
    }

    private fun refreshLog() {
        logView?.text = LaunchLog.dump()
    }

    /** 用对象身份 hash 作为实例标识，同一实例保持不变。 */
    private fun instanceTag(): String = "@" + Integer.toHexString(System.identityHashCode(this))

    companion object {
        private const val TAG = "LaunchModeDemo"
    }
}
