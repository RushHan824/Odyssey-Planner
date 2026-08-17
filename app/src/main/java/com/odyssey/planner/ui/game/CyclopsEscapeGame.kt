package com.odyssey.planner.ui.game

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

/**
 * 「独眼巨人逃脱」互动解谜。
 *
 * 玩家扮演奥德修斯，通过 3 步抉择重演逃出波吕斐摩斯洞穴的经典桥段：
 * 报上假名 → 灌醉刺目 → 藏身羊腹。选错会给出忠于史诗的后果提示。
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
    // 当前步已答对，进入“可继续”状态
    var solved by remember { mutableStateOf(false) }
    // 选择后的反馈文本（对错复用）
    var feedback by remember { mutableStateOf<String?>(null) }
    var lastCorrect by remember { mutableStateOf(false) }

    val finished = stepIndex >= cyclopsSteps.size

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

            val step = cyclopsSteps[stepIndex]

            Text(
                text = "第 ${stepIndex + 1} / ${cyclopsSteps.size} 关",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Text(
                    text = step.situation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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

            feedback?.let { msg ->
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
                        text = (if (lastCorrect) "✓ " else "✗ ") + msg,
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
private fun VictoryContent(
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⭐ 逃脱成功",
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
