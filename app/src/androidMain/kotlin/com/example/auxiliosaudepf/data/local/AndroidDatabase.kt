package com.example.auxiliosaudepf.data.local

import android.content.Context
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory

class AndroidDatabase(context: Context) : Database {
    private val dbHelper = DatabaseHelper(context)
    
    override fun getAllCategories(): List<ReceiptCategory> = dbHelper.getAllCategories()
    override fun insertReceipt(receipt: Receipt): Long = dbHelper.insertReceipt(receipt)
    override fun getAllReceipts(): List<Receipt> = dbHelper.getAllReceipts()
    override fun deleteReceipt(id: Long): Int = dbHelper.deleteReceipt(id)
    override fun deleteAllReceipts(): Int = dbHelper.deleteAllReceipts()
}
