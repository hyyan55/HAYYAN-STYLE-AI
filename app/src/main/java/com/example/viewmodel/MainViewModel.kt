package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiClothingAnalysisResult
import com.example.ai.AiClothingRecognitionService
import com.example.data.AppDatabase
import com.example.data.ClothingCategory
import com.example.data.ClothingStyle
import com.example.data.FavoriteOutfitEntity
import com.example.data.Occasion
import com.example.data.PatternType
import com.example.data.SeasonType
import com.example.data.WardrobeItemEntity
import com.example.engine.OutfitCandidate
import com.example.engine.OutfitStylingEngine
import com.example.engine.StylingResult
import com.example.util.ImageStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val wardrobeDao = db.wardrobeDao()
    private val favoriteOutfitDao = db.favoriteOutfitDao()

    // Preferences & Localization (Arabic is default)
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Wardrobe Data
    val wardrobeItems: StateFlow<List<WardrobeItemEntity>> = wardrobeDao.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalItemCount: StateFlow<Int> = wardrobeDao.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val favoriteOutfits: StateFlow<List<FavoriteOutfitEntity>> = favoriteOutfitDao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtering State
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null) // null = all
    val selectedStyleFilter = MutableStateFlow<String?>(null)

    // Stylist Engine State
    val stylistOccasion = MutableStateFlow(Occasion.SMART_CASUAL.name)
    val stylistStyle = MutableStateFlow(ClothingStyle.SMART_CASUAL.name)
    val stylistStartingItem = MutableStateFlow<WardrobeItemEntity?>(null)
    val stylistExcludedIds = MutableStateFlow<Set<Long>>(emptySet())
    val stylistPreferredColor = MutableStateFlow<String?>(null)

    private val _stylingResult = MutableStateFlow<StylingResult?>(null)
    val stylingResult: StateFlow<StylingResult?> = _stylingResult.asStateFlow()

    private val _isStylingLoading = MutableStateFlow(false)
    val isStylingLoading: StateFlow<Boolean> = _isStylingLoading.asStateFlow()

    // AI Analysis State
    private val _isAiAnalyzing = MutableStateFlow(false)
    val isAiAnalyzing: StateFlow<Boolean> = _isAiAnalyzing.asStateFlow()

    private val _lastAiAnalysis = MutableStateFlow<AiClothingAnalysisResult?>(null)
    val lastAiAnalysis: StateFlow<AiClothingAnalysisResult?> = _lastAiAnalysis.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    fun setLanguage(arabic: Boolean) {
        _isArabic.value = arabic
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    // Wardrobe CRUD
    fun addNewItem(
        name: String,
        category: String,
        garmentType: String,
        primaryColor: String,
        secondaryColor: String?,
        style: String,
        pattern: String,
        fit: String,
        season: String,
        notes: String,
        imageUri: Uri?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            var savedPath = "res:ic_category_top"
            if (imageUri != null) {
                try {
                    savedPath = ImageStorageManager.saveImageToInternalStorage(getApplication(), imageUri)
                } catch (_: Exception) {
                    savedPath = when (category) {
                        ClothingCategory.BOTTOMS.name -> "res:ic_category_bottom"
                        ClothingCategory.FOOTWEAR.name -> "res:ic_category_shoes"
                        ClothingCategory.OUTERWEAR.name -> "res:ic_category_jacket"
                        ClothingCategory.ACCESSORIES.name -> "res:ic_category_accessory"
                        else -> "res:ic_category_top"
                    }
                }
            } else {
                savedPath = when (category) {
                    ClothingCategory.BOTTOMS.name -> "res:ic_category_bottom"
                    ClothingCategory.FOOTWEAR.name -> "res:ic_category_shoes"
                    ClothingCategory.OUTERWEAR.name -> "res:ic_category_jacket"
                    ClothingCategory.ACCESSORIES.name -> "res:ic_category_accessory"
                    else -> "res:ic_category_top"
                }
            }

            val newItem = WardrobeItemEntity(
                name = name.ifBlank { "قطعة ملابس جديدة" },
                category = category,
                garmentType = garmentType.ifBlank { "Garment" },
                primaryColor = primaryColor.ifBlank { "#1A1D20" },
                secondaryColor = secondaryColor,
                style = style,
                pattern = pattern,
                fit = fit,
                season = season,
                notes = notes,
                imagePath = savedPath,
                isFavorite = false,
                isAvailable = true,
                dateAdded = System.currentTimeMillis()
            )

            wardrobeDao.insertItem(newItem)
            _userFeedbackMessage.value = if (_isArabic.value) "تمت إضافة القطعة بنجاح إلى خزانتك" else "Item added to your wardrobe successfully"
            onComplete()
        }
    }

    fun updateItem(item: WardrobeItemEntity, newImageUri: Uri? = null, onComplete: () -> Unit) {
        viewModelScope.launch {
            var updatedPath = item.imagePath
            if (newImageUri != null) {
                try {
                    // Delete old file if local
                    ImageStorageManager.deleteImageFromInternalStorage(item.imagePath)
                    updatedPath = ImageStorageManager.saveImageToInternalStorage(getApplication(), newImageUri)
                } catch (_: Exception) {
                }
            }
            val updated = item.copy(imagePath = updatedPath)
            wardrobeDao.updateItem(updated)
            _userFeedbackMessage.value = if (_isArabic.value) "تم تحديث بيانات القطعة" else "Item updated successfully"
            onComplete()
        }
    }

    fun deleteItem(item: WardrobeItemEntity) {
        viewModelScope.launch {
            ImageStorageManager.deleteImageFromInternalStorage(item.imagePath)
            wardrobeDao.deleteItem(item)
            _userFeedbackMessage.value = if (_isArabic.value) "تم حذف القطعة من الخزانة" else "Item deleted from wardrobe"
        }
    }

    fun toggleItemFavorite(item: WardrobeItemEntity) {
        viewModelScope.launch {
            wardrobeDao.setFavorite(item.id, !item.isFavorite)
        }
    }

    fun toggleItemAvailable(item: WardrobeItemEntity) {
        viewModelScope.launch {
            wardrobeDao.setAvailable(item.id, !item.isAvailable)
        }
    }

    // AI Multimodal Analysis
    fun analyzeImageForNewItem(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAiAnalyzing.value = true
            val result = AiClothingRecognitionService.analyzeImage(bitmap)
            _lastAiAnalysis.value = result
            _isAiAnalyzing.value = false
        }
    }

    fun clearLastAiAnalysis() {
        _lastAiAnalysis.value = null
    }

    // Outfit Stylist Action
    fun generateOutfits() {
        viewModelScope.launch {
            _isStylingLoading.value = true
            val items = wardrobeDao.getAvailableItemsSync()
            val result = OutfitStylingEngine.generateOutfits(
                wardrobe = items,
                occasion = stylistOccasion.value,
                targetStyle = stylistStyle.value,
                startingItemId = stylistStartingItem.value?.id,
                excludedItemIds = stylistExcludedIds.value,
                preferredColor = stylistPreferredColor.value
            )
            _stylingResult.value = result
            _isStylingLoading.value = false
        }
    }

    fun saveOutfitToFavorites(candidate: OutfitCandidate) {
        viewModelScope.launch {
            val isAr = _isArabic.value
            val entity = FavoriteOutfitEntity(
                occasion = candidate.occasion,
                style = candidate.style,
                score = candidate.score,
                stylingExplanation = if (isAr) candidate.stylistExplanationAr else candidate.stylistExplanationEn,
                colorHarmonyExplanation = if (isAr) candidate.colorHarmonyExplanationAr else candidate.colorHarmonyExplanationEn,
                topId = candidate.top.id,
                bottomId = candidate.bottom.id,
                footwearId = candidate.footwear.id,
                outerwearId = candidate.outerwear?.id,
                accessoryId = candidate.accessory?.id,
                topName = candidate.top.name,
                bottomName = candidate.bottom.name,
                footwearName = candidate.footwear.name,
                outerwearName = candidate.outerwear?.name,
                accessoryName = candidate.accessory?.name,
                topImage = candidate.top.imagePath,
                bottomImage = candidate.bottom.imagePath,
                footwearImage = candidate.footwear.imagePath,
                outerwearImage = candidate.outerwear?.imagePath,
                accessoryImage = candidate.accessory?.imagePath,
                topColor = candidate.top.primaryColor,
                bottomColor = candidate.bottom.primaryColor,
                footwearColor = candidate.footwear.primaryColor,
                dateSaved = System.currentTimeMillis()
            )
            favoriteOutfitDao.insertFavorite(entity)
            _userFeedbackMessage.value = if (isAr) "تم حفظ الإطلالة في المفضلة" else "Outfit saved to favorites"
        }
    }

    fun deleteFavorite(outfitId: Long) {
        viewModelScope.launch {
            favoriteOutfitDao.deleteFavoriteById(outfitId)
            _userFeedbackMessage.value = if (_isArabic.value) "تمت إزالة الإطلالة من المفضلة" else "Outfit removed from favorites"
        }
    }

    fun rateFavorite(outfitId: Long, feedback: Int) {
        viewModelScope.launch {
            favoriteOutfitDao.updateFeedback(outfitId, feedback)
        }
    }

    // Data Management
    suspend fun exportWardrobeJson(): String = withContext(Dispatchers.IO) {
        val items = db.wardrobeDao().getAvailableItemsSync()
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("name", item.name)
                put("category", item.category)
                put("garmentType", item.garmentType)
                put("primaryColor", item.primaryColor)
                put("secondaryColor", item.secondaryColor ?: "")
                put("style", item.style)
                put("pattern", item.pattern)
                put("fit", item.fit)
                put("season", item.season)
                put("notes", item.notes)
            }
            array.put(obj)
        }
        val root = JSONObject().apply {
            put("app", "HAYYAN STYLE AI")
            put("version", "1.0.0")
            put("exportDate", System.currentTimeMillis())
            put("items", array)
        }
        root.toString(2)
    }

    suspend fun importWardrobeJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            val itemsArray = root.getJSONArray("items")
            val importedList = mutableListOf<WardrobeItemEntity>()
            for (i in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(i)
                importedList.add(
                    WardrobeItemEntity(
                        name = itemObj.getString("name"),
                        category = itemObj.optString("category", ClothingCategory.TOPS.name),
                        garmentType = itemObj.optString("garmentType", "Garment"),
                        primaryColor = itemObj.optString("primaryColor", "#1A1D20"),
                        secondaryColor = itemObj.optString("secondaryColor").ifBlank { null },
                        style = itemObj.optString("style", ClothingStyle.CASUAL.name),
                        pattern = itemObj.optString("pattern", PatternType.SOLID.name),
                        fit = itemObj.optString("fit", "Regular"),
                        season = itemObj.optString("season", SeasonType.ALL_SEASON.name),
                        notes = itemObj.optString("notes", ""),
                        imagePath = "res:ic_category_top",
                        isFavorite = false,
                        isAvailable = true,
                        dateAdded = System.currentTimeMillis()
                    )
                )
            }
            if (importedList.isNotEmpty()) {
                wardrobeDao.insertAll(importedList)
                return@withContext true
            }
        } catch (_: Exception) {
        }
        return@withContext false
    }

    fun clearAllData() {
        viewModelScope.launch {
            wardrobeDao.clearAll()
            favoriteOutfitDao.clearAll()
            _stylingResult.value = null
            _userFeedbackMessage.value = if (_isArabic.value) "تم مسح جميع بيانات الخزانة" else "All wardrobe data cleared"
        }
    }

    fun resetToStarterWardrobe() {
        viewModelScope.launch {
            wardrobeDao.clearAll()
            wardrobeDao.insertAll(AppDatabase.getStarterCapsuleWardrobe())
            _stylingResult.value = null
            _userFeedbackMessage.value = if (_isArabic.value) "تمت استعادة كبسولة الملابس الأساسية" else "Capsule wardrobe restored"
        }
    }
}
