package com.odyssey.planner

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.odyssey.planner.data.InteractionRepository
import com.odyssey.planner.ui.game.CyclopsEscapeGame
import com.odyssey.planner.ui.game.StoryInteractionGame
import com.odyssey.planner.ui.story.StoryDetailScreen
import com.odyssey.planner.ui.story.StoryListScreen

private object Routes {
    const val LIST = "story_list"
    const val DETAIL = "story_detail"
    const val GAME = "story_game"
    const val ARG_STORY_ID = "storyId"
    const val ARG_GAME_ID = "gameId"
}

/**
 * 应用导航图：故事列表 -> 故事详情。
 */
@Composable
fun OdysseyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(Routes.LIST) {
            StoryListScreen(
                onStoryClick = { storyId ->
                    navController.navigate("${Routes.DETAIL}/$storyId")
                }
            )
        }
        composable(
            route = "${Routes.DETAIL}/{${Routes.ARG_STORY_ID}}",
            arguments = listOf(navArgument(Routes.ARG_STORY_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString(Routes.ARG_STORY_ID).orEmpty()
            StoryDetailScreen(
                storyId = storyId,
                onBack = { navController.popBackStack() },
                onStartGame = { gameId ->
                    navController.navigate("${Routes.GAME}/$gameId")
                }
            )
        }
        composable(
            route = "${Routes.GAME}/{${Routes.ARG_GAME_ID}}",
            arguments = listOf(navArgument(Routes.ARG_GAME_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString(Routes.ARG_GAME_ID).orEmpty()
            if (gameId == "cyclops_escape") {
                CyclopsEscapeGame(onBack = { navController.popBackStack() })
            } else {
                val interaction = InteractionRepository.get(gameId)
                if (interaction != null) {
                    StoryInteractionGame(
                        interaction = interaction,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    CyclopsEscapeGame(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
