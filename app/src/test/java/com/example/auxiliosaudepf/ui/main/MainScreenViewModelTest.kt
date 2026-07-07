package com.example.auxiliosaudepf.ui.main

import com.example.auxiliosaudepf.data.ReceiptRepository
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
    @Test
    fun categoriesState_initiallyLoadsCategories() = runTest {
        val viewModel = MainScreenViewModel(FakeReceiptRepository())
        // Give VM scope time to run if needed, but since it is direct flow first:
        val categories = viewModel.categoriesState.first()
        assertEquals(1, categories.size)
        assertEquals("Test Category", categories[0].name)
    }
}

private class FakeReceiptRepository : ReceiptRepository {
    override fun getCategories(): List<ReceiptCategory> {
        return listOf(ReceiptCategory(1, "Test Category", "Desc", listOf("test")))
    }
    override fun getReceipts(): Flow<List<Receipt>> {
        return flow { emit(emptyList()) }
    }
    override fun addReceipt(imagePath: String, ocrText: String, amount: Double): Receipt {
        return Receipt(1, imagePath, ocrText, 1, System.currentTimeMillis(), amount)
    }
    override fun classifyOcrText(ocrText: String): ReceiptCategory {
        return getCategories().first()
    }
}
