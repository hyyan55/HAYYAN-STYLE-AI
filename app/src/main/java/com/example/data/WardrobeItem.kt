package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ClothingCategory(val arLabel: String, val enLabel: String) {
    TOPS("القطع العلوية", "Tops"),
    BOTTOMS("القطع السفلية", "Bottoms"),
    FOOTWEAR("الأحذية", "Footwear"),
    OUTERWEAR("السترات والمعاطف", "Outerwear"),
    ACCESSORIES("الإكسسوارات", "Accessories")
}

enum class ClothingStyle(val arLabel: String, val enLabel: String) {
    MINIMALIST("مينيماليست (بسيط)", "Minimalist"),
    CASUAL("كاجوال", "Casual"),
    SMART_CASUAL("سمارت كاجوال", "Smart Casual"),
    FORMAL("رسمي", "Formal"),
    SPORTY("رياضي", "Sporty"),
    CLASSIC("كلاسيكي", "Classic"),
    STREETWEAR("ستريت وير", "Streetwear"),
    TRADITIONAL("تقليدي", "Traditional")
}

enum class Occasion(val arLabel: String, val enLabel: String) {
    EVERYDAY_CASUAL("يومي كاجوال", "Everyday Casual"),
    SMART_CASUAL("سمارت كاجوال", "Smart Casual"),
    UNIVERSITY("الجامعة والعمل اليومي", "University / Daily Work"),
    FORMAL_EVENT("مناسبة رسمية ومؤتمرات", "Formal Event"),
    FAMILY_GATHERING("لقاء عائلي", "Family Gathering"),
    EVENING_OUTING("سهرة مسائية", "Evening Outing"),
    TRADITIONAL("مناسبة تقليدية", "Traditional Event"),
    CUSTOM("مناسبة مخصصة", "Custom Occasion")
}

enum class PatternType(val arLabel: String, val enLabel: String) {
    SOLID("سادة (لون موحد)", "Solid"),
    STRIPED("مخطط", "Striped"),
    CHECKED("مربعات (كاروهات)", "Checked"),
    PATTERNED("نقوش وطبعات", "Patterned")
}

enum class SeasonType(val arLabel: String, val enLabel: String) {
    ALL_SEASON("كل الفصول", "All Seasons"),
    SUMMER("صيفي", "Summer"),
    WINTER("شتوي", "Winter"),
    SPRING_AUTUMN("ربيع / خريف", "Spring / Autumn")
}

@Entity(tableName = "wardrobe_items")
data class WardrobeItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // from ClothingCategory.name
    val garmentType: String,
    val primaryColor: String, // hex or color name
    val secondaryColor: String? = null,
    val style: String = ClothingStyle.CASUAL.name,
    val pattern: String = PatternType.SOLID.name,
    val fit: String = "Regular",
    val season: String = SeasonType.ALL_SEASON.name,
    val notes: String = "",
    val imagePath: String = "",
    val isFavorite: Boolean = false,
    val isAvailable: Boolean = true,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_outfits")
data class FavoriteOutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val occasion: String,
    val style: String,
    val score: Int,
    val stylingExplanation: String,
    val colorHarmonyExplanation: String,
    val topId: Long,
    val bottomId: Long,
    val footwearId: Long,
    val outerwearId: Long? = null,
    val accessoryId: Long? = null,
    val topName: String,
    val bottomName: String,
    val footwearName: String,
    val outerwearName: String? = null,
    val accessoryName: String? = null,
    val topImage: String,
    val bottomImage: String,
    val footwearImage: String,
    val outerwearImage: String? = null,
    val accessoryImage: String? = null,
    val topColor: String = "",
    val bottomColor: String = "",
    val footwearColor: String = "",
    val dateSaved: Long = System.currentTimeMillis(),
    val userFeedback: Int = 0 // 0 = none, 1 = liked, -1 = disliked
)
