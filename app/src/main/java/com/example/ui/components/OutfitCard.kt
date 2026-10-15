package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.OutfitCandidate

@Composable
fun OutfitCard(
    candidate: OutfitCandidate,
    isArabic: Boolean,
    onSaveFavorite: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userRating by remember { mutableIntStateOf(0) } // -1, 0, 1
    var isSaved by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .testTag("outfit_card_${candidate.top.id}_${candidate.bottom.id}")
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Styling Score & Occasion Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Occasion Chip
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = candidate.occasion,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Estimated Score Badge
                Surface(
                    color = when {
                        candidate.score >= 90 -> Color(0xFF2EB872).copy(alpha = 0.15f)
                        candidate.score >= 80 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${candidate.score}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                candidate.score >= 90 -> Color(0xFF2EB872)
                                candidate.score >= 80 -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Garments Collage Row (Top + Bottom + Shoes + optional Outerwear)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top
                GarmentMiniPreview(
                    name = candidate.top.name,
                    imagePath = candidate.top.imagePath,
                    tag = if (isArabic) "علوي" else "Top",
                    colorHex = candidate.top.primaryColor,
                    modifier = Modifier.weight(1f)
                )

                // Bottom
                GarmentMiniPreview(
                    name = candidate.bottom.name,
                    imagePath = candidate.bottom.imagePath,
                    tag = if (isArabic) "سفلي" else "Bottom",
                    colorHex = candidate.bottom.primaryColor,
                    modifier = Modifier.weight(1f)
                )

                // Footwear
                GarmentMiniPreview(
                    name = candidate.footwear.name,
                    imagePath = candidate.footwear.imagePath,
                    tag = if (isArabic) "حذاء" else "Shoes",
                    colorHex = candidate.footwear.primaryColor,
                    modifier = Modifier.weight(1f)
                )

                // Optional Outerwear or Accessory
                if (candidate.outerwear != null) {
                    GarmentMiniPreview(
                        name = candidate.outerwear.name,
                        imagePath = candidate.outerwear.imagePath,
                        tag = if (isArabic) "سترة" else "Outer",
                        colorHex = candidate.outerwear.primaryColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Harmony Breakdown
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isArabic) "تناسق الألوان:" else "Color Harmony:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) candidate.colorHarmonyExplanationAr else candidate.colorHarmonyExplanationEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isArabic) "رأي المنسّق:" else "Stylist Advice:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) candidate.stylistExplanationAr else candidate.stylistExplanationEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions row: Save to favorites, Like, Dislike, Regenerate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Like / Dislike buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { userRating = if (userRating == 1) 0 else 1 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (userRating == 1) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = if (userRating == 1) Color(0xFF2EB872) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { userRating = if (userRating == -1) 0 else -1 },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (userRating == -1) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                            contentDescription = "Dislike",
                            tint = if (userRating == -1) Color(0xFFE05353) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onRegenerate,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate alternative",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Save to Favorites Button
                Button(
                    onClick = {
                        if (!isSaved) {
                            onSaveFavorite()
                            isSaved = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) Color(0xFF2EB872) else MaterialTheme.colorScheme.primary,
                        contentColor = if (isSaved) Color.White else MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSaved) {
                            if (isArabic) "محفوظة" else "Saved"
                        } else {
                            if (isArabic) "حفظ الإطلالة" else "Save Outfit"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun GarmentMiniPreview(
    name: String,
    imagePath: String,
    tag: String,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ClothingImage(
                imagePath = imagePath,
                contentDescription = name,
                modifier = Modifier.fillMaxSize()
            )

            // Tag badge
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(bottomEnd = 6.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        // Color dot & title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val defaultColor = MaterialTheme.colorScheme.primary
            val color = try {
                if (colorHex.startsWith("#")) {
                    Color(android.graphics.Color.parseColor(colorHex))
                } else {
                    defaultColor
                }
            } catch (_: Exception) {
                defaultColor
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(0.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
