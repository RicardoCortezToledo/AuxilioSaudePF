package com.example.auxiliosaudepf.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun rememberPlatformPainter(pathOrUri: String?): Painter?
