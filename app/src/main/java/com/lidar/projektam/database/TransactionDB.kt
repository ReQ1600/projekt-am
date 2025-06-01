package com.lidar.projektam.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lidar.projektam.model.Transaction
import com.lidar.projektam.dao.TransactionDao

@Database(entities = [Transaction::class], version = 1)
abstract class TransactionRoomDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        //singleton
        @Volatile
        private var INSTANCE: TransactionRoomDatabase? = null;

        //creates instance if not created yet
        fun getDB(context: Context): TransactionRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionRoomDatabase::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
