package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [WardrobeItemEntity::class, FavoriteOutfitEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun favoriteOutfitDao(): FavoriteOutfitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hayyan_style_wardrobe.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with stylish capsule wardrobe
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).wardrobeDao().insertAll(getStarterCapsuleWardrobe())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        fun getStarterCapsuleWardrobe(): List<WardrobeItemEntity> {
            val now = System.currentTimeMillis()
            return listOf(
                // TOPS
                WardrobeItemEntity(
                    name = "قميص أكسفورد أبيض كلاسيكي (Oxford White Shirt)",
                    category = ClothingCategory.TOPS.name,
                    garmentType = "Shirt",
                    primaryColor = "#FFFFFF",
                    secondaryColor = null,
                    style = ClothingStyle.SMART_CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Slim Fit",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "قطعة أساسية متعددة الاستخدامات تناسب جميع المناسبات",
                    imagePath = "res:ic_category_top",
                    isFavorite = true,
                    dateAdded = now - 50000
                ),
                WardrobeItemEntity(
                    name = "تيشيرت كحلي قطني فاخر (Navy Blue Tee)",
                    category = ClothingCategory.TOPS.name,
                    garmentType = "T-shirt",
                    primaryColor = "#1D2A44",
                    secondaryColor = null,
                    style = ClothingStyle.CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Regular",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "خامة قطنية مريحة لإطلالة يومية راقية",
                    imagePath = "res:ic_category_top",
                    isFavorite = false,
                    dateAdded = now - 45000
                ),
                WardrobeItemEntity(
                    name = "بولو رمادي فحمي أنيق (Charcoal Polo Shirt)",
                    category = ClothingCategory.TOPS.name,
                    garmentType = "Polo",
                    primaryColor = "#333740",
                    secondaryColor = null,
                    style = ClothingStyle.SMART_CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Regular",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "تنسيق ممتاز مع البناطيل القماشية والجينز",
                    imagePath = "res:ic_category_top",
                    isFavorite = false,
                    dateAdded = now - 40000
                ),

                // BOTTOMS
                WardrobeItemEntity(
                    name = "بنطال تشينو بيج / كاكي (Classic Khaki Chinos)",
                    category = ClothingCategory.BOTTOMS.name,
                    garmentType = "Chinos",
                    primaryColor = "#C3B091",
                    secondaryColor = null,
                    style = ClothingStyle.SMART_CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Slim Fit",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "تناسق رائع مع الألوان الداكنة مثل الكحلي والزيتي والأسود",
                    imagePath = "res:ic_category_bottom",
                    isFavorite = true,
                    dateAdded = now - 35000
                ),
                WardrobeItemEntity(
                    name = "جينز نيلي غامق سادة (Dark Indigo Denim Jeans)",
                    category = ClothingCategory.BOTTOMS.name,
                    garmentType = "Jeans",
                    primaryColor = "#1B263B",
                    secondaryColor = null,
                    style = ClothingStyle.CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Regular",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "جينز نظيف بدون خدوش مناسب للعمل والجامعة",
                    imagePath = "res:ic_category_bottom",
                    isFavorite = true,
                    dateAdded = now - 30000
                ),
                WardrobeItemEntity(
                    name = "بنطال قماش رسمي رمادي داكن (Charcoal Wool Trousers)",
                    category = ClothingCategory.BOTTOMS.name,
                    garmentType = "Formal Trousers",
                    primaryColor = "#2B2D42",
                    secondaryColor = null,
                    style = ClothingStyle.FORMAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Tailored",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "للاجتماعات والمناسبات الرسمية",
                    imagePath = "res:ic_category_bottom",
                    isFavorite = false,
                    dateAdded = now - 25000
                ),

                // FOOTWEAR
                WardrobeItemEntity(
                    name = "حذاء سنيكرز أبيض مينيماليست (White Minimalist Sneakers)",
                    category = ClothingCategory.FOOTWEAR.name,
                    garmentType = "Sneakers",
                    primaryColor = "#F5F5F7",
                    secondaryColor = null,
                    style = ClothingStyle.CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Standard",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "حذاء جلدي أبيض نقي يتناسق مع 90% من الإطلالات",
                    imagePath = "res:ic_category_shoes",
                    isFavorite = true,
                    dateAdded = now - 20000
                ),
                WardrobeItemEntity(
                    name = "حذاء لوفر جلدي بني أنيق (Brown Leather Loafers)",
                    category = ClothingCategory.FOOTWEAR.name,
                    garmentType = "Loafers",
                    primaryColor = "#6F4E37",
                    secondaryColor = null,
                    style = ClothingStyle.SMART_CASUAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Standard",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "يعطي لمسة فاخرة لإطلالات السمارت كاجوال",
                    imagePath = "res:ic_category_shoes",
                    isFavorite = true,
                    dateAdded = now - 15000
                ),
                WardrobeItemEntity(
                    name = "حذاء تشيلسي أسود فخم (Black Chelsea Boots)",
                    category = ClothingCategory.FOOTWEAR.name,
                    garmentType = "Boots",
                    primaryColor = "#111111",
                    secondaryColor = null,
                    style = ClothingStyle.CLASSIC.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Standard",
                    season = SeasonType.WINTER.name,
                    notes = "أناقة شتوية ومسائية فريدة",
                    imagePath = "res:ic_category_shoes",
                    isFavorite = false,
                    dateAdded = now - 10000
                ),

                // OUTERWEAR
                WardrobeItemEntity(
                    name = "بليزر كحلي إيطالي (Navy Tailored Blazer)",
                    category = ClothingCategory.OUTERWEAR.name,
                    garmentType = "Blazer",
                    primaryColor = "#14213D",
                    secondaryColor = null,
                    style = ClothingStyle.FORMAL.name,
                    pattern = PatternType.SOLID.name,
                    fit = "Tailored",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "القطعة الذهبية في دولاب الرجل الأنيق",
                    imagePath = "res:ic_category_jacket",
                    isFavorite = true,
                    dateAdded = now - 5000
                ),

                // ACCESSORIES
                WardrobeItemEntity(
                    name = "ساعة كرونوغراف ستيل وفضية (Minimalist Chrono Watch)",
                    category = ClothingCategory.ACCESSORIES.name,
                    garmentType = "Watch",
                    primaryColor = "#D4AF37",
                    secondaryColor = "#C0C0C0",
                    style = ClothingStyle.CLASSIC.name,
                    pattern = PatternType.SOLID.name,
                    fit = "40mm",
                    season = SeasonType.ALL_SEASON.name,
                    notes = "تضفي لمسة من الوقار والدقة في المظهر",
                    imagePath = "res:ic_category_accessory",
                    isFavorite = true,
                    dateAdded = now
                )
            )
        }
    }
}
