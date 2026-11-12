package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GamesRepository
import com.example.model.AchievementItem
import com.example.model.ChallengeItem
import com.example.model.FriendItem
import com.example.model.GameItem
import com.example.model.GooglePlayProfile
import com.example.model.NavigationTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class LibraryFilter(val displayName: String) {
    ALL("All Games"),
    INSTALLED("On My Phone"),
    ARCADE("Apple Arcade"),
    FAVORITES("Favorites")
}

enum class SortOption(val displayName: String) {
    RECENT("Recently Played"),
    NAME("Name A-Z"),
    RATING("Top Rated")
}

class GamesViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GamesRepository(application.applicationContext)

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _libraryFilter = MutableStateFlow(LibraryFilter.ALL)
    val libraryFilter: StateFlow<LibraryFilter> = _libraryFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECENT)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameItem?>(null)
    val selectedGame: StateFlow<GameItem?> = _selectedGame.asStateFlow()

    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _showChallengeDialog = MutableStateFlow(false)
    val showChallengeDialog: StateFlow<Boolean> = _showChallengeDialog.asStateFlow()

    private val _challengeTargetFriend = MutableStateFlow<FriendItem?>(null)
    val challengeTargetFriend: StateFlow<FriendItem?> = _challengeTargetFriend.asStateFlow()

    private val _gameModeConfig = MutableStateFlow(com.example.model.GameModeConfig())
    val gameModeConfig: StateFlow<com.example.model.GameModeConfig> = _gameModeConfig.asStateFlow()

    private val _showAchievementsDialog = MutableStateFlow(false)
    val showAchievementsDialog: StateFlow<Boolean> = _showAchievementsDialog.asStateFlow()

    private val _showAddFriendDialog = MutableStateFlow(false)
    val showAddFriendDialog: StateFlow<Boolean> = _showAddFriendDialog.asStateFlow()

    val games: StateFlow<List<GameItem>> = repository.games
    val friends: StateFlow<List<FriendItem>> = repository.friends
    val challenges: StateFlow<List<ChallengeItem>> = repository.challenges
    val achievements: StateFlow<List<AchievementItem>> = repository.achievements
    val playProfile: StateFlow<GooglePlayProfile> = repository.playProfile

    // Filtered games for Library and Search
    val filteredGames: StateFlow<List<GameItem>> = combine(
        games,
        _searchQuery,
        _libraryFilter,
        _sortOption
    ) { allGames, query, filter, sort ->
        var list = allGames

        // Apply Library tab filter
        list = when (filter) {
            LibraryFilter.ALL -> list
            LibraryFilter.INSTALLED -> list.filter { it.isInstalledOnDevice }
            LibraryFilter.ARCADE -> list.filter { it.category.contains("Arcade", ignoreCase = true) }
            LibraryFilter.FAVORITES -> list.filter { it.isFavorite }
        }

        // Apply Search query if not blank
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.tagline.contains(query, ignoreCase = true)
            }
        }

        // Apply Sorting
        when (sort) {
            SortOption.RECENT -> list
            SortOption.NAME -> list.sortedBy { it.title.lowercase() }
            SortOption.RATING -> list.sortedByDescending { it.rating }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLibraryFilter(filter: LibraryFilter) {
        _libraryFilter.value = filter
    }

    fun cycleSortOption() {
        val next = when (_sortOption.value) {
            SortOption.RECENT -> SortOption.NAME
            SortOption.NAME -> SortOption.RATING
            SortOption.RATING -> SortOption.RECENT
        }
        _sortOption.value = next
    }

    fun openGameDetails(game: GameItem) {
        _selectedGame.value = game
    }

    fun closeGameDetails() {
        _selectedGame.value = null
    }

    fun openProfile() {
        _showProfileDialog.value = true
    }

    fun closeProfile() {
        _showProfileDialog.value = false
    }

    fun openAchievements() {
        _showAchievementsDialog.value = true
    }

    fun closeAchievements() {
        _showAchievementsDialog.value = false
    }

    fun openChallengeDialog(friend: FriendItem? = null) {
        _challengeTargetFriend.value = friend
        _showChallengeDialog.value = true
    }

    fun closeChallengeDialog() {
        _showChallengeDialog.value = false
        _challengeTargetFriend.value = null
    }

    fun openAddFriendDialog() {
        _showAddFriendDialog.value = true
    }

    fun closeAddFriendDialog() {
        _showAddFriendDialog.value = false
    }

    fun launchGame(game: GameItem) {
        // Automatically activate Game Mode when launching games in fullscreen on device
        _gameModeConfig.value = _gameModeConfig.value.copy(
            isGameModeActive = true,
            activeGame = game,
            isFullscreen = true,
            currentFps = if (_gameModeConfig.value.tunedLowPowerMode) 45 else 60
        )
        repository.launchGame(game)
    }

    fun exitFullscreenGameMode() {
        _gameModeConfig.value = _gameModeConfig.value.copy(
            isGameModeActive = false,
            activeGame = null,
            isFullscreen = false
        )
    }

    fun toggleSustainedPerformanceScaling(enabled: Boolean) {
        val lowPower = if (enabled) false else _gameModeConfig.value.tunedLowPowerMode
        val temp = if (enabled) 36.8f else 34.2f
        val power = if (enabled) 4.6f else 3.2f
        val fps = if (lowPower) 45 else 60
        _gameModeConfig.value = _gameModeConfig.value.copy(
            sustainedPerformanceScaling = enabled,
            tunedLowPowerMode = lowPower,
            currentFps = fps,
            deviceTemperatureCelsius = temp,
            batteryDrainWatts = power,
            frameConsistencyPercent = if (enabled) 99.8f else 97.5f
        )
    }

    fun toggleTunedLowPowerMode(enabled: Boolean) {
        val sustained = if (enabled) false else _gameModeConfig.value.sustainedPerformanceScaling
        val temp = if (enabled) 31.5f else 35.8f
        val power = if (enabled) 2.1f else 4.2f
        val fps = if (enabled) 45 else 60
        _gameModeConfig.value = _gameModeConfig.value.copy(
            tunedLowPowerMode = enabled,
            sustainedPerformanceScaling = sustained,
            currentFps = fps,
            targetFps = if (enabled) 45 else 60,
            deviceTemperatureCelsius = temp,
            batteryDrainWatts = power,
            frameConsistencyPercent = if (enabled) 99.1f else 98.4f
        )
    }

    fun setTargetFps(fps: Int) {
        val temp = when (fps) {
            45 -> 31.8f
            60 -> 34.6f
            90 -> 37.9f
            120 -> 40.5f
            else -> 35.0f
        }
        val power = when (fps) {
            45 -> 2.2f
            60 -> 3.4f
            90 -> 5.1f
            120 -> 6.8f
            else -> 3.5f
        }
        _gameModeConfig.value = _gameModeConfig.value.copy(
            targetFps = fps,
            currentFps = fps,
            deviceTemperatureCelsius = temp,
            batteryDrainWatts = power
        )
    }

    fun toggleDoNotDisturb(enabled: Boolean) {
        _gameModeConfig.value = _gameModeConfig.value.copy(doNotDisturbActive = enabled)
    }

    fun toggleTouchBoost(enabled: Boolean) {
        _gameModeConfig.value = _gameModeConfig.value.copy(touchSensitivityBoost = enabled)
    }

    fun toggleFavorite(gameId: String) {
        repository.toggleFavorite(gameId)
    }

    fun sendChallenge(friendId: String, gameTitle: String, targetScore: Long) {
        repository.sendChallenge(friendId, gameTitle, targetScore)
        closeChallengeDialog()
    }

    fun refreshInstalledGames() {
        repository.refreshGamesList()
    }

    fun signInGooglePlayGames(gamerTag: String) {
        repository.signInGooglePlayGames(gamerTag)
    }

    fun signOutGooglePlayGames() {
        repository.signOutGooglePlayGames()
    }

    fun updateGamerTag(newTag: String) {
        repository.updateGamerTag(newTag)
    }
}
