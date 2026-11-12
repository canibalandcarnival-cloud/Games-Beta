package com.example

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavigationTab
import com.example.ui.GamesViewModel
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.AddFriendDialog
import com.example.ui.components.ChallengeFriendDialog
import com.example.ui.components.FloatingFrostedNavBar
import com.example.ui.components.FullscreenGameSimulation
import com.example.ui.components.GameDetailSheet
import com.example.ui.components.GooglePlayGamesDialog
import com.example.ui.screens.ArcadeScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GamesApp()
            }
        }
    }
}

@Composable
fun GamesApp(
    viewModel: GamesViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()
    val showProfileDialog by viewModel.showProfileDialog.collectAsState()
    val showChallengeDialog by viewModel.showChallengeDialog.collectAsState()
    val challengeTargetFriend by viewModel.challengeTargetFriend.collectAsState()
    val showAchievementsDialog by viewModel.showAchievementsDialog.collectAsState()
    val showAddFriendDialog by viewModel.showAddFriendDialog.collectAsState()
    val playProfile by viewModel.playProfile.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val gameModeConfig by viewModel.gameModeConfig.collectAsState()

    // Sync sustained performance mode with Android Window API on supported devices
    LaunchedEffect(gameModeConfig.sustainedPerformanceScaling) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                (context as? Activity)?.window?.setSustainedPerformanceMode(
                    gameModeConfig.sustainedPerformanceScaling
                )
            } catch (_: Exception) {}
        }
    }

    // Handle Back Press gracefully
    BackHandler(
        enabled = gameModeConfig.isFullscreen ||
                selectedGame != null ||
                showProfileDialog ||
                showChallengeDialog ||
                showAchievementsDialog ||
                showAddFriendDialog ||
                currentTab != NavigationTab.HOME
    ) {
        when {
            gameModeConfig.isFullscreen -> viewModel.exitFullscreenGameMode()
            selectedGame != null -> viewModel.closeGameDetails()
            showProfileDialog -> viewModel.closeProfile()
            showChallengeDialog -> viewModel.closeChallengeDialog()
            showAchievementsDialog -> viewModel.closeAchievements()
            showAddFriendDialog -> viewModel.closeAddFriendDialog()
            currentTab != NavigationTab.HOME -> viewModel.selectTab(NavigationTab.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Tab Content with fluid transition
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                if (forward) {
                    (slideInHorizontally { width -> width / 4 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width / 4 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> width / 4 } + fadeOut())
                }
            },
            label = "tab_transition",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                NavigationTab.ARCADE -> ArcadeScreen(viewModel = viewModel)
                NavigationTab.FRIENDS -> FriendsScreen(viewModel = viewModel)
                NavigationTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                NavigationTab.SEARCH -> SearchScreen(viewModel = viewModel)
            }
        }

        // Translucent Floating Frosted Glass Bottom Navigation Bar
        FloatingFrostedNavBar(
            currentTab = currentTab,
            onTabSelected = { viewModel.selectTab(it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Google Play Games Profile & Login Dialog
        if (showProfileDialog) {
            GooglePlayGamesDialog(
                profile = playProfile,
                onDismiss = { viewModel.closeProfile() },
                onSignIn = { tag -> viewModel.signInGooglePlayGames(tag) },
                onSignOut = { viewModel.signOutGooglePlayGames() },
                onUpdateGamerTag = { newTag -> viewModel.updateGamerTag(newTag) }
            )
        }

        // Game Details Bottom Sheet
        selectedGame?.let { game ->
            GameDetailSheet(
                game = game,
                onDismiss = { viewModel.closeGameDetails() },
                onPlay = { viewModel.launchGame(game) },
                onToggleFavorite = { viewModel.toggleFavorite(game.id) },
                onChallengeFriends = {
                    viewModel.closeGameDetails()
                    viewModel.openChallengeDialog()
                }
            )
        }

        // Challenge Friend Dialog
        if (showChallengeDialog) {
            val gameTitle = selectedGame?.title ?: "Thronefall"
            ChallengeFriendDialog(
                friends = friends,
                preselectedFriend = challengeTargetFriend,
                gameTitle = gameTitle,
                onDismiss = { viewModel.closeChallengeDialog() },
                onSendChallenge = { friendId, targetScore ->
                    viewModel.sendChallenge(friendId, gameTitle, targetScore)
                }
            )
        }

        // Achievements Dialog
        if (showAchievementsDialog) {
            AchievementsDialog(
                achievements = achievements,
                onDismiss = { viewModel.closeAchievements() }
            )
        }

        // Add Friend Dialog
        if (showAddFriendDialog) {
            AddFriendDialog(
                onDismiss = { viewModel.closeAddFriendDialog() },
                onAddFriend = { gamerTag ->
                    Toast.makeText(
                        viewModel.getApplication(),
                        "Friend request sent to $gamerTag!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        // Fullscreen Game Mode Session with Game Overlay HUD
        if (gameModeConfig.isFullscreen && gameModeConfig.activeGame != null) {
            FullscreenGameSimulation(
                game = gameModeConfig.activeGame!!,
                config = gameModeConfig,
                onClose = { viewModel.exitFullscreenGameMode() },
                onToggleSustainedScaling = { viewModel.toggleSustainedPerformanceScaling(it) },
                onToggleLowPower = { viewModel.toggleTunedLowPowerMode(it) },
                onToggleDnd = { viewModel.toggleDoNotDisturb(it) },
                onToggleTouchBoost = { viewModel.toggleTouchBoost(it) },
                onChangeTargetFps = { viewModel.setTargetFps(it) }
            )
        }
    }
}
