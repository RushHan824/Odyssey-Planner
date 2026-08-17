package com.odyssey.planner.data

/**
 * 各篇故事的「点击式互动」配置。
 *
 * 键为 Story.id（独眼巨人 cyclops 使用特制的 CyclopsEscapeGame，不在此表中）。
 * 每则互动都基于荷马史诗的关键抉择设计，选对推进、选错给出忠于原著的后果。
 */
object InteractionRepository {

    private val interactions: Map<String, StoryInteraction> = listOf(
        StoryInteraction(
            id = "troy",
            title = "互动 · 木马计",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🏛️",
                    prompt = "特洛伊城十年不破。作为足智多谋的奥德修斯，你献上什么计策？",
                    choices = listOf(
                        Choice("🐴", "造一只巨大的木马，藏兵智取", true, "正是名垂青史的木马计——以智谋取代蛮力。"),
                        Choice("⚔️", "发动又一次强攻", false, "十年强攻都未能破城，硬拼只会徒增伤亡。"),
                        Choice("🏳️", "放弃，撤军回国", false, "功亏一篑，十年的围城将毫无意义。")
                    )
                ),
                InteractionStep(
                    sceneEmoji = "🐴",
                    prompt = "希腊人佯装撤退。特洛伊人发现了海边的巨大木马——你最希望他们怎么做？",
                    choices = listOf(
                        Choice("🎁", "把它当战利品拖进城中", true, "中计了！夜里藏在马腹中的精锐里应外合，特洛伊陷落。"),
                        Choice("🔥", "当场一把火烧掉", false, "那样藏在马腹里的伏兵就全完了，计策落空。"),
                        Choice("🌊", "把它推进海里", false, "木马沉海，你的奇兵也就无处施展了。")
                    )
                )
            ),
            victoryText = "木马计得手，固若金汤的特洛伊终于陷落。十年归途，就此启程。"
        ),
        StoryInteraction(
            id = "cicones",
            title = "互动 · 喀孔涅斯人",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🍖",
                    prompt = "你们攻破了喀孔涅斯人的城市，掠得财物。接下来该做什么？",
                    choices = listOf(
                        Choice("🚢", "立刻带上战利品扬帆离开", true, "明智！史诗中同伴贪留海边宴饮，才招致惨败。"),
                        Choice("🍷", "留在海边尽情宴饮庆祝", false, "这正是史诗里的致命错误——给了敌人搬来援军的时间。"),
                        Choice("⚔️", "继续深入内陆劫掠", false, "贪得无厌，只会陷入更深的险境。")
                    )
                )
            ),
            victoryText = "克制住贪念、及时撤离——你避开了那场让每船折损六人的反扑。贪婪与放纵，是归途上第一课。"
        ),
        StoryInteraction(
            id = "lotus_eaters",
            title = "互动 · 食忘忧果者",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🌸",
                    prompt = "尝过忘忧果的同伴忘了故乡，只想留下终日食莲。你怎么办？",
                    choices = listOf(
                        Choice("🔗", "强行把他们拖回船，绑在长凳下", true, "对！唯有如此才能带他们逃离“遗忘”的诱惑。"),
                        Choice("🌸", "自己也尝一颗看看", false, "你也会忘却归乡——全军将永远留在这里。"),
                        Choice("⏳", "等他们自己回心转意", false, "忘忧果的魔力下，他们再也不会想离开了。")
                    )
                )
            ),
            victoryText = "你拖回了沉醉的同伴，重新起航。对抗这重考验的不是刀剑，而是不肯遗忘故乡的心。"
        ),
        StoryInteraction(
            id = "aeolus",
            title = "互动 · 风神的皮袋",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🎁",
                    prompt = "风神送你一只封着所有逆风的皮袋。连日掌舵疲惫欲睡，同伴盯着鼓胀的皮袋。你该？",
                    choices = listOf(
                        Choice("🗣️", "提前坦白袋中是逆风，千万别打开", true, "透明的沟通，本可避免那场悲剧。"),
                        Choice("😴", "什么也不说，倒头就睡", false, "史诗正是如此——同伴以为是金银，解开皮袋，狂风将船吹回大海。"),
                        Choice("💰", "炫耀这是神赐的宝物", false, "更会勾起贪念与猜忌，招致灾祸。")
                    )
                )
            ),
            victoryText = "家园已近在眼前。史诗里，一念之差的贪婪与沉默让它得而复失——坦诚与警惕，同样是航海者的美德。"
        ),
        StoryInteraction(
            id = "laestrygonians",
            title = "互动 · 巨人的海港",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "⚓",
                    prompt = "前方是一处入口狭窄、四面环崖的深港。你的船该停在哪里？",
                    choices = listOf(
                        Choice("⚓", "谨慎地停在港外观察", true, "正是这份谨慎救了你——驶入港内的十一艘船无一幸免。"),
                        Choice("🏘️", "随大家驶入深港停泊", false, "食人巨人从崖顶投下巨石，港内的船连人带船尽数覆灭。"),
                        Choice("🔥", "上岸放火示威", false, "无异于惊动整族巨人，自寻死路。")
                    )
                )
            ),
            victoryText = "你停在港外，砍断缆绳逃出生天。十二艘船只剩最后一艘——谨慎，有时就是生与死的距离。"
        ),
        StoryInteraction(
            id = "circe",
            title = "互动 · 女巫喀耳刻",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🌿",
                    prompt = "赫尔墨斯授你一株神草 moly。喀耳刻举起魔杖要把你变成猪。你？",
                    choices = listOf(
                        Choice("🌿", "服下神草，抵御她的魔法", true, "神草护体，魔法对你失效，喀耳刻大惊。"),
                        Choice("🍷", "先痛饮她递来的美酒", false, "那酒掺了魔药——你会和同伴一样变成猪。"),
                        Choice("🗡️", "冲上去就砍", false, "未防魔法便动手，凶多吉少。")
                    )
                ),
                InteractionStep(
                    sceneEmoji = "🤝",
                    prompt = "魔法失效，喀耳刻惊恐臣服。你如何处置她？",
                    choices = listOf(
                        Choice("🤝", "令她起誓不再加害，并救回同伴", true, "智慧的处置——她恢复了同伴的人形，还指点了归途。"),
                        Choice("⚔️", "一剑杀了她", false, "杀了她，被变成猪的同伴就再也变不回来了。"),
                        Choice("🚶", "不管同伴，独自离开", false, "抛下同伴，非英雄所为。")
                    )
                )
            ),
            victoryText = "你制服了女巫，救回同伴。她还指引你：欲归乡，须先航向冥界，问询先知的亡魂。"
        ),
        StoryInteraction(
            id = "underworld",
            title = "互动 · 冥界的问询",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🩸",
                    prompt = "你在世界尽头掘坑献祭，成群渴血的亡魂涌来。你该怎么做？",
                    choices = listOf(
                        Choice("🗡️", "拔剑挡开群鬼，只让先知先饮血", true, "正确！唯有先知忒瑞西阿斯的预言，能指引你归乡。"),
                        Choice("🩸", "让所有亡魂随意饮血", false, "群鬼一拥而上，你将错失先知的预言。"),
                        Choice("🏃", "被吓得转身就逃", false, "半途而废，你仍不知归途何在。")
                    )
                )
            ),
            victoryText = "先知预言了归途与警告：切莫伤害太阳神的牛群。你也在此见到了亡母与故人的魂灵——直面死亡，方知生的可贵。"
        ),
        StoryInteraction(
            id = "sirens",
            title = "互动 · 塞壬的歌声",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🐝",
                    prompt = "致命的塞壬歌声将至。对于划桨的同伴，你先做什么？",
                    choices = listOf(
                        Choice("🐝", "用蜂蜡封住他们的双耳", true, "对！听不见歌声，他们才不会被引向死亡。"),
                        Choice("🎶", "让大家一起欣赏歌声", false, "所有人都会被蛊惑，将船驶向白骨荒岛。"),
                        Choice("🙉", "只是叮嘱他们别听", false, "凡人无法靠意志抗拒塞壬——必须物理隔绝。")
                    )
                ),
                InteractionStep(
                    sceneEmoji = "🪢",
                    prompt = "你自己渴望聆听那“无所不知”的歌声。该如何既听又不至丧命？",
                    choices = listOf(
                        Choice("🪢", "让同伴把你绑在桅杆，严令别松绑", true, "绝妙！你听尽了歌声，却因预先的自我约束而得救。"),
                        Choice("🚶", "自由地站着聆听", false, "你会挣脱着扑向大海，葬身歌声。"),
                        Choice("🙉", "干脆也堵上自己的耳朵", false, "那就体会不到这场“求知”的考验了——但至少你想听。")
                    )
                )
            ),
            victoryText = "你被缚于桅杆，听尽塞壬之歌而安然驶过。面对极致诱惑，事先的自我约束就是你的救赎。"
        ),
        StoryInteraction(
            id = "scylla_charybdis",
            title = "互动 · 双怪之间",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🌀",
                    prompt = "狭窄海峡：一侧是六头怪斯库拉，一侧是吞船巨漩卡律布狄斯。你贴哪一侧走？",
                    choices = listOf(
                        Choice("🐙", "贴斯库拉一侧，牺牲六人保全船", true, "两害相权取其轻——喀耳刻正是如此忠告你的。"),
                        Choice("🌀", "避开怪物，走漩涡一侧", false, "卡律布狄斯会把整条船连人一起吞没，无一生还。"),
                        Choice("⚔️", "停船与斯库拉搏斗", false, "六头怪居高临下，硬拼只会死得更多。")
                    )
                )
            ),
            victoryText = "斯库拉夺走了六名同伴，但船和余众得以生还。“在斯库拉与卡律布狄斯之间”，从此成为两难抉择的代名词。"
        ),
        StoryInteraction(
            id = "helios_cattle",
            title = "互动 · 太阳神的牛群",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🐄",
                    prompt = "被困圣岛、口粮耗尽。饥饿的同伴盯上了太阳神的神牛。作为首领，你坚持？",
                    choices = listOf(
                        Choice("🚫", "绝不碰神牛，另想办法充饥", true, "对！先知与喀耳刻都严令过——伤害神牛必遭灭顶之灾。"),
                        Choice("🍖", "带头宰牛饱餐一顿", false, "这正是史诗的悲剧：宙斯震怒，一道霹雳劈沉全船。"),
                        Choice("😴", "睡一觉，随他们去", false, "疏于约束，同伴便会铸成大错。")
                    )
                )
            ),
            victoryText = "你守住了底线。史诗中，同伴趁你熟睡宰了神牛，招来宙斯的雷霆——除奥德修斯外，无人生还。"
        ),
        StoryInteraction(
            id = "calypso",
            title = "互动 · 卡吕普索之岛",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "♾️",
                    prompt = "女神卡吕普索爱你，许诺让你永生不死、永葆青春，只求你留下。你的选择？",
                    choices = listOf(
                        Choice("⛵", "婉拒永生，坚持返回故乡", true, "这正是全诗对“人之为人”最深的礼赞。"),
                        Choice("♾️", "接受永生，留在岛上", false, "永生虽好，却要以放弃故乡与所爱为代价。"),
                        Choice("😢", "终日以泪洗面，不做决定", false, "史诗里你确实日日垂泪——但心始终朝向伊萨卡。")
                    )
                )
            ),
            victoryText = "你宁要终将一死的凡人妻子，也不要女神的永生。众神动容，终于放你归乡。"
        ),
        StoryInteraction(
            id = "phaeacians",
            title = "互动 · 费阿刻斯人",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🙇",
                    prompt = "海难后你赤身漂上异乡，遇见前来浣衣的公主瑙西卡。你该？",
                    choices = listOf(
                        Choice("🙏", "谦卑有礼地恳求她的庇护", true, "得体的恳求为你赢得了公主的善意与指引。"),
                        Choice("😤", "强硬地索要食物和船", false, "落难之人如此无礼，只会被驱逐。"),
                        Choice("🏃", "躲在灌木里不敢出来", false, "错失求助的机会，你将继续困顿。")
                    )
                ),
                InteractionStep(
                    sceneEmoji = "🎵",
                    prompt = "宫廷宴上，盲眼歌者唱起特洛伊木马的往事，你悄然落泪。此刻你？",
                    choices = listOf(
                        Choice("🗣️", "坦然道出身份，讲述自己的冒险", true, "你的故事打动了众人，他们用快船送你归乡。"),
                        Choice("🤐", "强忍着隐瞒到底", false, "不表明身份，便得不到他们倾力的相助。"),
                        Choice("🚪", "起身默默离席", false, "错过了这难得的归乡良机。")
                    )
                )
            ),
            victoryText = "费阿刻斯人被你的经历打动，赠予厚礼，用神奇的快船连夜将你送回了阔别二十年的伊萨卡。"
        ),
        StoryInteraction(
            id = "ithaca",
            title = "互动 · 重返伊萨卡",
            steps = listOf(
                InteractionStep(
                    sceneEmoji = "🏝️",
                    prompt = "终于回到伊萨卡，宫中却盘踞着上百名求婚者。雅典娜会建议你怎么做？",
                    choices = listOf(
                        Choice("🧓", "乔装成乞丐，暗中查探虚实", true, "隐忍与智谋——这正是奥德修斯的看家本领。"),
                        Choice("⚔️", "立刻表明身份，正面强攻", false, "寡不敌众，鲁莽只会送命。"),
                        Choice("📣", "昭告全岛你回来了", false, "打草惊蛇，求婚者会先下手为强。")
                    )
                ),
                InteractionStep(
                    sceneEmoji = "🏹",
                    prompt = "珀涅罗珀设下比武：谁能拉开你的强弓、一箭射穿十二把斧孔，便嫁给谁。轮到“老乞丐”了。",
                    choices = listOf(
                        Choice("🏹", "挽弓开满，一箭贯穿十二斧", true, "无人能开的强弓在你手中轻松拉满——真相大白的时刻到了。"),
                        Choice("🤷", "假装力弱，拉不开弓", false, "错失亮明身份、清算求婚者的最佳时机。"),
                        Choice("🏃", "放弃比试离开", false, "半途而废，家园将永远落入他人之手。")
                    )
                )
            ),
            victoryText = "一箭之后你褪去伪装，与儿子并肩清算了求婚者；又凭婚床的秘密与珀涅罗珀相认。十年归途，终于圆满。"
        )
    ).associateBy { it.id }

    fun get(id: String): StoryInteraction? = interactions[id]

    fun has(id: String): Boolean = interactions.containsKey(id)
}
