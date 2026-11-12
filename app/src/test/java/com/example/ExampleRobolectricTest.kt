package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GamesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Games", appName)
    }

    @Test
    fun `games repository loads games and play profile`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = GamesRepository(context)

        val games = repository.games.value
        assertTrue("Games list should not be empty", games.isNotEmpty())
        assertTrue("Consume Me should be in catalog", games.any { it.id == "consume_me" })
        assertTrue("Pine Hearts should be in catalog", games.any { it.id == "pine_hearts" })

        val profile = repository.playProfile.value
        assertTrue("Default profile should have gamer tag", profile.gamerTag.isNotEmpty())
        assertTrue("Gamer level should be positive", profile.level > 0)
    }
}
