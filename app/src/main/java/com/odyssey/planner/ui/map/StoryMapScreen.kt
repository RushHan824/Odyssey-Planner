package com.odyssey.planner.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.odyssey.planner.data.StoryRepository

/**
 * 故事地点地图页：在高德地图上标出该故事对应的真实地理坐标。
 *
 * 在 Compose 中通过 [AndroidView] 承载高德的传统 [MapView]，
 * 并借助 [DisposableEffect] 将 Activity 生命周期转发给 MapView，
 * 避免地图渲染异常与内存泄漏。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryMapScreen(
    storyId: String,
    onBack: () -> Unit
) {
    val story = StoryRepository.getById(storyId)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 创建并持有 MapView（onCreate 传 null 即可）
    val mapView = remember { MapView(context).apply { onCreate(null) } }

    // 将生命周期事件转发给 MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    // 放置标记并把镜头移动到该地点
    LaunchedEffect(story) {
        story?.let {
            val position = LatLng(it.latitude, it.longitude)
            val aMap = mapView.map
            aMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(it.locationName)
                    .snippet(it.realPlace)
            )
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 9f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = story?.let { "${it.locationName} · 真实地点" } ?: "地图",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
