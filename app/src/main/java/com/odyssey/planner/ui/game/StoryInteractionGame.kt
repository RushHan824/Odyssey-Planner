package com.odyssey.planner.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odyssey.planner.data.Choice
import com.odyssey.planner.data.StoryInteraction

/**
 * 通用「点击式互动」渲染器：所有非特制故事的互动都由它驱动。
 *
 * 交互：每步显示一个场景 emoji（脉动浮动）+ 情境 + 若干可点击选项；
 * 点对推进到下一步，点错给出反馈；全部通过后进入通关页。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryInteractionGame(
    interaction: StoryInteraction,
    onBack: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var solved by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var lastCorrect by remember { mutableStateOf(false) }

    val finished = stepIndex >= interaction.steps.size

    val inf = rememberInfiniteTransition(label = "inf")
    val pulse by inf.animateFloat(
        initialValue = 1f, targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = interaction.title,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (finished) {
                VictoryPanel(
                    pulse = pulse,
                    victoryText = interaction.victoryText,
                    onRestart = {
                        stepIndex = 0; solved = false; feedback = null; lastCorrect = false
                    },
                    onBack = onBack
                )
                return@Column
            }

            Text(
                text = "第 ${stepIndex + 1} / ${interaction.steps.size} 步",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 场景 emoji + 情境：随步骤淡入切换
            Crossfade(targetState = stepIndex, animationSpec = tween(450), label = "step") { idx ->
                val step = interaction.steps[idx]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = step.sceneEmoji,
                                fontSize = 64.sp,
                                modifier = Modifier.scale(pulse)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = step.prompt,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 选项（当前步）
            val step = interaction.steps[stepIndex]
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                step.choices.forEach { choice ->
                    ChoiceButton(
                        choice = choice,
                        enabled = !solved,
                        pulse = pulse,
                        onClick = {
                            feedback = choice.feedback
                            lastCorrect = choice.correct
                            if (choice.correct) solved = true
                        }
                    )
                }
            }

            // 反馈
            val fb = feedback
            AnimatedVisibility(visible = fb != null, enter = fadeIn(tween(250))) {
                if (fb != null) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = if (lastCorrect) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Text(
                                text = (if (lastCorrect) "✓ " else "✗ ") + fb,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (lastCorrect) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                },
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }

            if (solved) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        stepIndex += 1; solved = false; feedback = null; lastCorrect = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (stepIndex == interaction.steps.size - 1) "完成" else "继续")
                }
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    choice: Choice,
    enabled: Boolean,
    pulse: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = choice.emoji,
                fontSize = 30.sp,
                modifier = Modifier.scale(if (enabled) pulse else 1f)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = choice.label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun VictoryPanel(
    pulse: Float,
    victoryText: String,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⭐", fontSize = 72.sp, modifier = Modifier.scale(pulse))
            }
        }
        Text(
            text = "这一幕，你走对了",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = victoryText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text("再玩一次")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("返回故事")
        }
    }
}
