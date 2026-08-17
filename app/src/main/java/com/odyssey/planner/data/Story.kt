package com.odyssey.planner.data

/**
 * 一段奥德赛故事。
 *
 * 字段设计兼顾 M1（故事集阅读）与后续里程碑：
 * - [latitude] / [longitude] 为 M3「在地图上查看」预留的真实地理坐标；
 * - [characters] 为 M2 端侧 AI 角色对话预留可扮演的人物。
 */
data class Story(
    /** 唯一标识，用于导航与检索。 */
    val id: String,
    /** 在奥德修斯返乡旅程中的顺序。 */
    val order: Int,
    /** 中文标题。 */
    val title: String,
    /** 副标题 / 别称。 */
    val subtitle: String,
    /** 对应荷马史诗的卷次出处。 */
    val book: String,
    /** 神话中的地点名。 */
    val locationName: String,
    /** 学界考据的现实推测地。 */
    val realPlace: String,
    /** 真实坐标（M3 地图使用）。 */
    val latitude: Double,
    val longitude: Double,
    /** 关键人物（M2 AI 对话可扮演）。 */
    val characters: List<String>,
    /** 一句话摘要，用于列表展示。 */
    val summary: String,
    /** 故事正文（考据版）。 */
    val content: String,
    /** 关联的互动彩蛋标识；为 null 表示该故事暂无互动。 */
    val interactiveGame: String? = null
)
