package com.odyssey.planner.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * 「独眼巨人逃脱」互动解谜（动画版）。
 *
 * 玩家扮演奥德修斯，通过 3 步抉择重演逃出波吕斐摩斯洞穴的经典桥段。
 * 每一步配有 Canvas 手绘的动态场景插画：巨人扫视的独眼、逼近的火把、
 * 载人逃脱的羊群；通关时则是浪上起伏的归船与闪烁的星。
 */

private data class PuzzleOption(
    val text: String,
    val correct: Boolean,
    val feedback: String
)

private data class PuzzleStep(
    val situation: String,
    val question: String,
    val options: List<PuzzleOption>
)

// —— 场景插画配色（风格化，独立于主题）——
private val CaveDark = Color(0xFF2E2A24)
private val GiantSkin = Color(0xFF9E7B5F)
private val EyeWhite = Color(0xFFF5F0E6)
private val Iris = Color(0xFFC24A2E)
private val GoldTone = Color(0xFFD9B84A)
private val FlameOuter = Color(0xFFE2571E)
private val FlameInner = Color(0xFFF2C14E)
private val Wool = Color(0xFFECE7DC)
private val SheepFace = Color(0xFF6D6459)
private val SeaBlue = Color(0xFF2C6E8F)
private val SkyNight = Color(0xFF163C4E)

private val cyclopsSteps = listOf(
    PuzzleStep(
        situation = "巨石封死了洞口。独眼巨人波吕斐摩斯已吞食了你的两名同伴，此刻他盯着你，瓮声瓮气地问：「陌生人，你叫什么名字？」",
        question = "你如何回答？",
        options = listOf(
            PuzzleOption(
                "骄傲地报上真名——奥德修斯",
                correct = false,
                feedback = "危险！一旦暴露真名，巨人日后便能向他的父亲海神波塞冬指名求得复仇。这正是奥德修斯后来漂泊十年的祸根。"
            ),
            PuzzleOption(
                "我叫「没有人」（Oûtis）",
                correct = true,
                feedback = "妙！记住这个假名——它会在最意想不到的时刻救你一命。"
            ),
            PuzzleOption(
                "我是宙斯派来惩罚你的使者",
                correct = false,
                feedback = "巨人根本不敬神明，他放声嘲笑，反而更加凶残。"
            )
        )
    ),
    PuzzleStep(
        situation = "巨人力大如山，正面搏斗必死无疑；可若杀了他，又没人能搬开堵住洞口的巨石。你身上还带着一皮囊来自伊斯玛洛斯的烈酒。",
        question = "如何制服巨人？",
        options = listOf(
            PuzzleOption(
                "趁他熟睡，一剑刺死他",
                correct = false,
                feedback = "不行！唯有巨人能搬开洞口的巨石，杀了他，你和同伴都会被困死在洞里。"
            ),
            PuzzleOption(
                "用烈酒将他灌醉，再刺瞎他的独眼",
                correct = true,
                feedback = "正是如此！烈酒让巨人沉沉睡去，你们把火烤硬的橄榄木桩狠狠刺入他唯一的眼睛。"
            ),
            PuzzleOption(
                "召集同伴与他正面比力气",
                correct = false,
                feedback = "螳臂当车。你和同伴只会被他一个个抓起吞食。"
            )
        )
    ),
    PuzzleStep(
        situation = "巨人惨叫着失明了，却仍守在唯一的洞口，张开双臂摸索着，要抓住任何想溜出去的人。洞里养着一群肥壮的绵羊。",
        question = "如何逃出洞穴？",
        options = listOf(
            PuzzleOption(
                "趁乱直接从洞口冲出去",
                correct = false,
                feedback = "失明的巨人正守在唯一的出口，一伸手就能抓住冲出去的人。"
            ),
            PuzzleOption(
                "把同伴绑在绵羊腹下，随羊群一起出洞",
                correct = true,
                feedback = "绝妙！巨人只摸得到羊背，摸不到藏在羊腹下的你们。羊群载着你们逃出了生天。"
            ),
            PuzzleOption(
                "等巨人睡着后搬开巨石",
                correct = false,
                feedback = "巨石重逾千钧，凡人之力根本无法撼动。"
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyclopsEscapeGame(
    onBack: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var solved by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var lastCorrect by remember { mutableStateOf(false) }

    val finished = stepIndex >= cyclopsSteps.size

    // 全局循环动画驱动：pulse 用于脉动/扫视，wave 用于位移/起伏
    val transition = rememberInfiniteTransition(label = "scene")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulse"
    )
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "wave"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "互动 · 逃出独眼巨人的洞穴",
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
                VictoryContent(
                    pulse = pulse,
                    wave = wave,
                    onRestart = {
                        stepIndex = 0
                        solved = false
                        feedback = null
                        lastCorrect = false
                    },
                    onBack = onBack
                )
                return@Column
            }

            Text(
                text = "第 ${stepIndex + 1} / ${cyclopsSteps.size} 关",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 场景插画 + 情境文字：随步骤淡入切换
            Crossfade(targetState = stepIndex, animationSpec = tween(500), label = "scene-cross") { idx ->
                Column {
                    SceneCard {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            drawScene(idx, pulse, wave)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(
                            text = cyclopsSteps[idx].situation,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            val step = cyclopsSteps[stepIndex]
            Text(
                text = step.question,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            step.options.forEach { option ->
                OutlinedButton(
                    onClick = {
                        feedback = option.feedback
                        lastCorrect = option.correct
                        if (option.correct) solved = true
                    },
                    enabled = !solved,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(option.text)
                }
            }

            val fb = feedback
            AnimatedVisibility(
                visible = fb != null,
                enter = fadeIn(tween(300)) + expandVertically(tween(300))
            ) {
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
                        stepIndex += 1
                        solved = false
                        feedback = null
                        lastCorrect = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (stepIndex == cyclopsSteps.size - 1) "逃出洞穴！" else "继续")
                }
            }
        }
    }
}

@Composable
private fun SceneCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CaveDark),
        border = BorderStroke(1.dp, GoldTone)
    ) {
        content()
    }
}

@Composable
private fun VictoryContent(
    pulse: Float,
    wave: Float,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SceneCard {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                drawVictoryScene(pulse, wave)
            }
        }
        Text(
            text = "逃脱成功",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "你们藏身羊腹，随羊群逃出了洞穴，登船离岸。",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Text(
                text = "但请记住史诗的教训：奥德修斯在离岸时，因一时骄傲喊出了自己的真名。" +
                    "波吕斐摩斯据此向父亲波塞冬求得复仇——这一声得意的呼喊，换来了此后十年的漂泊。",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(14.dp)
            )
        }
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text("再玩一次")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("返回故事")
        }
    }
}

// ============================ Canvas 绘制 ============================

private fun DrawScope.drawScene(step: Int, pulse: Float, wave: Float) {
    when (step) {
        0 -> drawCyclopsWatching(pulse)
        1 -> drawTorchApproaching(pulse)
        else -> drawSheepEscape(wave)
    }
}

/** 场景 0：独眼巨人警觉地盯着你，瞳孔左右扫视。 */
private fun DrawScope.drawCyclopsWatching(pulse: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h * 0.52f

    drawCircle(color = GiantSkin, radius = h * 0.4f, center = Offset(cx, cy))
    drawLine(
        color = CaveDark,
        start = Offset(cx - h * 0.24f, cy - h * 0.22f),
        end = Offset(cx + h * 0.24f, cy - h * 0.26f),
        strokeWidth = h * 0.05f,
        cap = StrokeCap.Round
    )
    val eyeR = h * 0.17f
    drawCircle(color = EyeWhite, radius = eyeR, center = Offset(cx, cy - h * 0.02f))
    val irisR = h * 0.095f * (0.9f + 0.15f * pulse)
    val scan = (pulse - 0.5f) * w * 0.05f
    drawCircle(color = Iris, radius = irisR, center = Offset(cx + scan, cy - h * 0.02f))
    drawCircle(color = CaveDark, radius = irisR * 0.5f, center = Offset(cx + scan, cy - h * 0.02f))
    drawLine(
        color = CaveDark,
        start = Offset(cx - h * 0.18f, cy + h * 0.26f),
        end = Offset(cx + h * 0.18f, cy + h * 0.26f),
        strokeWidth = h * 0.03f,
        cap = StrokeCap.Round
    )
}

/** 场景 1：火把与尖木桩逼近独眼，火焰随脉动摇曳。 */
private fun DrawScope.drawTorchApproaching(pulse: Float) {
    val w = size.width
    val h = size.height
    val cx = w * 0.42f
    val cy = h * 0.52f

    drawCircle(color = GiantSkin, radius = h * 0.4f, center = Offset(cx, cy))
    val eyeR = h * 0.16f
    drawCircle(color = EyeWhite, radius = eyeR, center = Offset(cx, cy))
    drawCircle(color = Iris, radius = h * 0.08f, center = Offset(cx, cy))
    drawArc(
        color = GiantSkin,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx - eyeR, cy - eyeR),
        size = Size(eyeR * 2f, eyeR * 1.1f)
    )

    val stakeTipX = w * (0.92f - 0.22f * pulse)
    val stakePath = Path().apply {
        moveTo(stakeTipX, cy)
        lineTo(w * 0.98f, cy - h * 0.05f)
        lineTo(w * 0.98f, cy + h * 0.05f)
        close()
    }
    drawPath(stakePath, color = GoldTone)
    val flameH = h * (0.14f + 0.05f * pulse)
    val flame = Path().apply {
        moveTo(stakeTipX, cy - flameH)
        lineTo(stakeTipX - h * 0.06f, cy)
        lineTo(stakeTipX + h * 0.06f, cy)
        close()
    }
    drawPath(flame, color = FlameOuter)
    drawCircle(color = FlameInner, radius = h * 0.03f, center = Offset(stakeTipX, cy - flameH * 0.4f))
}

/** 场景 2：羊群载着藏身腹下的人，缓缓挪向洞口的光。 */
private fun DrawScope.drawSheepEscape(wave: Float) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = FlameInner.copy(alpha = 0.35f),
        radius = h * 0.55f,
        center = Offset(w * 0.05f, h * 0.5f)
    )

    val shift = -wave * w * 0.06f
    val baseY = h * 0.58f
    val spacing = w * 0.27f
    for (i in 0 until 3) {
        val cx = w * 0.3f + i * spacing + shift
        drawSheep(Offset(cx, baseY), h)
    }
}

private fun DrawScope.drawSheep(center: Offset, h: Float) {
    val bodyRx = h * 0.16f
    val bodyRy = h * 0.12f
    drawOval(
        color = Wool,
        topLeft = Offset(center.x - bodyRx, center.y - bodyRy),
        size = Size(bodyRx * 2f, bodyRy * 2f)
    )
    drawCircle(color = SheepFace, radius = h * 0.06f, center = Offset(center.x + bodyRx * 0.85f, center.y - bodyRy * 0.2f))
    val legY = center.y + bodyRy
    listOf(-0.6f, -0.2f, 0.2f, 0.6f).forEach { f ->
        drawLine(
            color = SheepFace,
            start = Offset(center.x + bodyRx * f, legY),
            end = Offset(center.x + bodyRx * f, legY + h * 0.12f),
            strokeWidth = h * 0.025f,
            cap = StrokeCap.Round
        )
    }
    drawCircle(color = GiantSkin, radius = h * 0.045f, center = Offset(center.x - bodyRx * 0.2f, legY + h * 0.02f))
}

/** 通关：归船在浪上起伏，夜空一颗星闪烁。 */
private fun DrawScope.drawVictoryScene(pulse: Float, wave: Float) {
    val w = size.width
    val h = size.height

    drawRect(color = SkyNight, topLeft = Offset(0f, 0f), size = Size(w, h))

    val starC = Offset(w * 0.8f, h * 0.22f)
    val starPath = Path().apply {
        val r = h * 0.09f
        moveTo(starC.x, starC.y - r)
        lineTo(starC.x + r * 0.28f, starC.y - r * 0.28f)
        lineTo(starC.x + r, starC.y)
        lineTo(starC.x + r * 0.28f, starC.y + r * 0.28f)
        lineTo(starC.x, starC.y + r)
        lineTo(starC.x - r * 0.28f, starC.y + r * 0.28f)
        lineTo(starC.x - r, starC.y)
        lineTo(starC.x - r * 0.28f, starC.y - r * 0.28f)
        close()
    }
    drawPath(starPath, color = GoldTone.copy(alpha = 0.5f + 0.5f * pulse))

    val seaTop = h * 0.6f
    drawWave(seaTop, wave, SeaBlue.copy(alpha = 0.9f), h * 0.05f)
    drawWave(seaTop + h * 0.12f, 1f - wave, SeaBlue, h * 0.06f)

    val bob = sin(wave * Math.PI).toFloat() * h * 0.04f
    val shipCx = w * 0.42f
    val shipCy = seaTop - h * 0.02f + bob
    drawShip(Offset(shipCx, shipCy), h)
}

private fun DrawScope.drawWave(y: Float, phase: Float, color: Color, strokeW: Float) {
    val w = size.width
    val path = Path().apply {
        moveTo(0f, y)
        val amp = size.height * 0.03f
        var x = 0f
        while (x <= w) {
            val yy = y + sin((x / w * 4f + phase * 2f) * Math.PI).toFloat() * amp
            lineTo(x, yy)
            x += w / 40f
        }
    }
    drawPath(path, color = color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
}

private fun DrawScope.drawShip(center: Offset, h: Float) {
    val hullW = h * 0.32f
    val hull = Path().apply {
        moveTo(center.x - hullW, center.y)
        quadraticBezierTo(center.x, center.y + h * 0.12f, center.x + hullW, center.y)
        quadraticBezierTo(center.x, center.y + h * 0.05f, center.x - hullW, center.y)
        close()
    }
    drawPath(hull, color = GoldTone)
    drawLine(
        color = GoldTone,
        start = Offset(center.x, center.y),
        end = Offset(center.x, center.y - h * 0.26f),
        strokeWidth = h * 0.02f,
        cap = StrokeCap.Round
    )
    val sail = Path().apply {
        moveTo(center.x + h * 0.01f, center.y - h * 0.24f)
        quadraticBezierTo(center.x + h * 0.16f, center.y - h * 0.14f, center.x + h * 0.02f, center.y - h * 0.02f)
        close()
    }
    drawPath(sail, color = EyeWhite)
}
