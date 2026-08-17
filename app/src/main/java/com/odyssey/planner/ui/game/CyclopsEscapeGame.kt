package com.odyssey.planner.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * 「独眼巨人逃脱」互动解谜（点击操作版）。
 *
 * 不再是文字选择题，而是直接点击画面元素来推进：
 *  - 第 1 步：点击漂浮的名字气泡，选择该向巨人报出的名字；
 *  - 第 2 步：连点酒袋灌醉巨人（眼睛随之渐渐闭上），再点火尖木桩刺瞎独眼；
 *  - 第 3 步：逐一点击绵羊，把同伴藏到羊腹下，随羊群溜出洞口。
 * 通关后是浪上起伏的归船与闪烁的星。
 */

// —— 场景配色 ——
private val CaveDark = Color(0xFF2E2A24)
private val GiantSkin = Color(0xFF9E7B5F)
private val EyeWhite = Color(0xFFF5F0E6)
private val Iris = Color(0xFFC24A2E)
private val GoldTone = Color(0xFFD9B84A)
private val BloodRed = Color(0xFFB3261E)
private val FlameInner = Color(0xFFF2C14E)
private val SeaBlue = Color(0xFF2C6E8F)
private val SkyNight = Color(0xFF163C4E)

private const val DRINKS_NEEDED = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyclopsEscapeGame(
    onBack: () -> Unit
) {
    // 关卡：0 报名 / 1 灌醉刺目 / 2 藏羊逃脱 / 3 通关
    var stage by remember { mutableIntStateOf(0) }

    // 关 1
    var nameChosen by remember { mutableStateOf(false) }
    // 关 2
    var drinkCount by remember { mutableIntStateOf(0) }
    var blinded by remember { mutableStateOf(false) }
    // 关 3
    val hiddenSheep = remember { mutableStateListOf<Int>() }
    var sheepEscaped by remember { mutableStateOf(false) }

    var hint by remember { mutableStateOf<String?>(null) }

    val drunk = drinkCount >= DRINKS_NEEDED

    // —— 动画驱动 ——
    val inf = rememberInfiniteTransition(label = "inf")
    val pulse by inf.animateFloat(
        initialValue = 1f, targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(680), RepeatMode.Reverse), label = "pulse"
    )
    val wave by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "wave"
    )

    // 巨人眼睛睁开程度：喝酒渐闭，醉倒全闭
    val targetEye = when {
        stage >= 2 || blinded -> 0f
        stage == 1 -> (1f - drinkCount * (1f / DRINKS_NEEDED)).coerceIn(0f, 1f)
        else -> 1f
    }
    val eyeOpen by animateFloatAsState(targetEye, tween(450), label = "eye")

    // 羊群溜出洞口的位移（-1 表示已移出）
    val sheepShift by animateFloatAsState(
        if (sheepEscaped) -1f else 0f, tween(1400), label = "sheepShift"
    )

    // 羊群逃出后，稍候进入通关
    LaunchedEffect(sheepEscaped) {
        if (sheepEscaped) {
            kotlinx.coroutines.delay(1500)
            stage = 3
        }
    }

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
            if (stage >= 3) {
                VictoryContent(pulse = pulse, wave = wave, onRestart = {
                    stage = 0; nameChosen = false; drinkCount = 0; blinded = false
                    hiddenSheep.clear(); sheepEscaped = false; hint = null
                }, onBack = onBack)
                return@Column
            }

            // 关卡进度
            Text(
                text = "第 ${stage + 1} / 3 关",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 舞台：Canvas 画巨人的反应（睁眼 / 渐闭 / 刺瞎）
            SceneCard {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    drawGiant(eyeOpen = eyeOpen, blinded = blinded, showCaveLight = stage == 2)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 指示语
            Text(
                text = instructionFor(stage, drinkCount, drunk, blinded, hiddenSheep.size),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))

            // —— 各关的点击交互区 ——
            when (stage) {
                0 -> StageChooseName(
                    pulse = pulse,
                    onPick = { correct, feedback ->
                        hint = feedback
                        if (correct) nameChosen = true
                    },
                    solved = nameChosen
                )

                1 -> StageDrinkAndBlind(
                    pulse = pulse,
                    drunk = drunk,
                    blinded = blinded,
                    onDrink = { if (!drunk) drinkCount += 1 },
                    onStab = { blinded = true },
                    onDagger = { hint = "杀不得！只有巨人能搬开洞口的巨石，杀了他你们会被困死洞中。" }
                )

                2 -> StageHideSheep(
                    pulse = pulse,
                    shift = sheepShift,
                    hidden = hiddenSheep,
                    escaped = sheepEscaped,
                    onHide = { i -> if (!hiddenSheep.contains(i)) hiddenSheep.add(i) },
                    onEscape = { sheepEscaped = true }
                )
            }

            // 反馈提示
            val h = hint
            AnimatedVisibility(visible = h != null, enter = fadeIn(tween(250))) {
                if (h != null) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = h,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }

            // 关 1 完成后的“继续”；关 2 刺瞎后的“继续”
            val canAdvance = (stage == 0 && nameChosen) || (stage == 1 && blinded)
            if (canAdvance) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { stage += 1; hint = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("继续")
                }
            }
        }
    }
}

private fun instructionFor(
    stage: Int, drinkCount: Int, drunk: Boolean, blinded: Boolean, hiddenCount: Int
): String = when (stage) {
    0 -> "巨人瓮声问：「陌生人，你叫什么名字？」—— 点击一个名字气泡回答他。"
    1 -> when {
        blinded -> "独眼已被刺瞎！他惨叫着失明了。"
        drunk -> "巨人醉倒了！趁现在，点击火尖木桩刺向他的独眼。"
        else -> "点击酒袋灌醉巨人（$drinkCount / $DRINKS_NEEDED）。"
    }
    2 -> if (hiddenCount >= 3) "都藏好了！点击“随羊群溜出洞口”。"
    else "失明的巨人守在洞口。点击绵羊，把同伴一个个藏到羊腹下（$hiddenCount / 3）。"
    else -> ""
}

// ============================ 各关交互区 ============================

@Composable
private fun StageChooseName(
    pulse: Float,
    onPick: (Boolean, String) -> Unit,
    solved: Boolean
) {
    val names = listOf(
        Triple("奥德修斯", false, "危险！暴露真名，巨人日后就能向父亲波塞冬指名复仇——这正是十年漂泊的祸根。"),
        Triple("「没有人」", true, "妙！记住这个假名，它会在最意想不到的时刻救你一命。"),
        Triple("宙斯的使者", false, "巨人根本不敬神明，他放声嘲笑，反而更加凶残。")
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        names.forEach { (label, correct, feedback) ->
            val highlight = solved && correct
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(if (highlight) pulse else 1f)
                    .clickable(enabled = !solved) { onPick(correct, feedback) },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (highlight) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    text = "「$label」",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun StageDrinkAndBlind(
    pulse: Float,
    drunk: Boolean,
    blinded: Boolean,
    onDrink: () -> Unit,
    onStab: () -> Unit,
    onDagger: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
    ) {
        if (!drunk) {
            EmojiButton("🍷", pulse = pulse, onClick = onDrink)
            EmojiButton("🗡️", pulse = 1f, onClick = onDagger)
        } else if (!blinded) {
            EmojiButton("🔥", pulse = pulse, onClick = onStab)
        } else {
            Text("🩸", fontSize = 56.sp)
        }
    }
}

@Composable
private fun StageHideSheep(
    pulse: Float,
    shift: Float,
    hidden: List<Int>,
    escaped: Boolean,
    onHide: (Int) -> Unit,
    onEscape: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (shift * 90f).dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            for (i in 0 until 3) {
                val isHidden = hidden.contains(i)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🐑",
                        fontSize = 52.sp,
                        modifier = Modifier
                            .scale(if (!isHidden) pulse else 1f)
                            .clickable(enabled = !isHidden && !escaped) { onHide(i) }
                    )
                    // 藏在羊腹下的同伴
                    Text(
                        text = if (isHidden) "🧍" else "　",
                        fontSize = 22.sp
                    )
                }
            }
        }
        if (hidden.size >= 3 && !escaped) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEscape, modifier = Modifier.fillMaxWidth()) {
                Text("随羊群溜出洞口")
            }
        }
    }
}

@Composable
private fun EmojiButton(emoji: String, pulse: Float, onClick: () -> Unit) {
    Text(
        text = emoji,
        fontSize = 56.sp,
        modifier = Modifier
            .scale(pulse)
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}

// ============================ 通用 & 通关 ============================

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

/** 巨人的脸：eyeOpen 控制独眼睁闭；blinded 时画红色叉；stage2 加洞口光。 */
private fun DrawScope.drawGiant(eyeOpen: Float, blinded: Boolean, showCaveLight: Boolean) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h * 0.52f

    if (showCaveLight) {
        drawCircle(
            color = FlameInner.copy(alpha = 0.3f),
            radius = h * 0.6f,
            center = Offset(w * 0.06f, h * 0.5f)
        )
    }

    // 头
    drawCircle(color = GiantSkin, radius = h * 0.4f, center = Offset(cx, cy))
    // 眉
    drawLine(
        color = CaveDark,
        start = Offset(cx - h * 0.24f, cy - h * 0.22f),
        end = Offset(cx + h * 0.24f, cy - h * 0.26f),
        strokeWidth = h * 0.05f,
        cap = StrokeCap.Round
    )

    val eyeCenter = Offset(cx, cy - h * 0.02f)
    if (blinded) {
        // 刺瞎：红色的叉
        val r = h * 0.15f
        drawLine(BloodRed, Offset(eyeCenter.x - r, eyeCenter.y - r), Offset(eyeCenter.x + r, eyeCenter.y + r), strokeWidth = h * 0.04f, cap = StrokeCap.Round)
        drawLine(BloodRed, Offset(eyeCenter.x + r, eyeCenter.y - r), Offset(eyeCenter.x - r, eyeCenter.y + r), strokeWidth = h * 0.04f, cap = StrokeCap.Round)
    } else if (eyeOpen <= 0.08f) {
        // 闭眼：一条弧线
        drawLine(
            color = CaveDark,
            start = Offset(eyeCenter.x - h * 0.16f, eyeCenter.y),
            end = Offset(eyeCenter.x + h * 0.16f, eyeCenter.y),
            strokeWidth = h * 0.035f,
            cap = StrokeCap.Round
        )
    } else {
        // 睁眼：眼白（高度随 eyeOpen）+ 虹膜 + 瞳孔
        val eyeRx = h * 0.17f
        val eyeRy = h * 0.17f * eyeOpen
        drawOval(
            color = EyeWhite,
            topLeft = Offset(eyeCenter.x - eyeRx, eyeCenter.y - eyeRy),
            size = Size(eyeRx * 2f, eyeRy * 2f)
        )
        val irisR = (h * 0.09f * eyeOpen).coerceAtLeast(h * 0.03f)
        drawCircle(color = Iris, radius = irisR, center = eyeCenter)
        drawCircle(color = CaveDark, radius = irisR * 0.5f, center = eyeCenter)
    }

    // 嘴
    drawLine(
        color = CaveDark,
        start = Offset(cx - h * 0.18f, cy + h * 0.26f),
        end = Offset(cx + h * 0.18f, cy + h * 0.26f),
        strokeWidth = h * 0.03f,
        cap = StrokeCap.Round
    )
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
    drawShip(Offset(w * 0.42f, seaTop - h * 0.02f + bob), h)
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
