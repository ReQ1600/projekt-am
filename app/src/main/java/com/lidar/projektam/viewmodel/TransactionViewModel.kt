package com.lidar.projektam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lidar.projektam.model.Transaction
import com.lidar.projektam.model.TransactionType
import com.lidar.projektam.repository.TransactionRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random


class TransactionViewModel(private val repository: TransactionRepo) : ViewModel() {
    val transactions = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    //adding transaction to db in the background
    fun addTransaction(amount: Double, type: TransactionType, description: String?){
        viewModelScope.launch {
            repository.insert(Transaction(amount = amount, type = type, description = description))
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun deleteTransactionById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    //calculating account balance
    val balance: StateFlow<Double> = transactions.map { list ->
        list.sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun populateWithDummyData(amount: Int)
    {

        viewModelScope.launch {
            repository.insert(
                Transaction(
                    amount = Random.nextDouble(),
                    type = TransactionType.values().random(),
                    description = "dummy data",
                    date = System.currentTimeMillis() - 1 * 24L * 60 * 60 * 1000
                )
            )
            delay(100)
            repository.insert(
                Transaction(
                    amount = Random.nextDouble(),
                    type = TransactionType.values().random(),
                    description = "dummy data",
                    date = System.currentTimeMillis() - 5 * 24L * 60 * 60 * 1000
                )
            )
            delay(100)
            repository.insert(
                Transaction(
                    amount = Random.nextDouble(),
                    type = TransactionType.values().random(),
                    description = "dummy data",
                    date = System.currentTimeMillis() - 10  * 24L * 60 * 60 * 1000
                )
            )
            delay(100)
            repository.insert(
                Transaction(
                    amount = Random.nextDouble(),
                    type = TransactionType.values().random(),
                    description = "dummy data",
                    date = System.currentTimeMillis() - 30 * 24L * 60 * 60 * 1000
                )
            )
            delay(100)
            repository.insert(
                Transaction(
                    amount = Random.nextDouble(),
                    type = TransactionType.values().random(),
                    description = "dummy data",
                    date = System.currentTimeMillis() - 150 * 24L * 60 * 60 * 1000
                )
            )
            delay(100)
        }

    }
}