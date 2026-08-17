package com.odyssey.planner.data

/**
 * 通用「点击式互动」数据模型。
 *
 * 每篇故事的互动由若干 [InteractionStep] 组成，玩家在每一步点击一个
 * [Choice] 来推进；选对进入下一步，选错给出忠于史诗的反馈。
 * 这样所有故事可复用同一套渲染逻辑（StoryInteractionGame），只需配置数据。
 */
data class StoryInteraction(
    /** 与 Story.id 对应，用于导航与查找。 */
    val id: String,
    /** 互动标题（显示在顶部栏与详情页入口按钮）。 */
    val title: String,
    /** 多步抉择。 */
    val steps: List<InteractionStep>,
    /** 通关后的收束文字（点出史诗教训或结局）。 */
    val victoryText: String
)

data class InteractionStep(
    /** 场景大图标（emoji），营造画面感。 */
    val sceneEmoji: String,
    /** 情境 / 指示语。 */
    val prompt: String,
    /** 可点击的选项。 */
    val choices: List<Choice>
)

data class Choice(
    /** 选项图标（emoji）。 */
    val emoji: String,
    /** 选项文字。 */
    val label: String,
    /** 是否为正确选择。 */
    val correct: Boolean,
    /** 选择后的反馈（对错都有）。 */
    val feedback: String
)
