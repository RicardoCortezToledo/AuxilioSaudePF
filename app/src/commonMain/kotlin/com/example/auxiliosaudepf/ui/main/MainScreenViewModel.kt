package com.example.auxiliosaudepf.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auxiliosaudepf.data.ReceiptRepository
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(private val receiptRepository: ReceiptRepository) : ViewModel() {

    private val _categoriesState = MutableStateFlow<List<ReceiptCategory>>(emptyList())
    val categoriesState: StateFlow<List<ReceiptCategory>> = _categoriesState.asStateFlow()

    private val _receiptsState = MutableStateFlow<List<Receipt>>(emptyList())
    val receiptsState: StateFlow<List<Receipt>> = _receiptsState.asStateFlow()

    init {
        loadCategories()
        observeReceipts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = receiptRepository.getCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveReceipt(imagePath: String, ocrText: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val receipt = receiptRepository.addReceipt(imagePath, ocrText)
                val category = receiptRepository.getCategories().find { it.id == receipt.categoryId }
                val categoryName = category?.name ?: "Outros"
                loadCategories()
                onComplete(categoryName)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete("Erro ao salvar")
            }
        }
    }

    private fun observeReceipts() {
        viewModelScope.launch {
            receiptRepository.getReceipts().collect {
                _receiptsState.value = it
            }
        }
    }

    fun deleteReceipt(id: Long) {
        viewModelScope.launch {
            receiptRepository.deleteReceipt(id)
        }
    }

    fun deleteAllReceipts() {
        viewModelScope.launch {
            receiptRepository.deleteAllReceipts()
        }
    }
}
