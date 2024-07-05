package com.prapti.lifememo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prapti.lifememo.viewmodel.AppViewModelFactory
import com.prapti.lifememo.viewmodel.EmotionViewModel
import com.prapti.lifememo.viewmodel.JournalViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    emotionViewModel: EmotionViewModel = viewModel(factory = AppViewModelFactory.Factory),
    journalViewModel: JournalViewModel = viewModel(factory = AppViewModelFactory.Factory)

) {
    NavHost(navController, startDestination = "main_screen") {
        composable("main_screen") { HomeScreen(navController) }
        composable("emotion_tracker") { EmotionTracker(emotionViewModel) }

        composable("journal_app") { JournalApp(journalViewModel) }
    }
}