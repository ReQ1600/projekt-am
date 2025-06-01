package com.lidar.projektam.repository

import com.lidar.projektam.model.Transaction
import com.lidar.projektam.dao.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepo(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    suspend fun insert(transaction: Transaction){
        dao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction){
        dao.delete(transaction)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}