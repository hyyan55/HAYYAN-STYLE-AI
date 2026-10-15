package com.example.engine

import com.example.data.ClothingCategory
import com.example.data.WardrobeItemEntity
import kotlin.math.abs

data class OutfitCandidate(
    val top: WardrobeItemEntity,
    val bottom: WardrobeItemEntity,
    val footwear: WardrobeItemEntity,
    val outerwear: WardrobeItemEntity? = null,
    val accessory: WardrobeItemEntity? = null,
    val score: Int,
    val occasion: String,
    val style: String,
    val stylistExplanationAr: String,
    val stylistExplanationEn: String,
    val colorHarmonyExplanationAr: String,
    val colorHarmonyExplanationEn: String
)

sealed interface StylingResult {
    data class Success(val outfits: List<OutfitCandidate>) : StylingResult
    data class MissingGarments(
        val missingCategoriesAr: List<String>,
        val missingCategoriesEn: List<String>,
        val adviceAr: String,
        val adviceEn: String
    ) : StylingResult
    data class NoValidCombinations(val reasonAr: String, val reasonEn: String) : StylingResult
}

object OutfitStylingEngine {

    fun generateOutfits(
        wardrobe: List<WardrobeItemEntity>,
        occasion: String,
        targetStyle: String,
        startingItemId: Long? = null,
        excludedItemIds: Set<Long> = emptySet(),
        preferredColor: String? = null
    ): StylingResult {
        // Filter out excluded items and unavailable items
        val activeItems = wardrobe.filter { it.isAvailable && it.id !in excludedItemIds }

        val tops = activeItems.filter { it.category == ClothingCategory.TOPS.name }
        val bottoms = activeItems.filter { it.category == ClothingCategory.BOTTOMS.name }
        val footwear = activeItems.filter { it.category == ClothingCategory.FOOTWEAR.name }
        val outerwears = activeItems.filter { it.category == ClothingCategory.OUTERWEAR.name }
        val accessories = activeItems.filter { it.category == ClothingCategory.ACCESSORIES.name }

        // Check wardrobe completeness
        val missingAr = mutableListOf<String>()
        val missingEn = mutableListOf<String>()

        if (tops.isEmpty()) {
            missingAr.add("القطع العلوية (قمصان / تيشيرتات)")
            missingEn.add("Tops (Shirts / T-shirts)")
        }
        if (bottoms.isEmpty()) {
            missingAr.add("القطع السفلية (بناطيل / جينز)")
            missingEn.add("Bottoms (Pants / Jeans)")
        }
        if (footwear.isEmpty()) {
            missingAr.add("الأحذية")
            missingEn.add("Footwear")
        }

        if (missingAr.isNotEmpty()) {
            return StylingResult.MissingGarments(
                missingCategoriesAr = missingAr,
                missingCategoriesEn = missingEn,
                adviceAr = "لبناء إطلالة متكاملة وأنيقة، يرجى إضافة القطع الناقصة لخزانتك: ${missingAr.joinToString("، ")}.",
                adviceEn = "To coordinate complete stylish outfits, please add the missing items to your wardrobe: ${missingEn.joinToString(", ")}."
            )
        }

        val candidates = mutableListOf<OutfitCandidate>()

        // Generate Cartesian candidates
        for (top in tops) {
            for (bottom in bottoms) {
                for (shoe in footwear) {
                    // If a starting item was selected, ensure it's in this outfit
                    if (startingItemId != null) {
                        if (top.id != startingItemId && bottom.id != startingItemId && shoe.id != startingItemId) {
                            continue
                        }
                    }

                    // Optional outer item pairing if matching occasion or weather
                    val matchingOuter = outerwears.firstOrNull { outer ->
                        isOuterwearAppropriate(outer, top, occasion, targetStyle)
                    }

                    // Optional accessory pairing
                    val matchingAccessory = accessories.firstOrNull()

                    // Calculate score
                    val scoreBreakdown = evaluateOutfit(
                        top = top,
                        bottom = bottom,
                        footwear = shoe,
                        outerwear = matchingOuter,
                        occasion = occasion,
                        targetStyle = targetStyle,
                        preferredColor = preferredColor
                    )

                    candidates.add(
                        OutfitCandidate(
                            top = top,
                            bottom = bottom,
                            footwear = shoe,
                            outerwear = matchingOuter,
                            accessory = matchingAccessory,
                            score = scoreBreakdown.totalScore,
                            occasion = occasion,
                            style = targetStyle,
                            stylistExplanationAr = scoreBreakdown.stylistExplanationAr,
                            stylistExplanationEn = scoreBreakdown.stylistExplanationEn,
                            colorHarmonyExplanationAr = scoreBreakdown.colorHarmonyAr,
                            colorHarmonyExplanationEn = scoreBreakdown.colorHarmonyEn
                        )
                    )
                }
            }
        }

        if (candidates.isEmpty()) {
            return StylingResult.NoValidCombinations(
                reasonAr = "لم نتمكن من إيجاد تنسيق مناسب مع القطعة المختارة أو الفلاتر المحددة. جرب إزالة الفلاتر الاستثنائية.",
                reasonEn = "Could not find a valid combination with the selected starting item or excluded filters."
            )
        }

        // Sort descending by score
        candidates.sortByDescending { it.score }

        // Shortlist Top 3 DIVERSE candidates (avoid having 3 identical combinations with only 1 accessory diff)
        val selectedDiverseOutfits = mutableListOf<OutfitCandidate>()
        for (candidate in candidates) {
            if (selectedDiverseOutfits.size >= 3) break

            val isDuplicate = selectedDiverseOutfits.any { existing ->
                (existing.top.id == candidate.top.id && existing.bottom.id == candidate.bottom.id) ||
                        (existing.top.id == candidate.top.id && existing.footwear.id == candidate.footwear.id) ||
                        (existing.bottom.id == candidate.bottom.id && existing.footwear.id == candidate.footwear.id)
            }

            if (!isDuplicate || selectedDiverseOutfits.isEmpty()) {
                selectedDiverseOutfits.add(candidate)
            }
        }

        // If strict diversity left fewer than 3, fill with top remaining candidates
        if (selectedDiverseOutfits.size < 3) {
            for (candidate in candidates) {
                if (selectedDiverseOutfits.size >= 3) break
                if (candidate !in selectedDiverseOutfits) {
                    selectedDiverseOutfits.add(candidate)
                }
            }
        }

        return StylingResult.Success(selectedDiverseOutfits)
    }

    private data class ScoreBreakdown(
        val totalScore: Int,
        val colorHarmonyAr: String,
        val colorHarmonyEn: String,
        val stylistExplanationAr: String,
        val stylistExplanationEn: String
    )

    private fun evaluateOutfit(
        top: WardrobeItemEntity,
        bottom: WardrobeItemEntity,
        footwear: WardrobeItemEntity,
        outerwear: WardrobeItemEntity?,
        occasion: String,
        targetStyle: String,
        preferredColor: String?
    ): ScoreBreakdown {
        var baseScore = 70

        // 1. Color Harmony Analysis
        val topHex = top.primaryColor.trim()
        val bottomHex = bottom.primaryColor.trim()
        val shoeHex = footwear.primaryColor.trim()

        val isTopLight = isLightColor(topHex)
        val isBottomLight = isLightColor(bottomHex)
        val isTopNeutral = isNeutralColor(topHex)
        val isBottomNeutral = isNeutralColor(bottomHex)
        val isShoeNeutral = isNeutralColor(shoeHex)

        val colorNotesAr = mutableListOf<String>()
        val colorNotesEn = mutableListOf<String>()

        // Contrast Rule: Light Top + Dark Bottom or vice versa gives high visual balance
        if (isTopLight != isBottomLight) {
            baseScore += 12
            colorNotesAr.add("تباين كلاسيكي متوازن بين القطعة العلوية والسفلية يبرز القوام بأناقة")
            colorNotesEn.add("Balanced light-dark contrast between top and bottom elongates the silhouette")
        } else if (isTopNeutral && isBottomNeutral) {
            baseScore += 10
            colorNotesAr.add("تناغم أحادي راقٍ بألوان محايدة هادئة تعكس فخامة بدون تكلف")
            colorNotesEn.add("Sophisticated monochromatic neutral harmony for an understated luxury aesthetic")
        } else {
            baseScore += 4
            colorNotesAr.add("ألوان متقاربة في درجاتها تعطي انطباعاً مريحاً للعين")
            colorNotesEn.add("Soft complementary color tones for a comfortable visual balance")
        }

        // Footwear anchoring
        if (isShoeNeutral || shoeHex.equals(bottomHex, ignoreCase = true)) {
            baseScore += 6
            colorNotesAr.add("حذاء بلون مدروس يرسخ الإطلالة بشكل متناسق")
            colorNotesEn.add("Footwear provides a clean grounded foundation for the ensemble")
        }

        // 2. Occasion & Garment Role Compatibility
        val stylistNotesAr = mutableListOf<String>()
        val stylistNotesEn = mutableListOf<String>()

        when (occasion) {
            "FORMAL_EVENT", "مناسبة رسمية ومؤتمرات" -> {
                if (top.garmentType.contains("Shirt", true) || top.style == "FORMAL") {
                    baseScore += 10
                    stylistNotesAr.add("القميص الرسمي يفي بمعايير الوقار والاحترافية")
                    stylistNotesEn.add("Crisp formal shirt aligns with professional etiquette")
                }
                if (bottom.garmentType.contains("Trousers", true) || bottom.style == "FORMAL") {
                    baseScore += 8
                    stylistNotesAr.add("بنطال قماشي رسمي يمنح مظهراً مهندماً")
                    stylistNotesEn.add("Tailored trousers reinforce structured elegance")
                }
                if (footwear.garmentType.contains("Loafers", true) || footwear.garmentType.contains("Boots", true) || footwear.garmentType.contains("Formal", true)) {
                    baseScore += 6
                } else {
                    baseScore -= 12 // Sneakers with formal suit reduced score
                    stylistNotesAr.add("ملاحظة: الأحذية الكلاسيكية أكثر ملاءمة للمناسبات الرسمية من الأحذية الرياضية")
                    stylistNotesEn.add("Note: Classic leather shoes are preferred over sneakers for formal events")
                }
                if (outerwear?.garmentType?.contains("Blazer", true) == true) {
                    baseScore += 8
                    stylistNotesAr.add("البليزر يكمل فخامة المناسبة الرسمية")
                    stylistNotesEn.add("Tailored blazer completes the refined formal look")
                }
            }
            "SMART_CASUAL", "سمارت كاجوال" -> {
                baseScore += 8
                stylistNotesAr.add("مزيج مثالي بين الراحة العصرية والشياكة الراقية للعمل واللقاءات")
                stylistNotesEn.add("Effortless balance of relaxed modern ease and polished refinement")
            }
            "UNIVERSITY", "الجامعة والعمل اليومي" -> {
                if (footwear.garmentType.contains("Sneakers", true) || bottom.garmentType.contains("Chinos", true) || bottom.garmentType.contains("Jeans", true)) {
                    baseScore += 8
                    stylistNotesAr.add("إطلالة عملية ومريحة وعصرية تلائم نشاط اليوم الجامعي")
                    stylistNotesEn.add("Practical, comfortable, and trendy for active university days")
                }
            }
            else -> {
                baseScore += 6
                stylistNotesAr.add("تنسيق متجانس ومرن يمنحك ثقة وجاذبية طبيعية")
                stylistNotesEn.add("Versatile, harmonious coordination projecting effortless confidence")
            }
        }

        // Preferred color bonus
        if (!preferredColor.isNullOrBlank()) {
            if (topHex.contains(preferredColor, true) || bottomHex.contains(preferredColor, true)) {
                baseScore += 5
            }
        }

        // Style matching bonus
        if (top.style.equals(targetStyle, ignoreCase = true) || bottom.style.equals(targetStyle, ignoreCase = true)) {
            baseScore += 5
        }

        val finalScore = baseScore.coerceIn(68, 98)

        val harmonyAr = colorNotesAr.joinToString("، ")
        val harmonyEn = colorNotesEn.joinToString(". ")
        val stylingAr = stylistNotesAr.joinToString("، ")
        val stylingEn = stylistNotesEn.joinToString(". ")

        return ScoreBreakdown(
            totalScore = finalScore,
            colorHarmonyAr = harmonyAr,
            colorHarmonyEn = harmonyEn,
            stylistExplanationAr = stylingAr,
            stylistExplanationEn = stylingEn
        )
    }

    private fun isOuterwearAppropriate(
        outer: WardrobeItemEntity,
        top: WardrobeItemEntity,
        occasion: String,
        targetStyle: String
    ): Boolean {
        if (occasion.contains("FORMAL", true) && outer.garmentType.contains("Blazer", true)) return true
        if (targetStyle.contains("SMART", true) && outer.garmentType.contains("Blazer", true)) return true
        if (outer.primaryColor.equals(top.primaryColor, true)) return false // avoid exact clone
        return true
    }

    private fun isLightColor(colorStr: String): Boolean {
        val normalized = colorStr.lowercase()
        if (normalized.contains("white") || normalized.contains("beige") || normalized.contains("cream") ||
            normalized.contains("light") || normalized.contains("ivory") || normalized.contains("khaki")
        ) return true

        if (normalized.startsWith("#") && normalized.length == 7) {
            try {
                val r = normalized.substring(1, 3).toInt(16)
                val g = normalized.substring(3, 5).toInt(16)
                val b = normalized.substring(5, 7).toInt(16)
                val brightness = (r * 299 + g * 587 + b * 114) / 1000
                return brightness > 155
            } catch (_: Exception) {
            }
        }
        return false
    }

    private fun isNeutralColor(colorStr: String): Boolean {
        val normalized = colorStr.lowercase()
        return normalized.contains("white") || normalized.contains("black") || normalized.contains("gray") ||
                normalized.contains("grey") || normalized.contains("navy") || normalized.contains("beige") ||
                normalized.contains("khaki") || normalized.contains("brown") || normalized.contains("charcoal") ||
                normalized.contains("camel") || normalized.contains("tan") || normalized.contains("olive") ||
                normalized == "#ffffff" || normalized == "#000000" || normalized == "#f5f5f7" || normalized == "#1d2a44"
    }
}
