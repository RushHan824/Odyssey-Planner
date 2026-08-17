package com.odyssey.planner

import android.app.Application
import com.amap.api.maps.MapsInitializer

/**
 * 应用 Application。
 *
 * 高德地图 SDK（2021 年后版本）强制要求在使用前完成隐私合规声明，
 * 否则地图不显示甚至崩溃。这里在进程启动时统一声明"已展示并同意隐私政策"。
 *
 * 注意：正式上架前，应确保 App 内确有对用户展示隐私政策的入口，
 * 再据用户选择调用 updatePrivacyAgree，以符合合规要求。
 */
class OdysseyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 声明隐私政策已弹窗展示、且用户已同意
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
    }
}
