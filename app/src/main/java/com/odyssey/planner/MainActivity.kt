package com.odyssey.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.odyssey.planner.ui.theme.OdysseyTheme

/**
 * 应用唯一入口 Activity，承载整个 Compose 界面树。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OdysseyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    OdysseyApp()
                }
            }
        }
    }
}
