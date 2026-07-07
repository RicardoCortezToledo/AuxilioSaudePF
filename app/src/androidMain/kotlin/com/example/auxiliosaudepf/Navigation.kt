package com.example.auxiliosaudepf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.auxiliosaudepf.data.DefaultReceiptRepository
import com.example.auxiliosaudepf.data.local.AndroidDatabase
import com.example.auxiliosaudepf.ui.main.MainScreen
import com.example.auxiliosaudepf.ui.main.MainScreenViewModel
import com.example.auxiliosaudepf.util.AndroidDocumentProcessor
import com.example.auxiliosaudepf.util.AndroidPlatformFiles

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)
  val context = LocalContext.current
  
  val db = remember { AndroidDatabase(context.applicationContext) }
  val fileManager = remember { AndroidPlatformFiles(context.applicationContext) }
  val docProcessor = remember { AndroidDocumentProcessor(context.applicationContext) }
  val repository = remember { DefaultReceiptRepository(db, fileManager) }
  
  val mainViewModel = viewModel {
      MainScreenViewModel(repository)
  }

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
              documentProcessor = docProcessor,
              fileManager = fileManager,
              viewModel = mainViewModel,
              modifier = Modifier.fillMaxSize()
          )
        }
      },
  )
}
