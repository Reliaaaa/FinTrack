package id.fintrack.app
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import id.fintrack.app.data.*
import id.fintrack.app.domain.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LedgerTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: Repository
    @Before fun setup() {
        db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),AppDatabase::class.java).build()
        repo=Repository(db)
    }
    @After fun close() { db.close() }
    @Test fun editAcrossAccountsAndDeleteRestoresBalances() = runBlocking {
        repo.addAccount("Bank","BANK",1_000_000,true);repo.addAccount("Wallet","DOMPET",200_000,true)
        val a=db.dao().accountsOnce()
        repo.addTransaction(a[0].id,TxType.EXPENSE,50_000,Category.MAKAN,"Makan")
        assertEquals(950_000L,db.dao().account(a[0].id)!!.balance)
        val t=db.dao().transactionsOnce().single()
        repo.editTransaction(t.id,a[1].id,TxType.INCOME,40_000,Category.GAJI,"Koreksi",t.timestamp)
        assertEquals(1_000_000L,db.dao().account(a[0].id)!!.balance)
        assertEquals(240_000L,db.dao().account(a[1].id)!!.balance)
        repo.deleteTransaction(t.id)
        assertEquals(200_000L,db.dao().account(a[1].id)!!.balance)
    }
    @Test fun transferAndUndoConserveTotalCash() = runBlocking {
        repo.addAccount("A","BANK",500_000,true);repo.addAccount("B","BANK",200_000,true)
        val a=db.dao().accountsOnce();repo.transfer(a[0].id,a[1].id,100_000)
        assertEquals(listOf(400_000L,300_000L),db.dao().accountsOnce().map { it.balance })
        val rows=db.dao().transactionsOnce()
        assertEquals(2,rows.size);assertNotNull(rows[0].transferGroup)
        assertEquals(rows[0].transferGroup,rows[1].transferGroup)
        assertTrue(rows.none { it.type=="INCOME" || it.type=="EXPENSE" })
        repo.deleteTransaction(rows.first().id)
        assertTrue(db.dao().transactionsOnce().isEmpty())
        assertEquals(listOf(500_000L,200_000L),db.dao().accountsOnce().map { it.balance })
    }
    @Test fun invalidTransferCannotPartiallyChangeLedger() = runBlocking {
        repo.addAccount("A","BANK",10,true);repo.addAccount("B","BANK",20,true)
        val a=db.dao().accountsOnce()
        assertTrue(runCatching { repo.transfer(a[0].id,a[1].id,11) }.isFailure)
        assertTrue(runCatching { repo.transfer(a[0].id,999,5) }.isFailure)
        assertEquals(listOf(10L,20L),db.dao().accountsOnce().map { it.balance })
        assertTrue(db.dao().transactionsOnce().isEmpty())
    }
    @Test fun repeatedImportIsIdempotentAndReferencesDistinguishPurchases() = runBlocking {
        repo.addAccount("CSV","BANK",100_000,true);val id=db.dao().accountsOnce().single().id
        val rows=CsvImport.parse("tanggal,jenis,jumlah,kategori,catatan,ref\n2026-01-01,EXPENSE,1000,MAKAN,Kopi,A\n2026-01-01,EXPENSE,1000,MAKAN,Kopi,B")
        assertEquals(2,repo.importRows(id,rows));assertEquals(0,repo.importRows(id,rows))
        assertEquals(98_000L,db.dao().account(id)!!.balance)
        assertEquals(2,db.dao().transactionsOnce().size)
    }
    @Test fun confirmationAppliesBalanceOnlyOnce() = runBlocking {
        repo.addAccount("A","BANK",100_000,true);val id=db.dao().accountsOnce().single().id
        val parsed=ParsedTx(TxType.EXPENSE,20_000,"Kopi","Contoh")
        assertTrue(repo.addDraft(parsed,1L,"unique",Category.MAKAN))
        assertFalse(repo.addDraft(parsed,1L,"unique",Category.MAKAN))
        val draft=db.dao().drafts().first().single()
        assertEquals(100_000L,db.dao().account(id)!!.balance)
        repo.confirmDraft(draft.id,id,Category.MAKAN);repo.confirmDraft(draft.id,id,Category.MAKAN)
        assertEquals(80_000L,db.dao().account(id)!!.balance)
        assertTrue(db.dao().drafts().first().isEmpty())
    }
    @Test fun populatedAccountCannotBeDeletedAndWipeClearsPlanningData() = runBlocking {
        repo.addAccount("A","BANK",100_000,true);val id=db.dao().accountsOnce().single().id
        repo.addTransaction(id,TxType.EXPENSE,1_000,Category.MAKAN,"Kopi")
        assertTrue(runCatching { repo.deleteAccount(id) }.isFailure)
        assertNotNull(db.dao().account(id))
        repo.saveBudget("MAKAN",50_000);repo.saveGoal(GoalEntity(name="Laptop",target=500_000,saved=1_000,dueDate="2027-01-01"))
        repo.wipe()
        assertTrue(db.dao().accountsOnce().isEmpty());assertTrue(db.dao().transactionsOnce().isEmpty())
        assertTrue(repo.budgets.first().isEmpty());assertTrue(repo.goals.first().isEmpty())
    }
}
