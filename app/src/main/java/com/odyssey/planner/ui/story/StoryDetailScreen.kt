package com.odyssey.planner.ui.story

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.odyssey.planner.data.InteractionRepository
import com.odyssey.planner.data.Story
import com.odyssey.planner.data.StoryRepository

/**
 * 故事详情页：完整正文 + 地理考据信息 + 关键人物。
 * 底部预留 M3「在地图上查看」与 M2「和 AI 角色聊聊」的入口（当前为即将上线状态）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    storyId: String,
    onBack: () -> Unit,
    onStartGame: (String) -> Unit = {},
    onViewMap: (String) -> Unit = {}
) {
    val story = StoryRepository.getById(storyId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = story?.title ?: "故事",
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (story == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("未找到该故事", style = MaterialTheme.typography.titleMedium)
            }
            return@Scaffold
        }

        StoryDetailContent(
            story = story,
            onStartGame = onStartGame,
            onViewMap = onViewMap,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun StoryDetailContent(
    story: Story,
    onStartGame: (String) -> Unit,
    onViewMap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = story.book,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = story.title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = story.subtitle,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
        GeographyCard(story)

        Spacer(modifier = Modifier.height(16.dp))
        SectionLabel("登场人物")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            story.characters.forEach { name ->
                AssistChip(
                    onClick = { /* M2：进入与该角色的 AI 对话 */ },
                    label = { Text(name) },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel("正文")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = story.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // —— 互动彩蛋（独眼巨人为特制版，其余由通用互动引擎驱动）——
        val gameId = story.interactiveGame ?: story.id
        if (story.interactiveGame != null || InteractionRepository.has(story.id)) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionLabel("互动彩蛋")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onStartGame(gameId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Extension, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("开始互动")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        // —— 在地图上查看真实地点（M3）——
        Button(
            onClick = { onViewMap(story.id) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("在地图上查看真实地点")
        }
        Spacer(modifier = Modifier.height(10.dp))
        // —— M2 入口（占位）——
        OutlinedButton(
            onClick = { },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("和 AI 吟游诗人聊聊 · 即将上线")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GeographyCard(story: Story) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(label = "神话地点", value = story.locationName)
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow(label = "现实考据", value = story.realPlace)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "坐标 %.4f, %.4f".format(story.latitude, story.longitude),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label：",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.tertiary
    )
}
