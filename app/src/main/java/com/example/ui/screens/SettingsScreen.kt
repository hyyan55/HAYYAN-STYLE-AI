package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.util.AppStrings
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isArabic by viewModel.isArabic.collectAsState()

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var exportCopied by remember { mutableStateOf(false) }
    var showApkGuideDialog by remember { mutableStateOf(false) }

    val hasGeminiKey = com.example.ai.AiClothingRecognitionService.getApiKey() != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Title
            Text(
                text = if (isArabic) "الإعدادات وعن التطبيق" else "Settings & About",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Developer Identity & Ownership Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hayyan_style_icon_1792045752849),
                                contentDescription = "App Icon",
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = AppStrings.APP_NAME,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = AppStrings.getTagline(isArabic),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = AppStrings.getDeveloperCredit(isArabic),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isArabic)
                            "تطبيق HAYYAN STYLE AI هو مساعد أزياء شخصي متطور للرجال، مصمم لمساعدتك في تنظيم خزانة ملابسك الفعلية، واختبار تناغم الألوان، وابتكار أطقم أنيقة متناسقة مما تمتلكه دون تكاليف إضافية."
                        else
                            "HAYYAN STYLE AI is a dedicated menswear personal stylist application designed to help men organize their actual clothing, evaluate color harmony, and discover stylish outfit combinations with clothes they already own.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // WhatsApp Contact Button
                    Button(
                        onClick = {
                            val encodedMsg = Uri.encode(AppStrings.WHATSAPP_PREFILLED_MSG)
                            val waUri = Uri.parse("https://wa.me/249927309195?text=$encodedMsg")
                            val intent = Intent(Intent.ACTION_VIEW, waUri)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("whatsapp_contact_button")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_whatsapp),
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تواصل مع المطور عبر واتساب" else "Contact Developer on WhatsApp",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Android APK Download & Release Info
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تطبيق أندرويد (APK)" else "Android Application (APK)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = AppStrings.APP_VERSION,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isArabic)
                            "تطبيق HAYYAN STYLE AI جاهز كحزمة أندرويد أصلية (Native Android APK). يمكنك استخراج الحزمة عبر زر الإعدادات في AI Studio وتثبيتها مباشرة على هاتفك."
                        else
                            "HAYYAN STYLE AI is built as a complete native Android application. You can export the project or APK directly via AI Studio export options.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Download Android App Button (تحميل التطبيق للأندرويد)
                    Button(
                        onClick = { showApkGuideDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_apk_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تحميل التطبيق للأندرويد" else "Download Android App",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Language & Display Settings
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isArabic) "لغة الواجهة (Language)" else "Interface Language",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setLanguage(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (isArabic) ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(text = "العربية (افتراضي)", fontWeight = if (isArabic) FontWeight.Bold else FontWeight.Normal)
                        }

                        OutlinedButton(
                            onClick = { viewModel.setLanguage(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (!isArabic) ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            ) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(text = "English (LTR)", fontWeight = if (!isArabic) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        // Services Status (AI & Cloud)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isArabic) "حالة الخدمات السحابية والذكاء الاصطناعي" else "AI & Services Status",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Engine Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (hasGeminiKey) Color(0xFF2EB872) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "محرك الذكاء الاصطناعي" else "AI Stylist Engine",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (hasGeminiKey) {
                                        if (isArabic) "Google Gemini Vision متصل" else "Google Gemini Vision Connected"
                                    } else {
                                        if (isArabic) "المحلل المحلي نشط (بدون مفتاح)" else "Smart Local Vision Active"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = if (hasGeminiKey) Color(0xFF2EB872).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (hasGeminiKey) "ACTIVE" else "LOCAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (hasGeminiKey) Color(0xFF2EB872) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Storage Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "قاعدة البيانات والتخزين" else "Storage & Database",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isArabic) "تخزين محلي آمن مشفر على جهازك (Room SQLite)" else "Local Room SQLite on device (Zero cloud cost)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "LOCAL DB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Data Management (Export, Import, Reset)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isArabic) "إدارة البيانات والنسخ الاحتياطي" else "Data Management & Backup",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Export JSON Button
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val json = viewModel.exportWardrobeJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Wardrobe Export", json)
                                clipboard.setPrimaryClip(clip)
                                exportCopied = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = if (exportCopied) Icons.Default.Check else Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (exportCopied) {
                                if (isArabic) "تم نسخ بيانات الخزانة (JSON) للحافظة!" else "Exported & Copied JSON to Clipboard!"
                            } else {
                                if (isArabic) "تصدير بيانات الخزانة (JSON)" else "Export Wardrobe (JSON)"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Import JSON Button
                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isArabic) "استيراد خزانة سابقة (JSON)" else "Import Wardrobe (JSON)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reset to Starter Wardrobe
                    OutlinedButton(
                        onClick = { viewModel.resetToStarterWardrobe() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isArabic) "استعادة كبسولة الملابس الأساسية" else "Restore Capsule Wardrobe")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Clear All Data
                    OutlinedButton(
                        onClick = { showClearDataDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isArabic) "مسح جميع بيانات الخزانة" else "Clear All Local Data")
                    }
                }
            }
        }

        // Privacy Policy Note
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "الخصوصية والأمان" else "Privacy & Security",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isArabic)
                            "• صور ملابسك وبيانات الخزانة مخزنة محلياً على جهازك فقط.\n" +
                                    "• لا يتم إرسال أي صورة إلى خدمات الذكاء الاصطناعي إلا بعد نقرك الصريح على زر 'تحليل بالذكاء الاصطناعي'.\n" +
                                    "• التطبيق يعمل بالكامل دون الحاجة إلى إنشاء حساب أو دفع أي رسوم اشتراك."
                        else
                            "• All clothing images and wardrobe items are stored strictly locally on your device.\n" +
                                    "• No photos are sent to AI servers unless you explicitly tap 'AI Auto-Detect'.\n" +
                                    "• 100% usable without account sign-in or recurring cloud fees.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = {
                Text(text = if (isArabic) "تأكيد مسح البيانات" else "Confirm Clear Data")
            },
            text = {
                Text(text = if (isArabic) "هل أنت متأكد من رغبتك في حذف جميع الملابس والتنسيقات المحفوظة؟ لا يمكن التراجع عن هذه العملية." else "Are you sure you want to delete all wardrobe items and saved outfits? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = if (isArabic) "نعم، احذف الكل" else "Yes, Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // Import JSON Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(text = if (isArabic) "استيراد بيانات الخزانة" else "Import Wardrobe JSON")
            },
            text = {
                Column {
                    Text(
                        text = if (isArabic) "الصق نص الـ JSON المصدر سابقاً لاستعادة قطع الخزانة:" else "Paste previously exported JSON text below to restore wardrobe items:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("{\"app\": \"HAYYAN STYLE AI\", ...}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.importWardrobeJson(importJsonText)
                            if (success) {
                                showImportDialog = false
                                importJsonText = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = if (isArabic) "استيراد" else "Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(text = if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // APK Installation & Export Guide Dialog
    if (showApkGuideDialog) {
        AlertDialog(
            onDismissRequest = { showApkGuideDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isArabic) "تحميل وتثبيت تطبيق الأندرويد" else "Android App Download & Installation",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isArabic)
                            "تطبيق HAYYAN STYLE AI جاهز الآن كحزمة أندرويد أصلية (v1.0.0).\n\n" +
                                    "خطوات التثبيت:\n" +
                                    "1. يمكنك تنزيل ملف الـ APK أو تصدير المشروع كاملاً بصيغة ZIP من قائمة AI Studio.\n" +
                                    "2. عند فتح ملف الـ APK على هاتف أندرويد، قد يطلب النظام إذن 'تثبيت التطبيقات من مصادر غير معروفة'؛ وافق للمتابعة.\n" +
                                    "3. استمتع بتجربة منسق الأزياء الشخصي في أي وقت وبدون إنترنت!"
                        else
                            "HAYYAN STYLE AI is ready as a native Android package (v1.0.0).\n\n" +
                                    "Installation Steps:\n" +
                                    "1. You can export the compiled APK or download the project ZIP from the AI Studio top menu.\n" +
                                    "2. When opening the APK on your Android phone, Android may prompt to allow 'Install unknown apps'; proceed with installation.\n" +
                                    "3. Enjoy your personal AI menswear stylist anywhere, fully offline!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showApkGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = if (isArabic) "حسناً، فهمت" else "Got It")
                }
            }
        )
    }
}
