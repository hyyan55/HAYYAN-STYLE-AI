package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.WardrobeItemEntity
import com.example.ui.dialogs.AddEditItemDialog
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OutfitStylistScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WardrobeScreen
import com.example.ui.theme.HayyanStyleTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainAppContent()
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<WardrobeItemEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Dynamic RTL for Arabic (default) and LTR for English
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // BackHandler: if not on Home tab, go to Home tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        HayyanStyleTheme(darkTheme = isDarkMode) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        // 1. Home
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { viewModel.setTab(0) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text(if (isArabic) "الرئيسية" else "Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_home")
                        )

                        // 2. My Wardrobe
                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { viewModel.setTab(1) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 1) Icons.Filled.Checkroom else Icons.Outlined.Checkroom,
                                    contentDescription = "Wardrobe"
                                )
                            },
                            label = { Text(if (isArabic) "خزانتي" else "Wardrobe") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_wardrobe")
                        )

                        // 3. Outfit Stylist
                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { viewModel.setTab(2) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 2) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                    contentDescription = "Stylist"
                                )
                            },
                            label = { Text(if (isArabic) "المنسّق" else "Stylist") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_stylist")
                        )

                        // 4. Favorites
                        NavigationBarItem(
                            selected = currentTab == 3,
                            onClick = { viewModel.setTab(3) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 3) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Favorites"
                                )
                            },
                            label = { Text(if (isArabic) "المفضلة" else "Favorites") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_favorites")
                        )

                        // 5. Settings & About
                        NavigationBarItem(
                            selected = currentTab == 4,
                            onClick = { viewModel.setTab(4) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 4) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text(if (isArabic) "الإعدادات" else "Settings") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_settings")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToTab = { tabIndex -> viewModel.setTab(tabIndex) },
                            onOpenAddItem = {
                                itemToEdit = null
                                showAddEditDialog = true
                            }
                        )
                        1 -> WardrobeScreen(
                            viewModel = viewModel,
                            onAddItemClick = {
                                itemToEdit = null
                                showAddEditDialog = true
                            },
                            onEditItemClick = { item ->
                                itemToEdit = item
                                showAddEditDialog = true
                            }
                        )
                        2 -> OutfitStylistScreen(
                            viewModel = viewModel,
                            onNavigateToAddItem = {
                                itemToEdit = null
                                showAddEditDialog = true
                            }
                        )
                        3 -> FavoritesScreen(
                            viewModel = viewModel,
                            onNavigateToStylist = { viewModel.setTab(2) }
                        )
                        4 -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }

                if (showAddEditDialog) {
                    AddEditItemDialog(
                        itemToEdit = itemToEdit,
                        viewModel = viewModel,
                        onDismiss = {
                            showAddEditDialog = false
                            itemToEdit = null
                        }
                    )
                }
            }
        }
    }
}
