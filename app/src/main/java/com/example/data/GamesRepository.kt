package com.example.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.example.R
import com.example.model.AchievementItem
import com.example.model.ChallengeItem
import com.example.model.FriendItem
import com.example.model.GameItem
import com.example.model.GooglePlayProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamesRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("games_app_prefs", Context.MODE_PRIVATE)

    private val _playProfile = MutableStateFlow(loadPlayProfile())
    val playProfile: StateFlow<GooglePlayProfile> = _playProfile.asStateFlow()

    private val _games = MutableStateFlow<List<GameItem>>(emptyList())
    val games: StateFlow<List<GameItem>> = _games.asStateFlow()

    private val _friends = MutableStateFlow<List<FriendItem>>(loadInitialFriends())
    val friends: StateFlow<List<FriendItem>> = _friends.asStateFlow()

    private val _challenges = MutableStateFlow<List<ChallengeItem>>(loadInitialChallenges())
    val challenges: StateFlow<List<ChallengeItem>> = _challenges.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementItem>>(loadInitialAchievements())
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    init {
        refreshGamesList()
    }

    private fun loadPlayProfile(): GooglePlayProfile {
        val isSignedIn = prefs.getBoolean("gplay_signed_in", true)
        val gamerTag = prefs.getString("gplay_gamer_tag", "PixelMaster#4092") ?: "PixelMaster#4092"
        val name = prefs.getString("gplay_display_name", "Alex Hunter") ?: "Alex Hunter"
        val level = prefs.getInt("gplay_level", 34)
        val xp = prefs.getInt("gplay_xp", 16400)
        return GooglePlayProfile(
            isSignedIn = isSignedIn,
            gamerTag = gamerTag,
            displayName = name,
            level = level,
            currentXp = xp,
            nextLevelXp = 20000,
            totalAchievements = 54,
            totalGamesPlayed = 18
        )
    }

    fun signInGooglePlayGames(gamerTag: String = "PixelMaster#4092") {
        prefs.edit().apply {
            putBoolean("gplay_signed_in", true)
            putString("gplay_gamer_tag", gamerTag)
            apply()
        }
        _playProfile.value = _playProfile.value.copy(
            isSignedIn = true,
            gamerTag = gamerTag
        )
        Toast.makeText(context, "Signed in to Google Play Games as $gamerTag", Toast.LENGTH_SHORT).show()
    }

    fun signOutGooglePlayGames() {
        prefs.edit().apply {
            putBoolean("gplay_signed_in", false)
            apply()
        }
        _playProfile.value = _playProfile.value.copy(isSignedIn = false)
        Toast.makeText(context, "Signed out of Google Play Games", Toast.LENGTH_SHORT).show()
    }

    fun updateGamerTag(newTag: String) {
        if (newTag.isBlank()) return
        prefs.edit().putString("gplay_gamer_tag", newTag.trim()).apply()
        _playProfile.value = _playProfile.value.copy(gamerTag = newTag.trim())
        Toast.makeText(context, "GamerTag updated to ${newTag.trim()}", Toast.LENGTH_SHORT).show()
    }

    fun refreshGamesList() {
        val installedDeviceGames = scanInstalledGames()
        val curatedCatalog = getCuratedCatalogGames()

        // Combine installed device apps and curated games
        val combined = ArrayList<GameItem>()
        // Curated first to preserve the featured experience from the screenshots
        combined.addAll(curatedCatalog)

        // Then add device installed apps that aren't already represented
        for (installed in installedDeviceGames) {
            if (combined.none { it.packageName == installed.packageName }) {
                combined.add(installed)
            }
        }
        _games.value = combined
    }

    private fun scanInstalledGames(): List<GameItem> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launchables = try {
            packageManager.queryIntentActivities(intent, 0)
        } catch (_: Exception) {
            emptyList()
        }

        val installedGames = mutableListOf<GameItem>()

        for (resolveInfo in launchables) {
            val appInfo = resolveInfo.activityInfo.applicationInfo
            val packageName = appInfo.packageName

            // Skip this app itself
            if (packageName == context.packageName) continue

            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            // Also detect common game package prefixes or popular apps
            val isKnownGameOrApp = isGame ||
                    packageName.contains("game", ignoreCase = true) ||
                    packageName.contains("play", ignoreCase = true) ||
                    packageName.contains("arcade", ignoreCase = true) ||
                    packageName.contains("studio", ignoreCase = true) ||
                    packageName.contains("unity", ignoreCase = true)

            val appLabel = try {
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                resolveInfo.activityInfo.name
            }

            // Include all identified games and notable apps
            if (isKnownGameOrApp || installedGames.size < 6) {
                installedGames.add(
                    GameItem(
                        id = "device_$packageName",
                        title = appLabel,
                        subtitle = "Installed on this phone",
                        category = if (isGame) "Device Game" else "Phone App",
                        tagline = "Installed application",
                        description = "Launch and play directly from your device.",
                        isInstalledOnDevice = true,
                        packageName = packageName,
                        playedTimeAgo = "On device",
                        rating = 4.7f,
                        playerCount = "Single Player",
                        primaryColor = 0xFF1C2237,
                        secondaryColor = 0xFF0E121E,
                        achievementsTotal = 15,
                        achievementsUnlocked = 4
                    )
                )
            }
        }

        return installedGames
    }

    fun launchGame(game: GameItem) {
        val packageName = game.packageName
        if (packageName != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Toast.makeText(context, "Launching ${game.title}...", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // If not installed on device, provide smooth simulation or open Play Store
        try {
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=${Uri.encode(game.title)}"))
            playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(playStoreIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "Starting ${game.title} session...", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleFavorite(gameId: String) {
        _games.value = _games.value.map {
            if (it.id == gameId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun sendChallenge(friendId: String, gameTitle: String, targetScore: Long) {
        val friend = _friends.value.find { it.id == friendId } ?: return
        val newChallenge = ChallengeItem(
            id = "chal_${System.currentTimeMillis()}",
            friendName = friend.name,
            gameTitle = gameTitle,
            targetScore = targetScore,
            userScore = 0,
            statusText = "Challenge Sent!",
            endsIn = "3 days left",
            isUserTurn = false
        )
        _challenges.value = listOf(newChallenge) + _challenges.value
        Toast.makeText(context, "Challenge sent to ${friend.name} in $gameTitle!", Toast.LENGTH_LONG).show()
    }

    private fun getCuratedCatalogGames(): List<GameItem> {
        return listOf(
            GameItem(
                id = "sneaky_sasquatch",
                title = "Sneaky Sasquatch",
                subtitle = "Only on Apple Arcade",
                category = "Apple Arcade",
                tagline = "Sneak and snack across a wild world",
                description = "Live the life of a Sasquatch and do regular, everyday Sasquatch things like sneak around campsites, disguise yourself in human clothes, eat food from unguarded coolers, and play a round of golf.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Featured on Arcade",
                rating = 4.9f,
                playerCount = "1 Player",
                primaryColor = 0xFF2A7B62,
                secondaryColor = 0xFF144D3D,
                heroDrawableRes = R.drawable.hero_sasquatch,
                achievementsTotal = 32,
                achievementsUnlocked = 18,
                highScores = 89000,
                isFavorite = true,
                downloadSize = "780 MB"
            ),
            GameItem(
                id = "consume_me",
                title = "Consume Me",
                subtitle = "Incredible Indie",
                category = "Indie",
                tagline = "Growing up is exhausting",
                description = "A deeply heartfelt, funny, and tender narrative puzzle game about navigating adolescence, school pressures, and family meals.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Recently Downloaded",
                rating = 4.9f,
                playerCount = "1 Player",
                primaryColor = 0xFFE07A5F,
                secondaryColor = 0xFF3D405B,
                heroDrawableRes = R.drawable.hero_consume_me,
                achievementsTotal = 24,
                achievementsUnlocked = 9,
                highScores = 34200,
                isFavorite = true,
                downloadSize = "850 MB"
            ),
            GameItem(
                id = "pine_hearts",
                title = "Pine Hearts",
                subtitle = "Recently Downloaded",
                category = "Apple Arcade",
                tagline = "A cozy little narrative adventure",
                description = "Explore the great outdoors in the tranquil Caravan Park. Discover warm memories, make quirky friends, and solve gentle puzzles.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Recently Downloaded",
                rating = 4.8f,
                playerCount = "1 Player",
                primaryColor = 0xFF588157,
                secondaryColor = 0xFF344E41,
                achievementsTotal = 18,
                achievementsUnlocked = 6,
                highScores = 12800,
                isFavorite = true,
                downloadSize = "1.1 GB"
            ),
            GameItem(
                id = "madden_nfl_27",
                title = "Madden NFL 27 Arcade",
                subtitle = "Played 2d ago",
                category = "Apple Arcade",
                tagline = "Fast-paced arcade football action",
                description = "Electrifying 5v5 arcade football with high-flying tackles, trick plays, and real NFL legends.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Played 2d ago",
                rating = 4.6f,
                playerCount = "1-4 Players",
                primaryColor = 0xFF1D3557,
                secondaryColor = 0xFF457B9D,
                achievementsTotal = 30,
                achievementsUnlocked = 14,
                highScores = 98400,
                isFavorite = true,
                downloadSize = "2.4 GB"
            ),
            GameItem(
                id = "control",
                title = "Control",
                subtitle = "Played 1w ago",
                category = "Action",
                tagline = "Supernatural third-person shooter",
                description = "Enter the Oldest House. Master telekinetic abilities and unravel the mystery of the Federal Bureau of Control.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Played 1w ago",
                rating = 4.9f,
                playerCount = "1 Player",
                primaryColor = 0xFFD90429,
                secondaryColor = 0xFF2B2D42,
                achievementsTotal = 42,
                achievementsUnlocked = 28,
                highScores = 154000,
                isFavorite = false,
                downloadSize = "4.2 GB"
            ),
            GameItem(
                id = "sea_of_stars",
                title = "Sea of Stars",
                subtitle = "Played 2w ago",
                category = "Apple Arcade",
                tagline = "Retro turn-based RPG masterpiece",
                description = "Combines modern turn-based combat, rich traversal, and daylight eclipse eclipse powers into a stunning pixel art adventure.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Played 2w ago",
                rating = 5.0f,
                playerCount = "1 Player",
                primaryColor = 0xFF4361EE,
                secondaryColor = 0xFF3A0CA3,
                achievementsTotal = 36,
                achievementsUnlocked = 22,
                highScores = 82400,
                isFavorite = true,
                downloadSize = "1.8 GB"
            ),
            GameItem(
                id = "thronefall",
                title = "Thronefall",
                subtitle = "Start Challenge",
                category = "Strategy",
                tagline = "A little kingdom defense game",
                description = "Build your kingdom by day, defend it to the last breath by night. Strategic, addicting, and beautifully minimalist.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Played 3d ago",
                rating = 4.9f,
                playerCount = "1-2 Players",
                primaryColor = 0xFF7209B7,
                secondaryColor = 0xFF3F37C9,
                heroDrawableRes = R.drawable.hero_multiplayer,
                achievementsTotal = 25,
                achievementsUnlocked = 15,
                highScores = 45200,
                isFavorite = true,
                downloadSize = "920 MB"
            ),
            GameItem(
                id = "monster_deep",
                title = "Monster of the deep",
                subtitle = "2-16 Players",
                category = "Multiplayer",
                tagline = "Co-op oceanic mythical battle",
                description = "Team up with up to 16 players to hunt colossal leviathans and conquer the deep ocean trenches.",
                isInstalledOnDevice = true,
                packageName = null,
                playedTimeAgo = "Popular in Friends",
                rating = 4.7f,
                playerCount = "2-16 Players",
                primaryColor = 0xFF0077B6,
                secondaryColor = 0xFF03045E,
                heroDrawableRes = R.drawable.hero_multiplayer,
                achievementsTotal = 20,
                achievementsUnlocked = 11,
                highScores = 67800,
                isFavorite = false,
                downloadSize = "1.6 GB"
            ),
            GameItem(
                id = "brawl_stars",
                title = "Brawl Stars",
                subtitle = "Trending now",
                category = "Action",
                tagline = "Fast-paced 3v3 multiplayer and battle royale",
                description = "Unlock and upgrade dozens of Brawlers with powerful Super abilities, Star Powers and Gadgets!",
                isInstalledOnDevice = false,
                packageName = "com.supercell.brawlstars",
                playedTimeAgo = "Trending with friends",
                rating = 4.5f,
                playerCount = "3v3 Multiplayer",
                primaryColor = 0xFFFFB703,
                secondaryColor = 0xFFFB8500,
                achievementsTotal = 40,
                achievementsUnlocked = 18,
                highScores = 18450,
                isFavorite = false,
                downloadSize = "450 MB"
            ),
            GameItem(
                id = "subway_surfers",
                title = "Subway Surfers",
                subtitle = "Classic Endless Runner",
                category = "Arcade",
                tagline = "Dodge oncoming trains and surf the tracks",
                description = "Dash as fast as you can through subway stations with hoverboards and jetpacks.",
                isInstalledOnDevice = false,
                packageName = "com.kiloo.subwaysurf",
                playedTimeAgo = "Friend High Score: 1.2M",
                rating = 4.6f,
                playerCount = "1 Player",
                primaryColor = 0xFF06D6A0,
                secondaryColor = 0xFF118AB2,
                achievementsTotal = 30,
                achievementsUnlocked = 24,
                highScores = 1250000,
                isFavorite = false,
                downloadSize = "180 MB"
            )
        )
    }

    private fun loadInitialFriends(): List<FriendItem> {
        return listOf(
            FriendItem(
                id = "friend_1",
                name = "Oliver Cat",
                gamerTag = "@oliver_cat",
                avatarEmoji = "🐱",
                accentColor = 0xFFE6AF2E, // Warm gold matching 3rd image
                currentlyPlaying = "Thronefall",
                statusText = "Playing now",
                score = 45200,
                isOnline = true,
                level = 28,
                gamesCount = 19
            ),
            FriendItem(
                id = "friend_2",
                name = "Spike D.",
                gamerTag = "@spikegreen",
                avatarEmoji = "🦖",
                accentColor = 0xFF2A9D8F, // Teal green matching 3rd image
                currentlyPlaying = "Monster of the deep",
                statusText = "Played 10m ago",
                score = 38900,
                isOnline = true,
                level = 24,
                gamesCount = 14
            ),
            FriendItem(
                id = "friend_3",
                name = "Maya Blue",
                gamerTag = "@mayablue",
                avatarEmoji = "🦊",
                accentColor = 0xFF3A86FF, // Ocean blue
                currentlyPlaying = "Sea of Stars",
                statusText = "Level 42",
                score = 52100,
                isOnline = false,
                level = 36,
                gamesCount = 31
            ),
            FriendItem(
                id = "friend_4",
                name = "Leo Star",
                gamerTag = "@leostar",
                avatarEmoji = "🦁",
                accentColor = 0xFF8338EC, // Violet purple
                currentlyPlaying = "Control",
                statusText = "Completed Chapter 7",
                score = 61200,
                isOnline = true,
                level = 31,
                gamesCount = 22
            )
        )
    }

    private fun loadInitialChallenges(): List<ChallengeItem> {
        return listOf(
            ChallengeItem(
                id = "chal_1",
                friendName = "Oliver Cat",
                gameTitle = "Thronefall",
                targetScore = 48000,
                userScore = 45200,
                statusText = "Beat Oliver's High Score!",
                endsIn = "1d 12h left",
                isUserTurn = true
            ),
            ChallengeItem(
                id = "chal_2",
                friendName = "Spike D.",
                gameTitle = "Monster of the deep",
                targetScore = 35000,
                userScore = 38900,
                statusText = "You're in the lead! 🏆",
                endsIn = "2 days left",
                isUserTurn = false
            )
        )
    }

    private fun loadInitialAchievements(): List<AchievementItem> {
        return listOf(
            AchievementItem(
                id = "ach_1",
                gameTitle = "Consume Me",
                title = "First Dinner Cleared",
                description = "Mastered the balance between conversation and dinner.",
                xpPoints = 500,
                isCompleted = true,
                iconEmoji = "🍽️"
            ),
            AchievementItem(
                id = "ach_2",
                gameTitle = "Pine Hearts",
                title = "Nature's Friend",
                description = "Pet all 10 woodland creatures in Caravan Park.",
                xpPoints = 750,
                isCompleted = true,
                iconEmoji = "🌲"
            ),
            AchievementItem(
                id = "ach_3",
                gameTitle = "Thronefall",
                title = "Iron Monarch",
                description = "Defend Castle Hill without taking base damage.",
                xpPoints = 1000,
                isCompleted = true,
                iconEmoji = "👑"
            ),
            AchievementItem(
                id = "ach_4",
                gameTitle = "Sea of Stars",
                title = "Eclipse Weaver",
                description = "Synchronize solar and lunar magic in a single combo turn.",
                xpPoints = 1200,
                isCompleted = true,
                iconEmoji = "✨"
            ),
            AchievementItem(
                id = "ach_5",
                gameTitle = "Control",
                title = "Director's Clearance",
                description = "Unlock Level 6 Security clearance in the Oldest House.",
                xpPoints = 850,
                isCompleted = false,
                iconEmoji = "🗝️"
            ),
            AchievementItem(
                id = "ach_6",
                gameTitle = "Madden NFL 27 Arcade",
                title = "Hail Mary Magic",
                description = "Score an 80+ yard touchdown in the final 5 seconds.",
                xpPoints = 900,
                isCompleted = false,
                iconEmoji = "🏈"
            )
        )
    }
}
