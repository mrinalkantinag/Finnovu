package com.example.finnovu.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ConfigRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.configDao()

    fun paymentTypes(): Flow<List<PaymentType>> = dao.getPaymentTypes()
    suspend fun addPaymentType(name: String) = dao.insertPaymentType(PaymentType(name = name))
    suspend fun updatePaymentType(pt: PaymentType) = dao.updatePaymentType(pt)
    suspend fun deletePaymentType(pt: PaymentType) = dao.deletePaymentType(pt)

    fun expenseCategories(): Flow<List<ExpenseCategory>> = dao.getExpenseCategories()
    suspend fun addExpenseCategory(name: String) = dao.insertExpenseCategory(ExpenseCategory(name = name))
    suspend fun updateExpenseCategory(cat: ExpenseCategory) = dao.updateExpenseCategory(cat)
    suspend fun deleteExpenseCategory(cat: ExpenseCategory) = dao.deleteExpenseCategory(cat)
}