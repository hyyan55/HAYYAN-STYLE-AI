package com.example

import com.example.data.AppDatabase
import com.example.data.ClothingCategory
import com.example.data.ClothingStyle
import com.example.data.Occasion
import com.example.data.WardrobeItemEntity
import com.example.engine.OutfitStylingEngine
import com.example.engine.StylingResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OutfitStylingEngineTest {

    @Test
    fun testStarterWardrobeGeneratesOutfits() {
        val starter = AppDatabase.getStarterCapsuleWardrobe()
        val result = OutfitStylingEngine.generateOutfits(
            wardrobe = starter,
            occasion = Occasion.SMART_CASUAL.name,
            targetStyle = ClothingStyle.SMART_CASUAL.name
        )

        assertTrue("Expected Success result", result is StylingResult.Success)
        val success = result as StylingResult.Success
        assertEquals("Should return top 3 outfits", 3, success.outfits.size)

        // Strict Wardrobe Integrity: all returned item IDs must exist in starter wardrobe
        val validIds = starter.map { it.id }.toSet()
        for (outfit in success.outfits) {
            assertTrue(outfit.score in 60..100)
            assertTrue(outfit.top.category == ClothingCategory.TOPS.name)
            assertTrue(outfit.bottom.category == ClothingCategory.BOTTOMS.name)
            assertTrue(outfit.footwear.category == ClothingCategory.FOOTWEAR.name)
        }
    }

    @Test
    fun testMissingGarmentsDetection() {
        // Wardrobe with only tops (missing bottoms and shoes)
        val topsOnly = listOf(
            WardrobeItemEntity(
                id = 1,
                name = "White Shirt",
                category = ClothingCategory.TOPS.name,
                garmentType = "Shirt",
                primaryColor = "#FFFFFF"
            )
        )

        val result = OutfitStylingEngine.generateOutfits(
            wardrobe = topsOnly,
            occasion = Occasion.EVERYDAY_CASUAL.name,
            targetStyle = ClothingStyle.CASUAL.name
        )

        assertTrue("Expected MissingGarments result", result is StylingResult.MissingGarments)
        val missing = result as StylingResult.MissingGarments
        assertTrue(missing.missingCategoriesAr.isNotEmpty())
    }
}
