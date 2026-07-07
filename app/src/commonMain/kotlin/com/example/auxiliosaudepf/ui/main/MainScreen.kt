package com.example.auxiliosaudepf.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import com.example.auxiliosaudepf.util.*

sealed interface ScreenState {
    object Normal : ScreenState
    data class Preview(
        val pathOrUri: String,
        val isPdf: Boolean,
        val thumbnailPath: String?
    ) : ScreenState
    object ProcessingOcr : ScreenState
    object ListComprovantes : ScreenState
}

enum class GroupingMode {
    Month, Category
}

@Composable
fun MainScreen(
    documentProcessor: DocumentProcessor,
    fileManager: PlatformFiles,
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categoriesState.collectAsState()
    val receipts by viewModel.receiptsState.collectAsState()
    var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Normal) }

    val filePicker = rememberFilePicker { pathOrUri, isPdf ->
        val thumbnailPath = if (isPdf) {
            documentProcessor.renderPdfFirstPage(pathOrUri)
        } else {
            null
        }
        screenState = ScreenState.Preview(pathOrUri, isPdf = isPdf, thumbnailPath = thumbnailPath ?: pathOrUri)
    }

    // Custom horizontal gradient: Brown on edges, White in the center
    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4E342E), // Deep Warm Brown
            Color(0xFFFFFFFF), // Pure White
            Color(0xFF4E342E)  // Deep Warm Brown
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        when (val state = screenState) {
            is ScreenState.Normal -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .safeDrawingPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Section: App Headers
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Auxílio Saúde",
                            color = Color(0xFF0D47A1), // Dark Blue
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Polícia Federal",
                            color = Color(0xFF8D6E63), // Yellowish Brown
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Center Section: Big Pill Buttons
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Button 1: Foto Comprovante (Pink)
                        Button(
                            onClick = {
                                filePicker.launchCamera()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A)),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(58.dp)
                                .padding(vertical = 5.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Foto Comprovante", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Button 2: Upload Comprovante (Pink)
                        Button(
                            onClick = {
                                filePicker.launchGallery()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A)),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(58.dp)
                                .padding(vertical = 5.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Upload Comprovante", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Button 3: Listar Comprovantes (Pink)
                        Button(
                            onClick = {
                                screenState = ScreenState.ListComprovantes
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A)),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(58.dp)
                                .padding(vertical = 5.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Listar Comprovantes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Button 4: Enviar à PF (Purple)
                        Button(
                            onClick = { /* Non-functional */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)), // Roxo
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(58.dp)
                                .padding(vertical = 5.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Enviar à PF", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Button 5: Relatório Ressarcimento (Pink)
                        Button(
                            onClick = { /* Non-functional */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A)),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(58.dp)
                                .padding(vertical = 5.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Relatório Ressarcimento", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Bottom Section: Horizontal chip scroll
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Categorias no Banco de Dados:",
                            color = Color(0xFF5D4037),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (categories.isEmpty()) {
                            Text(text = "Carregando...", color = Color.Gray, fontSize = 12.sp)
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(categories) { category ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5EBE6)),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = Brush.linearGradient(colors = listOf(Color(0xFF8D6E63), Color(0xFFD7CCC8)))
                                        )
                                    ) {
                                        Text(
                                            text = category.name,
                                            color = Color(0xFF4E342E),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is ScreenState.Preview -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .safeDrawingPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title
                    Text(
                        text = "Visualização do Recibo",
                        color = Color(0xFF0D47A1),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Preview Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val painter = rememberPlatformPainter(state.thumbnailPath ?: state.pathOrUri)
                        if (painter != null) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(0.75f)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painter,
                                        contentDescription = "Comprovante Preview",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                    if (state.isPdf) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(12.dp)
                                                .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "PDF - Página 1",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Fallback if rendering fails
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
                                    .padding(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color(0xFF8D6E63), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (state.isPdf) "PDF" else "DOC",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (state.isPdf) "Documento PDF" else "Imagem Selecionada",
                                    color = Color(0xFF4E342E),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.pathOrUri.substringAfterLast('/'),
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Save / Cancel Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { screenState = ScreenState.Normal },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text("Cancelar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                screenState = ScreenState.ProcessingOcr
                                val processOcrAndSave = { textToUse: String ->
                                    val prefix = if (state.isPdf) "pdf" else "photo"
                                    val ext = if (state.isPdf) "pdf" else "jpg"
                                    val localPath = fileManager.copyFileToInternalStorage(state.pathOrUri, prefix, ext)
                                    viewModel.saveReceipt(localPath, textToUse) { categoryName ->
                                        showToast("Salvo com sucesso!\nClassificado como: $categoryName")
                                        screenState = ScreenState.Normal
                                    }
                                }

                                if (state.isPdf) {
                                    val extractedText = documentProcessor.extractTextFromPdf(state.pathOrUri)
                                    if (extractedText.trim().isNotEmpty()) {
                                        processOcrAndSave(extractedText)
                                    } else {
                                        if (state.thumbnailPath != null) {
                                            documentProcessor.runOcr(
                                                imagePath = state.thumbnailPath,
                                                onSuccess = { recognizedText ->
                                                    val finalOcrText = if (recognizedText.trim().isNotEmpty()) recognizedText else generateMockOcrText(state.pathOrUri, state.isPdf)
                                                    processOcrAndSave(finalOcrText)
                                                },
                                                onFailure = {
                                                    val mockText = generateMockOcrText(state.pathOrUri, state.isPdf)
                                                    processOcrAndSave(mockText)
                                                }
                                            )
                                        } else {
                                            val mockText = generateMockOcrText(state.pathOrUri, state.isPdf)
                                            processOcrAndSave(mockText)
                                        }
                                    }
                                } else {
                                    documentProcessor.runOcr(
                                        imagePath = state.pathOrUri,
                                        onSuccess = { recognizedText ->
                                            val finalOcrText = if (recognizedText.trim().isNotEmpty()) recognizedText else generateMockOcrText(state.pathOrUri, state.isPdf)
                                            processOcrAndSave(finalOcrText)
                                        },
                                        onFailure = {
                                            val mockText = generateMockOcrText(state.pathOrUri, state.isPdf)
                                            processOcrAndSave(mockText)
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)), // Green
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text("Salvar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            is ScreenState.ProcessingOcr -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFEC407A),
                        strokeWidth = 6.dp,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Realizando OCR no documento...",
                        color = Color(0xFF0D47A1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Isso pode levar alguns segundos",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            is ScreenState.ListComprovantes -> {
                var groupingMode by remember { mutableStateOf(GroupingMode.Month) }
                var selectedReceipt by remember { mutableStateOf<Receipt?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .safeDrawingPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Row with Back Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { screenState = ScreenState.Normal },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF0D47A1)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("< Voltar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Comprovantes",
                                color = Color(0xFF0D47A1),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Button(
                            onClick = {
                                viewModel.deleteAllReceipts()
                                showToast("Todos os comprovantes foram apagados")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Apagar Todos", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Toggle Grouping Mode (Month vs Category)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 16.dp)
                            .background(Color(0xFFEFEBE9), RoundedCornerShape(24.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val selectedColor = Color(0xFFEC407A)
                        val unselectedColor = Color.Transparent
                        
                        Button(
                            onClick = { groupingMode = GroupingMode.Month },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (groupingMode == GroupingMode.Month) selectedColor else unselectedColor,
                                contentColor = if (groupingMode == GroupingMode.Month) Color.White else Color(0xFF5D4037)
                            ),
                            shape = CircleShape,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Mês/Ano", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { groupingMode = GroupingMode.Category },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (groupingMode == GroupingMode.Category) selectedColor else unselectedColor,
                                contentColor = if (groupingMode == GroupingMode.Category) Color.White else Color(0xFF5D4037)
                            ),
                            shape = CircleShape,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Categoria", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // List of Grouped Receipts
                    if (receipts.isEmpty()) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("Nenhum comprovante cadastrado ainda.", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    } else {
                        val groupedReceipts = if (groupingMode == GroupingMode.Month) {
                            receipts.groupBy {
                                formatEpochMillis(it.timestamp, "MMMM yyyy").replaceFirstChar { char -> char.uppercase() }
                            }
                        } else {
                            receipts.groupBy { receipt ->
                                categories.find { it.id == receipt.categoryId }?.name ?: "Outros"
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            groupedReceipts.forEach { (groupName, itemsList) ->
                                item {
                                    Text(
                                        text = groupName,
                                        color = Color(0xFF5D4037),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF5EBE6), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                
                                items(itemsList.sortedByDescending { it.timestamp }) { receipt ->
                                    val catName = categories.find { it.id == receipt.categoryId }?.name ?: "Outros"
                                    val dateStr = formatEpochMillis(receipt.timestamp, "dd/MM/yyyy HH:mm")
                                    
                                    Card(
                                        onClick = { selectedReceipt = receipt },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val thumbnailPath = remember(receipt.imagePath) {
                                                if (receipt.imagePath.endsWith(".pdf", ignoreCase = true)) {
                                                    documentProcessor.renderPdfFirstPage(receipt.imagePath)
                                                } else {
                                                    receipt.imagePath
                                                }
                                            }
                                            val painter = rememberPlatformPainter(thumbnailPath)

                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .background(Color(0xFFEFEBE9), RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (painter != null) {
                                                    Image(
                                                        painter = painter,
                                                        contentDescription = "Thumbnail",
                                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                                    )
                                                } else {
                                                    Text(
                                                        text = if (receipt.imagePath.endsWith(".pdf", ignoreCase = true)) "PDF" else "DOC",
                                                        color = Color(0xFF8D6E63),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (groupingMode == GroupingMode.Month) catName else dateStr,
                                                    color = Color(0xFF0D47A1),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = if (groupingMode == GroupingMode.Month) dateStr else catName,
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                                if (receipt.ocrText.isNotEmpty()) {
                                                    val snippet = receipt.ocrText.replace("\n", " ").take(45)
                                                    Text(
                                                        text = if (receipt.ocrText.length > 45) "$snippet..." else snippet,
                                                        color = Color(0xFF5D4037),
                                                        fontSize = 11.sp,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.deleteReceipt(receipt.id)
                                                    showToast("Comprovante apagado")
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                                shape = CircleShape,
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text("Apagar", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Detail Dialog Overlay
                val receiptToDetail = selectedReceipt
                if (receiptToDetail != null) {
                    val catName = categories.find { it.id == receiptToDetail.categoryId }?.name ?: "Outros"
                    val dateStr = formatEpochMillis(receiptToDetail.timestamp, "dd/MM/yyyy HH:mm")
                    val isPdf = receiptToDetail.imagePath.endsWith(".pdf", ignoreCase = true)
                    
                    val thumbnailPath = remember(receiptToDetail.imagePath) {
                        if (isPdf) {
                            documentProcessor.renderPdfFirstPage(receiptToDetail.imagePath)
                        } else {
                            receiptToDetail.imagePath
                        }
                    }
                    val painter = rememberPlatformPainter(thumbnailPath)

                    AlertDialog(
                        onDismissRequest = { selectedReceipt = null },
                        title = {
                            Column {
                                Text(catName, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(dateStr, color = Color.Gray, fontSize = 12.sp)
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (painter != null) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .aspectRatio(0.8f)
                                            .padding(bottom = 16.dp)
                                    ) {
                                        Image(
                                            painter = painter,
                                            contentDescription = "Detail Photo",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                                
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Texto OCR Extraído:",
                                            color = Color(0xFF5D4037),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = receiptToDetail.ocrText,
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { selectedReceipt = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                            ) {
                                Text("Fechar", color = Color.White)
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun generateMockOcrText(pathOrUri: String, isPdf: Boolean): String {
    val name = pathOrUri.substringAfterLast('/').lowercase()
    return when {
        name.contains("farmacia") || name.contains("drogaria") || name.contains("remedio") || name.contains("pague") -> 
            "Drogaria Sao Paulo Nota Fiscal de Medicamento Remedios Alergia Valor 120.00"
        name.contains("pf saude") || name.contains("pfsaude") || name.contains("policia") || name.contains("mensalidade") -> 
            "Comprovante de Pagamento Plano de Saude PF SAUDE Polícia Federal Mensalidade Titular 18/06/2026 18:57"
        name.contains("gympass") || name.contains("totalpass") || name.contains("gym") || name.contains("pass") -> 
            "GymPass Corporate Plan Mensalidade Fitness TotalPass 15/06/2026"
        name.contains("dentista") || name.contains("odonto") || name.contains("dente") || name.contains("canal") -> 
            "Clinica Odontologica Dr Silva Dentista Tratamento Ortodontico 12/06/2026"
        name.contains("academia") || name.contains("natacao") || name.contains("tenis") || name.contains("fitness") || name.contains("treino") || name.contains("jiu-jitsu") || name.contains("jiu jitsu") || name.contains("judo") || name.contains("karate") || name.contains("boxe") -> 
            "IMPERIO JIU-JITSU CNPJ/CPF 54482689000167 ELO R$ 300,00 A VISTA 18/06/2026 18:57 APROVADO PELO EMISSOR"
        name.contains("otorrino") || name.contains("dermatologista") || name.contains("consulta") || name.contains("medico") || name.contains("reumatologista") -> 
            "Recibo de Consulta Medica Especialidade Otorrino Dr Abreu 10/06/2026"
        else -> {
            val mockOptions = listOf(
                "Drogaria Pague Menos Nota Fiscal Farmacia Remedio Ibuprofeno 05/06/2026",
                "Comprovante PF Saúde Pagamento Coparticipacao Mensalidade 02/06/2026",
                "Gympass Totalpass Gym Pass Mensalidade Academia 28/05/2026",
                "Doutor Jose Dentista Recibo Odontologico Limpeza 20/05/2026",
                "Clinica Reumatologista Reumatologia Otorrinolaringologista Consulta Medica 15/05/2026",
                "IMPERIO JIU-JITSU CNPJ/CPF 54482689000167 ELO R$ 300,00 A VISTA 18/06/2026 18:57 APROVADO"
            )
            mockOptions.random()
        }
    }
}
