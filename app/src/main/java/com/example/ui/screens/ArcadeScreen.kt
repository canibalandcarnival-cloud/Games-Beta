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
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.SportsFootball
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.components.elasticOverscroll
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ArcadeCategoryItem(
    val name: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun ArcadeScreen(
    viewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    val sasquatchGame = games.find { it.id == "sneaky_sasquatch" } ?: games.firstOrNull()
    val arcadeForYouGame = games.find { it.id == "madden_nfl_27" } ?: games.getOrNull(1)

    var selectedCategory by remember { mutableStateOf("All Games") }

    val categories = listOf(
        ArcadeCategoryItem("All Games", Icons.Outlined.GridView, Color(0xFFFF375F)),
        ArcadeCategoryItem("Action", Icons.Outlined.Whatshot, Color(0xFFFFD60A)),
        ArcadeCategoryItem("Adventure", Icons.Outlined.Explore, Color(0xFF64D2FF)),
        ArcadeCategoryItem("Puzzle", Icons.Outlined.Extension, Color(0xFFBF5AF2)),
        ArcadeCategoryItem("Sports", Icons.Outlined.SportsFootball, Color(0xFF30D158))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .elasticOverscroll(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Bar matching image: "Arcade", "SUBSCRIBED ARCADE" badge, user avatar
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
                    text = "Arcade",
                    color = TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("arcade_title")
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "SUBSCRIBED",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.avatar_gamer),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                            .clickable { viewModel.openProfile() }
                            .testTag("arcade_profile_avatar"),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Hero Card: Sneaky Sasquatch (matches image!)
        item {
            if (sasquatchGame != null) {
                BouncyBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("arcade_hero_sasquatch_card"),
                    onClick = { viewModel.openGameDetails(sasquatchGame) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(410.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFF1B2F23))
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                                RoundedCornerShape(28.dp)
                            )
                    ) {
                        // Campground illustration background
                        Image(
                            painter = painterResource(id = R.drawable.hero_sasquatch),
                            contentDescription = sasquatchGame.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Rich frosted gradient overlay exactly like screenshot
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0x22000000),
                                            Color(0x990A3438),
                                            Color(0xFA092B2E)
                                        )
                                    )
                                )
                        )

                        // Sasquatch text overlay and View Game pill
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Only on Apple Arcade",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Sneaky Sasquatch",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Sneak and snack across a wild world",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // View Game cyan/teal translucent pill button
                            BouncyBox(
                                onClick = { viewModel.openGameDetails(sasquatchGame) },
                                modifier = Modifier.testTag("view_game_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            Color(0x66299FA8)
                                        )
                                        .border(
                                            BorderStroke(1.dp, Color(0x9952D4DE)),
                                            RoundedCornerShape(24.dp)
                                        )
                                        .padding(horizontal = 42.dp, vertical = 11.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "View Game",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 5 Carousel Dots
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(5) { index ->
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
                                                else Color.White.copy(alpha = 0.35f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Pill Chips row right below hero card (matches image!)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat.name
                    BouncyBox(
                        onClick = { selectedCategory = cat.name },
                        modifier = Modifier.testTag("arcade_category_${cat.name.lowercase().replace(" ", "_")}")
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFF2C2D38)
                                    else Color(0xFF161822)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) Color.White.copy(alpha = 0.25f)
                                        else Color.White.copy(alpha = 0.08f)
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = cat.iconTint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = cat.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: "Arcade Games for You >" (matches image!)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Arcade Games for You",
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

            // Large landscape featured game card (Madden NFL 27)
            if (arcadeForYouGame != null) {
                BouncyBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("arcade_games_for_you_card"),
                    onClick = { viewModel.openGameDetails(arcadeForYouGame) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF0F2B48),
                                        Color(0xFF071424)
                                    )
                                )
                            )
                            .border(
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                RoundedCornerShape(22.dp)
                            )
                            .padding(18.dp)
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
                                        text = "APPLE ARCADE EXCLUSIVE",
                                        color = Color(0xFF64D2FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = arcadeForYouGame.title,
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = arcadeForYouGame.tagline,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                }

                                PillPlayButton(
                                    text = "Play",
                                    onClick = { viewModel.launchGame(arcadeForYouGame) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "★ ${arcadeForYouGame.rating} • 1-4 Players",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Family Favorite",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: "Family Favorites & Indie Hits >"
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Family Favorites & Indie Hits",
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

            val familyGames = games.filter {
                it.id != "sneaky_sasquatch" && it.id != "madden_nfl_27"
            }.take(4)

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(familyGames) { game ->
                    BouncyBox(
                        modifier = Modifier.width(220.dp),
                        onClick = { viewModel.openGameDetails(game) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(20.dp))
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
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(14.dp)
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
                                            text = game.title,
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = game.tagline,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }

                                    PillPlayButton(
                                        text = "Play",
                                        onClick = { viewModel.launchGame(game) }
                                    )
                                }

                                Text(
                                    text = "${game.category} • ★ ${game.rating}",
                                    color = Color.White.copy(alpha = 0.7f),
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
