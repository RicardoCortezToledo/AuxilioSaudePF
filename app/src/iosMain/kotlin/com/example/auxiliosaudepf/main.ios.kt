package com.example.auxiliosaudepf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.example.auxiliosaudepf.data.DefaultReceiptRepository
import com.example.auxiliosaudepf.data.local.IosDatabase
import com.example.auxiliosaudepf.ui.main.MainScreen
import com.example.auxiliosaudepf.ui.main.MainScreenViewModel
import com.example.auxiliosaudepf.util.IosDocumentProcessor
import com.example.auxiliosaudepf.util.IosPlatformFiles
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val db = remember { IosDatabase() }
    val fileManager = remember { IosPlatformFiles() }
    val docProcessor = remember { IosDocumentProcessor() }
    val repository = remember { DefaultReceiptRepository(db, fileManager) }
    val viewModel = remember { MainScreenViewModel(repository) }

    MainScreen(
        documentProcessor = docProcessor,
        fileManager = fileManager,
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
    )
}
