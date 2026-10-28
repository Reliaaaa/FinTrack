package id.fintrack.app

import android.app.Application
import androidx.room.Room
import id.fintrack.app.data.AppDatabase
import id.fintrack.app.data.Prefs
import id.fintrack.app.data.Repository

class FinTrackApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "fintrack.db").build()
    }
    val repository: Repository by lazy { Repository(database) }
    val prefs: Prefs by lazy { Prefs(this) }
}
