package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.ClothingCategory
import com.example.data.ClothingStyle
import com.example.data.PatternType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class AiClothingAnalysisResult(
    val category: String,
    val itemType: String,
    val primaryColor: String,
    val secondaryColor: String?,
    val pattern: String,
    val style: String,
    val confidence: Float,
    val suggestedDescriptionAr: String,
    val suggestedDescriptionEn: String,
    val isAiPowered: Boolean,
    val messageAr: String,
    val messageEn: String
)

object AiClothingRecognitionService {

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun getApiKey(): String? {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") null else key
        } catch (_: Exception) {
            null
        }
    }

    suspend fun analyzeImage(bitmap: Bitmap): AiClothingAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()

        if (apiKey.isNullOrBlank()) {
            // Local fallback analysis via pixel sampling
            return@withContext performLocalFallbackAnalysis(bitmap, isAiAvailable = false)
        }

        try {
            // Resize bitmap for low bandwidth and token cost
            val scaled = resizeForAi(bitmap, 640)
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            val prompt = """
                Analyze this menswear clothing item image.
                Return ONLY valid JSON matching this schema:
                {
                  "category": "TOPS" | "BOTTOMS" | "FOOTWEAR" | "OUTERWEAR" | "ACCESSORIES",
                  "item_type": "Shirt" | "T-shirt" | "Polo" | "Sweater" | "Hoodie" | "Jeans" | "Chinos" | "Formal Trousers" | "Sneakers" | "Loafers" | "Boots" | "Blazer" | "Watch" | "Belt" | "Other",
                  "primary_color": "Hex code like #1D2A44 or common name",
                  "secondary_color": "Hex code or null",
                  "pattern": "SOLID" | "STRIPED" | "CHECKED" | "PATTERNED",
                  "style": "MINIMALIST" | "CASUAL" | "SMART_CASUAL" | "FORMAL" | "SPORTY" | "CLASSIC" | "STREETWEAR" | "TRADITIONAL",
                  "confidence": 0.95,
                  "description_ar": "وصف موجز ودقيق للقطعة باللغة العربية",
                  "description_en": "Concise menswear garment description"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                            put(JSONObject().apply {
                                put("inline_data", JSONObject().apply {
                                    put("mime_type", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("response_mime_type", "application/json")
                    put("temperature", 0.2)
                })
            }

            // Using gemini-2.5-flash endpoint for fast multimodal vision
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(requestBody).build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val parsed = parseGeminiResponse(responseBody)
                if (parsed != null) {
                    return@withContext parsed
                }
            }
        } catch (_: Exception) {
            // Fallback gracefully on network error or quota
        }

        return@withContext performLocalFallbackAnalysis(bitmap, isAiAvailable = false)
    }

    private fun parseGeminiResponse(rawJson: String): AiClothingAnalysisResult? {
        return try {
            val root = JSONObject(rawJson)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            val text = parts.getJSONObject(0).optString("text")

            val itemJson = JSONObject(text.trim())

            var category = itemJson.optString("category", ClothingCategory.TOPS.name).uppercase()
            if (!ClothingCategory.entries.any { it.name == category }) {
                category = ClothingCategory.TOPS.name
            }

            var style = itemJson.optString("style", ClothingStyle.CASUAL.name).uppercase()
            if (!ClothingStyle.entries.any { it.name == style }) {
                style = ClothingStyle.CASUAL.name
            }

            var pattern = itemJson.optString("pattern", PatternType.SOLID.name).uppercase()
            if (!PatternType.entries.any { it.name == pattern }) {
                pattern = PatternType.SOLID.name
            }

            AiClothingAnalysisResult(
                category = category,
                itemType = itemJson.optString("item_type", "Garment"),
                primaryColor = itemJson.optString("primary_color", "#1A1D20"),
                secondaryColor = if (itemJson.isNull("secondary_color")) null else itemJson.optString("secondary_color"),
                pattern = pattern,
                style = style,
                confidence = itemJson.optDouble("confidence", 0.9).toFloat(),
                suggestedDescriptionAr = itemJson.optString("description_ar", "قطعة ملابس رجالية"),
                suggestedDescriptionEn = itemJson.optString("description_en", "Menswear item"),
                isAiPowered = true,
                messageAr = "تم التعرف بنجاح عبر الذكاء الاصطناعي. يمكنك تعديل أي حقل قبل الحفظ.",
                messageEn = "AI recognition complete. You can confirm or adjust details before saving."
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun performLocalFallbackAnalysis(bitmap: Bitmap, isAiAvailable: Boolean): AiClothingAnalysisResult {
        // Sample center pixels to find dominant color
        val dominantHex = extractDominantCenterColor(bitmap)

        val messageAr = if (!isAiAvailable) {
            "وضع التحليل المحلي نشط (بدون مفتاح ذكاء اصطناعي). تم استخراج اللون الرئيسي ويمكنك تحديد نوع القطعة يدوياً."
        } else {
            "تعذر الاتصال بخدمة الذكاء الاصطناعي السحابية. تم استخدام المحلل المحلي."
        }

        val messageEn = if (!isAiAvailable) {
            "Local analysis active (no cloud AI key). Dominant color extracted for quick manual entry."
        } else {
            "Cloud AI unreachable. Switched to local color extraction."
        }

        return AiClothingAnalysisResult(
            category = ClothingCategory.TOPS.name,
            itemType = "Shirt",
            primaryColor = dominantHex,
            secondaryColor = null,
            pattern = PatternType.SOLID.name,
            style = ClothingStyle.CASUAL.name,
            confidence = 0.70f,
            suggestedDescriptionAr = "قطعة ملابس بلون $dominantHex",
            suggestedDescriptionEn = "Garment in $dominantHex",
            isAiPowered = false,
            messageAr = messageAr,
            messageEn = messageEn
        )
    }

    private fun extractDominantCenterColor(bitmap: Bitmap): String {
        try {
            val width = bitmap.width
            val height = bitmap.height
            val centerX = width / 2
            val centerY = height / 2

            var rSum = 0L
            var gSum = 0L
            var bSum = 0L
            var count = 0

            val sampleRadius = (minOf(width, height) / 6).coerceAtLeast(10)
            val step = (sampleRadius / 6).coerceAtLeast(1)

            for (x in (centerX - sampleRadius)..(centerX + sampleRadius) step step) {
                for (y in (centerY - sampleRadius)..(centerY + sampleRadius) step step) {
                    if (x in 0 until width && y in 0 until height) {
                        val pixel = bitmap.getPixel(x, y)
                        rSum += (pixel shr 16) and 0xFF
                        gSum += (pixel shr 8) and 0xFF
                        bSum += pixel and 0xFF
                        count++
                    }
                }
            }

            if (count > 0) {
                val avgR = (rSum / count).toInt()
                val avgG = (gSum / count).toInt()
                val avgB = (bSum / count).toInt()
                return String.format("#%02X%02X%02X", avgR, avgG, avgB)
            }
        } catch (_: Exception) {
        }
        return "#1E2024"
    }

    private fun resizeForAi(bitmap: Bitmap, maxDim: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDim && h <= maxDim) return bitmap

        val ratio = w.toFloat() / h.toFloat()
        val targetW: Int
        val targetH: Int
        if (ratio > 1) {
            targetW = maxDim
            targetH = (maxDim / ratio).toInt()
        } else {
            targetH = maxDim
            targetW = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
    }
}
