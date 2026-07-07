package com.example.auxiliosaudepf.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "auxilio_saude_pf.db"
        private const val DATABASE_VERSION = 3

        // Tables
        private const val TABLE_CATEGORIES = "categories"
        private const val TABLE_RECEIPTS = "receipts"

        // Common column names
        private const val KEY_ID = "id"

        // Categories Table Columns
        private const val KEY_CAT_NAME = "name"
        private const val KEY_CAT_DESC = "description"
        private const val KEY_CAT_KEYWORDS = "keywords"

        // Receipts Table Columns
        private const val KEY_REC_IMAGE_PATH = "image_path"
        private const val KEY_REC_OCR_TEXT = "ocr_text"
        private const val KEY_REC_CATEGORY_ID = "category_id"
        private const val KEY_REC_TIMESTAMP = "timestamp"
        private const val KEY_REC_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_CAT_NAME TEXT UNIQUE,
                $KEY_CAT_DESC TEXT,
                $KEY_CAT_KEYWORDS TEXT
            )
        """.trimIndent()

        val createReceiptsTable = """
            CREATE TABLE $TABLE_RECEIPTS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_REC_IMAGE_PATH TEXT,
                $KEY_REC_OCR_TEXT TEXT,
                $KEY_REC_CATEGORY_ID INTEGER,
                $KEY_REC_TIMESTAMP INTEGER,
                $KEY_REC_AMOUNT REAL,
                FOREIGN KEY($KEY_REC_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($KEY_ID)
            )
        """.trimIndent()

        db.execSQL(createCategoriesTable)
        db.execSQL(createReceiptsTable)

        // Seed initial categories
        seedCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECEIPTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    private fun seedCategories(db: SQLiteDatabase) {
        val initialCategories = listOf(
            ReceiptCategory(
                name = "Farmácia/Drogaria",
                description = "Gastos com medicamentos, remédios e insumos em farmácias ou drogarias.",
                keywords = listOf("farmacia", "drogaria", "medicamento", "remedio", "remédio", "droga", "farmácia")
            ),
            ReceiptCategory(
                name = "PF Saúde",
                description = "Mensalidades e co-participações do plano de saúde PF Saúde.",
                keywords = listOf("pf saude", "pf saúde", "policia federal saude", "polícia federal saúde", "assist saude", "assist saúde", "programa de assist", "apoio ao prog")
            ),
            ReceiptCategory(
                name = "GymPass/TotalPass",
                description = "Planos de benefício de atividade física corporativos.",
                keywords = listOf("gympass", "totalpass", "total pass", "gym pass")
            ),
            ReceiptCategory(
                name = "Dentista",
                description = "Tratamentos odontológicos, consultas e procedimentos dentários.",
                keywords = listOf("dentista", "odontologia", "ortodontia", "dente", "odontológico", "odontólogo", "odontologista")
            ),
            ReceiptCategory(
                name = "Despesas Médicas",
                description = "Consultas e exames com médicos de diversas especialidades (otorrino, dermatologista, reumatologista, etc.).",
                keywords = listOf(
                    "despesas médicas", "despesa medica", "despesa médica", "despesas medicas", "medico", "médico", "consulta medica", "consulta médica",
                    "otorrino", "proctologista", "dermatologista", "reumatologista", "otorrinolaringologista", "oftalmologista",
                    "cardiologista", "pediatra", "ginecologista", "ortopedista", "urologista", "psiquiatra", "endocrinologista", "neurologista", "oncologista"
                )
            ),
            ReceiptCategory(
                name = "Academias",
                description = "Mensalidades de academias de ginástica, natação, tênis, etc.",
                keywords = listOf(
                    "academia", "natacao", "natação", "tenis", "tênis", "ginastica", "ginástica", "pilates", "crossfit", "musculacao", "musculação",
                    "jiu-jitsu", "jiu jitsu", "judo", "judô", "karate", "karatê", "muay-tai", "muay thai", "boxe", "boxer", "luta", "combate", "atletismo",
                    "jiu", "jitsu", "judo", "judô", "muay", "boxe", "academ", "natac"
                )
            ),
            ReceiptCategory(
                name = "Outros",
                description = "Outros comprovantes e despesas gerais que não se enquadram nas categorias acima.",
                keywords = emptyList()
            )
        )

        for (category in initialCategories) {
            val values = ContentValues().apply {
                put(KEY_CAT_NAME, category.name)
                put(KEY_CAT_DESC, category.description)
                put(KEY_CAT_KEYWORDS, category.keywords.joinToString(","))
            }
            db.insert(TABLE_CATEGORIES, null, values)
        }
    }

    // Category Queries
    fun getAllCategories(): List<ReceiptCategory> {
        val list = mutableListOf<ReceiptCategory>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(KEY_ID)
            val nameIndex = cursor.getColumnIndex(KEY_CAT_NAME)
            val descIndex = cursor.getColumnIndex(KEY_CAT_DESC)
            val keywordsIndex = cursor.getColumnIndex(KEY_CAT_KEYWORDS)

            do {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val desc = cursor.getString(descIndex)
                val keywordsStr = cursor.getString(keywordsIndex)
                val keywords = if (keywordsStr.isNotEmpty()) keywordsStr.split(",") else emptyList()

                list.add(ReceiptCategory(id, name, desc, keywords))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Receipt Queries
    fun insertReceipt(receipt: Receipt): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_REC_IMAGE_PATH, receipt.imagePath)
            put(KEY_REC_OCR_TEXT, receipt.ocrText)
            put(KEY_REC_CATEGORY_ID, receipt.categoryId)
            put(KEY_REC_TIMESTAMP, receipt.timestamp)
            put(KEY_REC_AMOUNT, receipt.amount)
        }
        return db.insert(TABLE_RECEIPTS, null, values)
    }

    fun getAllReceipts(): List<Receipt> {
        val list = mutableListOf<Receipt>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_RECEIPTS ORDER BY $KEY_REC_TIMESTAMP DESC", null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(KEY_ID)
            val pathIndex = cursor.getColumnIndex(KEY_REC_IMAGE_PATH)
            val ocrIndex = cursor.getColumnIndex(KEY_REC_OCR_TEXT)
            val catIndex = cursor.getColumnIndex(KEY_REC_CATEGORY_ID)
            val timeIndex = cursor.getColumnIndex(KEY_REC_TIMESTAMP)
            val amountIndex = cursor.getColumnIndex(KEY_REC_AMOUNT)

            do {
                list.add(
                    Receipt(
                        id = cursor.getLong(idIndex),
                        imagePath = cursor.getString(pathIndex),
                        ocrText = cursor.getString(ocrIndex),
                        categoryId = cursor.getLong(catIndex),
                        timestamp = cursor.getLong(timeIndex),
                        amount = cursor.getDouble(amountIndex)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteReceipt(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RECEIPTS, "$KEY_ID = ?", arrayOf(id.toString()))
    }

    fun deleteAllReceipts(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RECEIPTS, null, null)
    }
}
