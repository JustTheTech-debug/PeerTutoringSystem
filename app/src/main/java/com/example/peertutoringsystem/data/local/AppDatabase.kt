package com.example.peertutoringsystem.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.peertutoringsystem.data.model.Session
import com.example.peertutoringsystem.data.model.TutorOffer
import com.example.peertutoringsystem.data.model.TutoringRequest
import com.example.peertutoringsystem.data.model.User

@Database(
    entities = [User::class, TutoringRequest::class, TutorOffer::class, Session::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Each member adds ONE line here for their own DAO, for example:
    abstract fun requestDao(): RequestDao



    abstract fun userDao(): UserDao
    abstract fun offerDao(): OfferDao
    abstract fun sessionDao(): SessionDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Only ONE database object for the whole app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "peer_tutor_database"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}