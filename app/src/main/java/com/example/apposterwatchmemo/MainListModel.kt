package com.example.apposterwatchmemo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mainlist")
data class MainListModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imgUrl:String,
    val title:String?,
    val content:String?
)

@Dao
interface MainListDao{
    @Query("SELECT * FROM mainlist")
    fun getAll() : List<MainListModel>

    @Insert(onConflict = REPLACE)
    suspend fun insert(mainListModel: MainListModel)

    @Update
    suspend fun update(mainListModel: MainListModel)

    @Delete
    suspend fun delete(mainListModel: MainListModel)
}

@Database(entities = [MainListModel::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase(){

    abstract fun mainListDao():MainListDao

    companion object {
        @Volatile
        private var INSTANCE : MainDatabase? = null
        fun getDatabase(context: Context) : MainDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "mainlist.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }
}
