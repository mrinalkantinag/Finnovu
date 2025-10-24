package com.example.finnovu.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_types")
data class PaymentType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)