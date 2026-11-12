package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.GameItem
import com.example.ui.GamesViewModel
import com.example.ui.LibraryFilter
import com.example.ui.components.BouncyBox
import com.example.ui.components.LibraryGradientCard
import com.example.ui.components.PillPlayButton
import com.example.ui.theme.ArcadeRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import com.example.ui.components.elasticOverscroll

@Composable
fun LibraryScreen(
    viewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.filteredGames.collectAsState()
    val allGames by viewModel.games.collectAsState()
    val currentFilter by viewModel.libraryFilter.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val completedAchievements = achievements.count { it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .elasticOverscroll(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Bar: "Library" title on left, user avatar on right (matching Image 2)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Library",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("library_title")
                )

                Image(
                    painter = painterResource(id = R.drawable.avatar_gamer),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                        .clickable { viewModel.openProfile() }
                        .testTag("library_profile_avatar"),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 3 Gradient Cards from Image 2:
        // 1. Red/Ruby: Apple Arcade / 4 Games Added
        // 2. Amber/Orange: Updates and Events / Explore What's New
        // 3. Teal/Blue: Achievements / 9 Completed
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Apple Arcade
                LibraryGradientCard(
                    title = "Apple Arcade",
                    subtitle = "${allGames.count { it.category.contains("Arcade", ignoreCase = true) }} Games Added",
                    iconEmoji = "🕹️",
                    gradientColors = listOf(
                        Color(0xFF7A1828),
                        Color(0xFF45101A)
                    ),
                    onClick = {
                        viewModel.setLibraryFilter(
                            if (currentFilter == LibraryFilter.ARCADE) LibraryFilter.ALL
                            else LibraryFilter.ARCADE
                        )
                    }
                )

                // Card 2: Updates and Events
                LibraryGradientCard(
                    title = "Updates and Events",
                    subtitle = "Explore What's New",
                    iconEmoji = "🎁",
                    gradientColors = listOf(
                        Color(0xFF9E4B00),
                        Color(0xFF5E2B00)
                    ),
                    onClick = {
                        // Switch tab to Arcade/Events
                        viewModel.selectTab(com.example.model.NavigationTab.ARCADE)
                    }
                )

                // Card 3: Achievements
                LibraryGradientCard(
                    title = "Achievements",
                    subtitle = "$completedAchievements Completed",
                    iconEmoji = "🃏",
                    gradientColors = listOf(
                        Color(0xFF16507A),
                        Color(0xFF0F304B)
                    ),
                    onClick = { viewModel.openAchievements() }
                )
            }
        }

        // Section: "Your Games" with total count & sort button
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Games",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${allGames.size} Total • ${currentSort.displayName}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.cycleSortOption() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .testTag("sort_games_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sort,
                        contentDescription = "Sort Games",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LibraryFilter.entries) { filter ->
                    val isSelected = currentFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Color.White
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .clickable { viewModel.setLibraryFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("filter_chip_${filter.name.lowercase()}")
                    ) {
                        Text(
                            text = filter.displayName,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // List of Games matching Image 2
        items(games) { game ->
            GameListRow(
                game = game,
                onGameClick = { viewModel.openGameDetails(game) },
                onPlayClick = { viewModel.launchGame(game) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * List row item matching Image 2:
 * Squircle Icon | Title & Played status | Pill "Play" button
 */
@Composable
fun GameListRow(
    game: GameItem,
    onGameClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    BouncyBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("game_row_${game.id}"),
        onClick = onGameClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF10121A).copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squircle Game Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(game.primaryColor))
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (game.heroDrawableRes != null) {
                    Image(
                        painter = painterResource(id = game.heroDrawableRes),
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = game.title.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Game Title and Subtitle (e.g. Pine Hearts / Recently Downloaded)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (game.subtitle.isNotBlank()) "${game.category} • ${game.subtitle}" else game.playedTimeAgo,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Pill "Play" button
            PillPlayButton(
                text = "Play",
                onClick = onPlayClick
            )
        }
    }
}
