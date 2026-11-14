package com.mutarjimi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mutarjimi.app.ui.screens.HistoryScreen
import com.mutarjimi.app.ui.screens.TranslatorScreen
import com.mutarjimi.app.ui.theme.MutarjimiTheme
import com.mutarjimi.app.ui.viewmodel.TranslatorViewModel

private enum class AppTab(val label: String) {
    TRANSLATE("الترجمة"),
    HISTORY("السجل"),
    FAVORITES("المفضلة")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(systemDark) }
            var currentTab by rememberSaveable { mutableStateOf(AppTab.TRANSLATE) }

            val translatorViewModel: TranslatorViewModel = viewModel()
            val history by translatorViewModel.historyList.collectAsState()
            val favorites by translatorViewModel.favoritesList.collectAsState()

            MutarjimiTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentTab == AppTab.TRANSLATE,
                                onClick = { currentTab = AppTab.TRANSLATE },
                                icon = { Icon(Icons.Rounded.Translate, contentDescription = null) },
                                label = { Text(AppTab.TRANSLATE.label) }
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.HISTORY,
                                onClick = { currentTab = AppTab.HISTORY },
                                icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                                label = { Text(AppTab.HISTORY.label) }
                            )
                            NavigationBarItem(
                                selected = currentTab == AppTab.FAVORITES,
                                onClick = { currentTab = AppTab.FAVORITES },
                                icon = { Icon(Icons.Rounded.Star, contentDescription = null) },
                                label = { Text(AppTab.FAVORITES.label) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            AppTab.TRANSLATE -> {
                                TranslatorScreen(
                                    viewModel = translatorViewModel,
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                                )
                            }
                            AppTab.HISTORY -> {
                                HistoryScreen(
                                    title = "سجل الترجمات",
                                    items = history,
                                    emptyMessage = "لا يوجد سجل ترجمات بعد",
                                    showClearAll = true,
                                    onItemClick = {
                                        translatorViewModel.loadFromHistory(it)
                                        currentTab = AppTab.TRANSLATE
                                    },
                                    onToggleFavorite = { translatorViewModel.toggleItemFavorite(it) },
                                    onDeleteItem = { translatorViewModel.deleteHistoryItem(it) },
                                    onClearAll = { translatorViewModel.clearAllHistory() }
                                )
                            }
                            AppTab.FAVORITES -> {
                                HistoryScreen(
                                    title = "المفضلة",
                                    items = favorites,
                                    emptyMessage = "لا توجد ترجمات مفضلة بعد",
                                    showClearAll = false,
                                    onItemClick = {
                                        translatorViewModel.loadFromHistory(it)
                                        currentTab = AppTab.TRANSLATE
                                    },
                                    onToggleFavorite = { translatorViewModel.toggleItemFavorite(it) },
                                    onDeleteItem = { translatorViewModel.deleteHistoryItem(it) },
                                    onClearAll = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
