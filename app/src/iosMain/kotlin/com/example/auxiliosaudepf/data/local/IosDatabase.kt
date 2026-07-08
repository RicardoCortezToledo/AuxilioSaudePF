package com.example.auxiliosaudepf.data.local

import com.example.auxiliosaudepf.data.model.Receipt
import com.example.auxiliosaudepf.data.model.ReceiptCategory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.Foundation.*

@Serializable
data class JsonDb(
    val receipts: List<Receipt> = emptyList(),
    val categories: List<ReceiptCategory> = emptyList()
)

class IosDatabase : Database {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val dbPath: String

    init {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val destDir = (paths.firstOrNull() as? String) ?: ""
        dbPath = "$destDir/auxilio_saude_pf_db.json"
        
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(dbPath)) {
            seedCategories()
        }
    }

    private fun readDb(): JsonDb {
        return try {
            val nsData = NSData.dataWithContentsOfFile(dbPath) ?: return JsonDb()
            val str = NSString.alloc().initWithData(nsData, NSUTF8StringEncoding) as? String ?: ""
            json.decodeFromString(JsonDb.serializer(), str)
        } catch (e: Exception) {
            e.printStackTrace()
            JsonDb()
        }
    }

    private fun writeDb(db: JsonDb) {
        try {
            val str = json.encodeToString(JsonDb.serializer(), db)
            val nsData = NSString.stringWithString(str).dataUsingEncoding(NSUTF8StringEncoding)
            nsData?.writeToFile(dbPath, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun seedCategories() {
        val initialCategories = listOf(
            ReceiptCategory(
                id = 1,
                name = "Farmácia/Drogaria",
                description = "Gastos com medicamentos, remédios e insumos em farmácias ou drogarias.",
                keywords = listOf("farmacia", "drogaria", "medicamento", "remedio", "remédio", "droga", "farmácia")
            ),
            ReceiptCategory(
                id = 2,
                name = "PF Saúde",
                description = "Mensalidades e co-participações do plano de saúde PF Saúde.",
                keywords = listOf("pf saude", "pf saúde", "policia federal saude", "polícia federal saúde", "assist saude", "assist saúde", "programa de assist", "apoio ao prog")
            ),
            ReceiptCategory(
                id = 3,
                name = "GymPass/TotalPass",
                description = "Planos de benefício de atividade física corporativos.",
                keywords = listOf("gympass", "totalpass", "total pass", "gym pass")
            ),
            ReceiptCategory(
                id = 4,
                name = "Dentista",
                description = "Tratamentos odontológicos, consultas e procedimentos dentários.",
                keywords = listOf("dentista", "odontologia", "ortodontia", "dente", "odontológico", "odontólogo", "odontologista")
            ),
            ReceiptCategory(
                id = 5,
                name = "Despesas Médicas",
                description = "Consultas e exames com médicos de diversas especialidades.",
                keywords = listOf(
                    "despesas médicas", "despesa medica", "despesa médica", "despesas medicas", "medico", "médico", "consulta medica", "consulta médica",
                    "otorrino", "proctologista", "dermatologista", "reumatologista", "otorrinolaringologista", "oftalmologista",
                    "cardiologista", "pediatra", "ginecologista", "ortopedista", "urologista", "psiquiatra", "endocrinologista", "neurologista", "oncologista"
                )
            ),
            ReceiptCategory(
                id = 6,
                name = "Academias",
                description = "Mensalidades de academias de ginástica, natação, tenis, etc.",
                keywords = listOf(
                    "academia", "natacao", "natação", "tenis", "tênis", "ginastica", "ginástica", "pilates", "crossfit", "musculacao", "musculação",
                    "jiu-jitsu", "jiu jitsu", "judo", "judô", "karate", "karatê", "muay-tai", "muay thai", "boxe", "boxer", "luta", "combate", "atletismo"
                )
            ),
            ReceiptCategory(
                id = 7,
                name = "Outros",
                description = "Outros comprovantes e despesas gerais que não se enquadram nas categorias acima.",
                keywords = emptyList()
            )
        )
        writeDb(JsonDb(categories = initialCategories))
    }

    override fun getAllCategories(): List<ReceiptCategory> = readDb().categories

    override fun insertReceipt(receipt: Receipt): Long {
        val db = readDb()
        val nextId = (db.receipts.maxOfOrNull { it.id } ?: 0L) + 1L
        val newReceipt = receipt.copy(id = nextId)
        writeDb(db.copy(receipts = db.receipts + newReceipt))
        return nextId
    }

    override fun getAllReceipts(): List<Receipt> = readDb().receipts.sortedByDescending { it.timestamp }

    override fun deleteReceipt(id: Long): Int {
        val db = readDb()
        val originalSize = db.receipts.size
        val filtered = db.receipts.filter { it.id != id }
        writeDb(db.copy(receipts = filtered))
        return originalSize - filtered.size
    }

    override fun deleteAllReceipts(): Int {
        val db = readDb()
        val count = db.receipts.size
        writeDb(db.copy(receipts = emptyList()))
        return count
    }
}
