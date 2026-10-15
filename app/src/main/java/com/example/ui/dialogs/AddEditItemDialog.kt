package com.example.ui.dialogs

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ClothingCategory
import com.example.data.ClothingStyle
import com.example.data.PatternType
import com.example.data.SeasonType
import com.example.data.WardrobeItemEntity
import com.example.ui.components.ClothingImage
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditItemDialog(
    itemToEdit: WardrobeItemEntity? = null,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val lastAiAnalysis by viewModel.lastAiAnalysis.collectAsState()

    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(itemToEdit?.category ?: ClothingCategory.TOPS.name) }
    var garmentType by remember { mutableStateOf(itemToEdit?.garmentType ?: "Shirt") }
    var primaryColor by remember { mutableStateOf(itemToEdit?.primaryColor ?: "#FFFFFF") }
    var secondaryColor by remember { mutableStateOf(itemToEdit?.secondaryColor ?: "") }
    var selectedStyle by remember { mutableStateOf(itemToEdit?.style ?: ClothingStyle.SMART_CASUAL.name) }
    var selectedPattern by remember { mutableStateOf(itemToEdit?.pattern ?: PatternType.SOLID.name) }
    var selectedSeason by remember { mutableStateOf(itemToEdit?.season ?: SeasonType.ALL_SEASON.name) }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }
    var isAvailable by remember { mutableStateOf(itemToEdit?.isAvailable ?: true) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var aiMessage by remember { mutableStateOf<String?>(null) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            aiMessage = null
        }
    }

    // When AI analysis completes, populate fields automatically
    LaunchedEffect(lastAiAnalysis) {
        lastAiAnalysis?.let { analysis ->
            selectedCategory = analysis.category
            garmentType = analysis.itemType
            primaryColor = analysis.primaryColor
            analysis.secondaryColor?.let { secondaryColor = it }
            selectedStyle = analysis.style
            selectedPattern = analysis.pattern
            if (name.isBlank()) {
                name = if (isArabic) analysis.suggestedDescriptionAr else analysis.suggestedDescriptionEn
            }
            aiMessage = if (isArabic) analysis.messageAr else analysis.messageEn
        }
    }

    val predefinedMenswearColors = listOf(
        Pair("#FFFFFF", if (isArabic) "أبيض" else "White"),
        Pair("#0F1115", if (isArabic) "أسود" else "Black"),
        Pair("#1D2A44", if (isArabic) "كحلي" else "Navy"),
        Pair("#373D4A", if (isArabic) "فحمي" else "Charcoal"),
        Pair("#8A92A0", if (isArabic) "رمادي" else "Gray"),
        Pair("#C3B091", if (isArabic) "بيج / كاكي" else "Beige / Khaki"),
        Pair("#6F4E37", if (isArabic) "بني" else "Brown"),
        Pair("#3B4D3C", if (isArabic) "زيتي" else "Olive"),
        Pair("#722F37", if (isArabic) "خمري" else "Burgundy"),
        Pair("#C89D7C", if (isArabic) "جملي" else "Camel")
    )

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearLastAiAnalysis()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("add_edit_item_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (itemToEdit == null) {
                        if (isArabic) "إضافة قطعة ملابس جديدة" else "Add New Garment"
                    } else {
                        if (isArabic) "تعديل بيانات القطعة" else "Edit Garment"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = {
                        viewModel.clearLastAiAnalysis()
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Upload & AI Auto-Detect Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            selectedImageUri != null -> {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected garment",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            itemToEdit != null -> {
                                ClothingImage(
                                    imagePath = itemToEdit.imagePath,
                                    contentDescription = itemToEdit.name,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isArabic) "اختر صورة القطعة" else "Choose Photo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isArabic) "تغيير الصورة" else "Pick Photo",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedImageUri != null) {
                                    try {
                                        val stream = context.contentResolver.openInputStream(selectedImageUri!!)
                                        val bitmap = BitmapFactory.decodeStream(stream)
                                        stream?.close()
                                        if (bitmap != null) {
                                            viewModel.analyzeImageForNewItem(bitmap)
                                        }
                                    } catch (_: Exception) {
                                    }
                                } else {
                                    aiMessage = if (isArabic) "يرجى اختيار صورة أولاً لتفعيل التحليل بالذكاء الاصطناعي" else "Please select a photo first for AI recognition"
                                }
                            },
                            enabled = !isAiAnalyzing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("ai_detect_button")
                        ) {
                            if (isAiAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "جاري التحليل..." else "Analyzing...",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "تحليل بالذكاء الاصطناعي" else "AI Auto-Detect",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (!aiMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Garment Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isArabic) "اسم القطعة (مثل: قميص كتان أبيض)" else "Garment Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("item_name_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selection Chips
            Text(
                text = if (isArabic) "التصنيف:" else "Category:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClothingCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.name,
                        onClick = { selectedCategory = cat.name },
                        label = { Text(if (isArabic) cat.arLabel else cat.enLabel, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Garment Type Input
            OutlinedTextField(
                value = garmentType,
                onValueChange = { garmentType = it },
                label = { Text(if (isArabic) "نوع القطعة (Shirt, T-Shirt, Chinos, Jeans, Loafers...)" else "Garment Type") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Color Picker Palette
            Text(
                text = if (isArabic) "اللون الأساسي:" else "Primary Color:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                predefinedMenswearColors.forEach { (hex, label) ->
                    val color = try {
                        Color(android.graphics.Color.parseColor(hex))
                    } catch (_: Exception) {
                        MaterialTheme.colorScheme.primary
                    }
                    val isSelected = primaryColor.equals(hex, ignoreCase = true)

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { primaryColor = hex }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(0.5.dp, Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Style Selection Chips
            Text(
                text = if (isArabic) "الستايل والمظهر:" else "Style:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClothingStyle.entries.forEach { st ->
                    FilterChip(
                        selected = selectedStyle == st.name,
                        onClick = { selectedStyle = st.name },
                        label = { Text(if (isArabic) st.arLabel else st.enLabel, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pattern Selection
            Text(
                text = if (isArabic) "النقش:" else "Pattern:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PatternType.entries.forEach { pat ->
                    FilterChip(
                        selected = selectedPattern == pat.name,
                        onClick = { selectedPattern = pat.name },
                        label = { Text(if (isArabic) pat.arLabel else pat.enLabel, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Availability Toggle Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "متاحة للتنسيق في الإطلالات" else "Available for Outfits",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "قم بالتعطيل إذا كانت القطعة في الغسيل أو غير جاهزة" else "Turn off if garment is currently in laundry or repair",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { isAvailable = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (isArabic) "ملاحظات إضافية (اختياري)" else "Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Save / Delete / Cancel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (itemToEdit != null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteItem(itemToEdit)
                            viewModel.clearLastAiAnalysis()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                    }
                }

                Button(
                    onClick = {
                        val finalName = name.ifBlank {
                            if (isArabic) "قطعة ملابس $garmentType" else "$garmentType item"
                        }
                        if (itemToEdit == null) {
                            viewModel.addNewItem(
                                name = finalName,
                                category = selectedCategory,
                                garmentType = garmentType,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor.ifBlank { null },
                                style = selectedStyle,
                                pattern = selectedPattern,
                                fit = "Regular",
                                season = selectedSeason,
                                notes = notes,
                                imageUri = selectedImageUri,
                                onComplete = {
                                    viewModel.clearLastAiAnalysis()
                                    onDismiss()
                                }
                            )
                        } else {
                            val updated = itemToEdit.copy(
                                name = finalName,
                                category = selectedCategory,
                                garmentType = garmentType,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor.ifBlank { null },
                                style = selectedStyle,
                                pattern = selectedPattern,
                                season = selectedSeason,
                                notes = notes,
                                isAvailable = isAvailable
                            )
                            viewModel.updateItem(
                                item = updated,
                                newImageUri = selectedImageUri,
                                onComplete = {
                                    viewModel.clearLastAiAnalysis()
                                    onDismiss()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .testTag("save_garment_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "حفظ في الخزانة" else "Save to Wardrobe",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
