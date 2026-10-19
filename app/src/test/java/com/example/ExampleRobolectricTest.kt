package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Relationship
import com.example.model.SurpriseData
import com.example.model.SurpriseTheme
import com.example.util.SurpriseEncoder
import com.example.util.SurpriseStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wishora", appName)
  }

  @Test
  fun testSurpriseEncodeAndDecode() {
    val surprise = SurpriseData(
      recipientName = "Priya",
      relationship = Relationship.ROMANTIC_PARTNER,
      message = "Happy Birthday my love!",
      senderName = "Alex",
      theme = SurpriseTheme.LOVE
    )

    val encoded = SurpriseEncoder.encodeSurprise(surprise)
    assertTrue(encoded.isNotEmpty())

    val decoded = SurpriseEncoder.decodeSurprise(encoded)
    assertNotNull(decoded)
    assertEquals("Priya", decoded?.recipientName)
    assertEquals(Relationship.ROMANTIC_PARTNER, decoded?.relationship)
    assertEquals("Happy Birthday my love!", decoded?.message)
    assertEquals("Alex", decoded?.senderName)
    assertEquals(SurpriseTheme.LOVE, decoded?.theme)
  }

  @Test
  fun testSurpriseStorage() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val storage = SurpriseStorage(context)

    val surprise = SurpriseData(
      recipientName = "Tariq",
      relationship = Relationship.BEST_FRIEND,
      message = "শুভ জন্মদিন দোস্ত!",
      senderName = "Naim",
      theme = SurpriseTheme.FUNNY
    )

    storage.saveSurprise(surprise)
    val list = storage.getAllSurprises()
    assertTrue(list.any { it.recipientName == "Tariq" })

    storage.deleteSurprise(surprise.id)
    val listAfter = storage.getAllSurprises()
    assertTrue(listAfter.none { it.id == surprise.id })
  }
}
