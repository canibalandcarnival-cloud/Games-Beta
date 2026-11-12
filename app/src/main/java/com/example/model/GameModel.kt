package com.example.model

enum class NavigationTab(val label: String) {
    HOME("Home"),
    ARCADE("Arcade"),
    FRIENDS("Friends"),
    LIBRARY("Library"),
    SEARCH("Search")
}

data class GameItem(
    val id: String,
    val title: String,
    val subtitle: String, // e.g. "Recently Downloaded", "Played 2d ago", "Apple Arcade"
    val category: String, // e.g. "Apple Arcade", "Indie", "Strategy", "Sports", "RPG", "Device App"
    val tagline: String = "",
    val description: String = "",
    val isInstalledOnDevice: Boolean = false,
    val packageName: String? = null,
    val playedTimeAgo: String = "Recently Downloaded",
    val rating: Float = 4.8f,
    val playerCount: String = "1 Player",
    val primaryColor: Long = 0xFF2A2D3D,
    val secondaryColor: Long = 0xFF14151F,
    val heroDrawableRes: Int? = null,
    val localIconRes: Int? = null,
    val achievementsTotal: Int = 20,
    val achievementsUnlocked: Int = 6,
    val highScores: Long = 24500,
    val isFavorite: Boolean = false,
    val downloadSize: String = "1.2 GB"
)

data class FriendItem(
    val id: String,
    val name: String,
    val gamerTag: String,
    val avatarEmoji: String,
    val accentColor: Long,
    val currentlyPlaying: String,
    val statusText: String, // e.g. "Playing now", "15m ago"
    val score: Long,
    val isOnline: Boolean,
    val level: Int = 18,
    val gamesCount: Int = 24
)

data class ChallengeItem(
    val id: String,
    val friendName: String,
    val gameTitle: String,
    val targetScore: Long,
    val userScore: Long = 0,
    val statusText: String = "Active Challenge",
    val endsIn: String = "2d 4h left",
    val isUserTurn: Boolean = true
)

data class AchievementItem(
    val id: String,
    val gameTitle: String,
    val title: String,
    val description: String,
    val xpPoints: Int,
    val isCompleted: Boolean,
    val iconEmoji: String = "🏆"
)

data class GooglePlayProfile(
    val isSignedIn: Boolean = true,
    val gamerTag: String = "ShadowRider#4920",
    val displayName: String = "Alex Chen",
    val email: String = "alex.chen.gaming@gmail.com",
    val level: Int = 32,
    val currentXp: Int = 14350,
    val nextLevelXp: Int = 20000,
    val totalAchievements: Int = 54,
    val totalGamesPlayed: Int = 19
)

data class GameModeConfig(
    val isGameModeActive: Boolean = false,
    val activeGame: GameItem? = null,
    val isFullscreen: Boolean = false,
    val sustainedPerformanceScaling: Boolean = true,
    val tunedLowPowerMode: Boolean = false,
    val targetFps: Int = 60,
    val currentFps: Int = 60,
    val gpuLoadPercent: Int = 52,
    val deviceTemperatureCelsius: Float = 34.6f,
    val batteryDrainWatts: Float = 3.4f,
    val doNotDisturbActive: Boolean = true,
    val touchSensitivityBoost: Boolean = true,
    val isOverlayExpanded: Boolean = false,
    val frameConsistencyPercent: Float = 99.4f
)
