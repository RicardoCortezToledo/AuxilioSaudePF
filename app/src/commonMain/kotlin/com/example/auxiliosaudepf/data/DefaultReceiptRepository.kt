package com.example.auxiliosaudepf.data

import com.example.auxiliosaudepf.data.local.Database
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import com.example.auxiliosaudepf.util.PlatformFiles
import com.example.auxiliosaudepf.util.getEpochMillis
import com.example.auxiliosaudepf.util.getCurrentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultReceiptRepository(
    private val db: Database,
    private val fileManager: PlatformFiles
) : ReceiptRepository {

    private val _receiptsFlow = MutableStateFlow<List<Receipt>>(emptyList())

    init {
        refreshReceipts()
    }

    private fun refreshReceipts() {
        _receiptsFlow.value = db.getAllReceipts()
    }

    override fun getCategories(): List<ReceiptCategory> {
        return db.getAllCategories()
    }

    override fun getReceipts(): Flow<List<Receipt>> {
        return _receiptsFlow.asStateFlow()
    }

    override fun addReceipt(imagePath: String, ocrText: String, amount: Double): Receipt {
        val category = classifyOcrText(ocrText)
        val timestamp = extractDateFromText(ocrText)
        val receipt = Receipt(
            imagePath = imagePath,
            ocrText = ocrText,
            categoryId = category.id,
            timestamp = timestamp,
            amount = amount
        )
        val id = db.insertReceipt(receipt)
        val savedReceipt = receipt.copy(id = id)
        refreshReceipts()
        return savedReceipt
    }

    private fun extractDateFromText(text: String): Long {
        val currentYear = 2026 // Fallback reference year
        
        val dateRegex = """\b(\d{2})/(\d{2})/(\d{4})\b""".toRegex()
        val match = dateRegex.find(text)
        if (match != null) {
            try {
                val day = match.groupValues[1].toInt()
                val month = match.groupValues[2].toInt()
                var year = match.groupValues[3].toInt()
                if (year < currentYear - 2 || year > currentYear + 1) {
                    year = currentYear
                }
                return getEpochMillis(day, month, year)
            } catch (e: Exception) {
                // Ignore and proceed to next check
            }
        }

        val dateRegexShort = """\b(\d{2})/(\d{2})/(\d{2})\b""".toRegex()
        val matchShort = dateRegexShort.find(text)
        if (matchShort != null) {
            try {
                val day = matchShort.groupValues[1].toInt()
                val month = matchShort.groupValues[2].toInt()
                var year = matchShort.groupValues[3].toInt() + 2000
                if (year < currentYear - 2 || year > currentYear + 1) {
                    year = currentYear
                }
                return getEpochMillis(day, month, year)
            } catch (e: Exception) {
                // Ignore
            }
        }

        return getCurrentTimeMillis() // Fallback to current time
    }

    override fun classifyOcrText(ocrText: String): ReceiptCategory {
        val cleanText = ocrText.lowercase()
        val categories = getCategories()

        // 1. PF Saúde (highest priority match)
        val pfSaude = categories.find { it.name == "PF Saúde" }
        if (pfSaude != null && pfSaude.keywords.any { cleanText.contains(it) }) {
            return pfSaude
        }

        // 2. Check other main categories (excluding PF Saúde and Outros)
        for (category in categories) {
            if (category.name == "PF Saúde" || category.name == "Outros") continue
            if (category.keywords.any { cleanText.contains(it) }) {
                return category
            }
        }

        // 3. Fallback: If no keywords match, fall back to "Outros"
        val outros = categories.find { it.name == "Outros" }
        if (outros != null) {
            return outros
        }

        return categories.firstOrNull() ?: ReceiptCategory(7, "Outros", "Despesas gerais", emptyList())
    }

    override fun deleteReceipt(id: Long) {
        val list = db.getAllReceipts()
        val receipt = list.find { it.id == id }
        if (receipt != null) {
            db.deleteReceipt(id)
            try {
                fileManager.deleteFile(receipt.imagePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            refreshReceipts()
        }
    }

    override fun deleteAllReceipts() {
        val list = db.getAllReceipts()
        db.deleteAllReceipts()
        for (receipt in list) {
            try {
                fileManager.deleteFile(receipt.imagePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        refreshReceipts()
    }
}
