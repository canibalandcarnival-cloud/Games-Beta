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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.BouncyBox
import com.example.ui.components.PillPlayButton
import com.example.ui.theme.ArcadeRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import com.example.ui.components.elasticOverscroll

@Composable
fun HomeScreen(
    viewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    val profile by viewModel.playProfile.collectAsState()
    val featuredGame = games.find { it.id == "consume_me" } ?: games.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .elasticOverscroll(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Bar: "Home", "SUBSCRIBED ARCADE" badge, user avatar
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
                    text = "Home",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("home_title")
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Subscribed / Play Games Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (profile.isSignedIn) "GOOGLE PLAY GAMES" else "SUBSCRIBED",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Profile Avatar button
                    Image(
                        painter = painterResource(id = R.drawable.avatar_gamer),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                            .clickable { viewModel.openProfile() }
                            .testTag("home_profile_avatar"),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Hero Featured Game Card (matches Image 1: Consume Me)
        item {
            if (featuredGame != null) {
                BouncyBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("hero_featured_card"),
                    onClick = { viewModel.openGameDetails(featuredGame) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(390.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFF1B1925))
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                                RoundedCornerShape(28.dp)
                            )
                    ) {
                        // Background cozy indie illustration
                        Image(
                            painter = painterResource(id = R.drawable.hero_consume_me),
                            contentDescription = featuredGame.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Rich frosted gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0x33000000),
                                            Color(0xD90D0E15),
                                            Color(0xF50D0E15)
                                        )
                                    )
                                )
                        )

                        // Content at bottom
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Incredible Indie",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = featuredGame.title,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = featuredGame.tagline.ifBlank { "Growing up is exhausting" },
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Play button
                            PillPlayButton(
                                text = "Play",
                                onClick = { viewModel.launchGame(featuredGame) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Carousel dots (Image 1 has 8 dots, first selected)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(8) { index ->
                                    val isCurrent = index == 0
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = if (isCurrent) 18.dp else 6.dp,
                                                height = 6.dp
                                            )
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (isCurrent) Color.White
                                                else Color.White.copy(alpha = 0.3f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: "Continue Playing >" (matching Image 1)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.selectTab(com.example.model.NavigationTab.LIBRARY) }
                ) {
                    Text(
                        text = "Continue Playing",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "View all",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal row of rounded squircle game icons
            val continueGames = games.take(6)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(continueGames) { game ->
                    BouncyBox(
                        modifier = Modifier.testTag("continue_game_${game.id}"),
                        onClick = { viewModel.openGameDetails(game) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(76.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(game.primaryColor))
                                    .border(
                                        BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                                        RoundedCornerShape(18.dp)
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
                                        text = game.title.take(2).uppercase(),
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = game.title,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }

        // Section: "Installed on Your Phone" (Android device games!)
        item {
            val phoneGames = games.filter { it.isInstalledOnDevice }
            if (phoneGames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Smartphone,
                            contentDescription = null,
                            tint = Color(0xFF30D158),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Installed on Your Phone",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshInstalledGames() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(phoneGames) { game ->
                        BouncyBox(
                            onClick = { viewModel.launchGame(game) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF161822))
                                    .border(
                                        BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(game.primaryColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = game.title.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = game.title,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Tap to Launch",
                                            color = Color(0xFF30D158),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: "New Games We Love >" (matching Image 1)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Games We Love",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val newGames = games.filter { it.id != "consume_me" }.take(4)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(newGames) { game ->
                    BouncyBox(
                        modifier = Modifier.width(240.dp),
                        onClick = { viewModel.openGameDetails(game) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(game.primaryColor),
                                            Color(game.secondaryColor)
                                        )
                                    )
                                )
                                .border(
                                    BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = game.category.uppercase(),
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = game.title,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    PillPlayButton(
                                        text = "Play",
                                        onClick = { viewModel.launchGame(game) }
                                    )
                                }

                                Text(
                                    text = game.tagline,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
