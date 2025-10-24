package com.example.finnovu.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    // Payment Types
    @Query("SELECT * FROM payment_types ORDER BY id DESC")
    fun getPaymentTypes(): Flow<List<PaymentType>>

    @Insert
    suspend fun insertPaymentType(pt: PaymentType): Long

    @Update
    suspend fun updatePaymentType(pt: PaymentType)

    @Delete
    suspend fun deletePaymentType(pt: PaymentType)

    // Expense Categories
    @Query("SELECT * FROM expense_categories ORDER BY id DESC")
    fun getExpenseCategories(): Flow<List<ExpenseCategory>>

    @Insert
    suspend fun insertExpenseCategory(cat: ExpenseCategory): Long

    @Update
    suspend fun updateExpenseCategory(cat: ExpenseCategory)

    @Delete
    suspend fun deleteExpenseCategory(cat: ExpenseCategory)
}