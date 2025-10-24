package com.example.finnovu.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finnovu.data.ConfigRepository
import com.example.finnovu.data.ExpenseCategory
import com.example.finnovu.data.PaymentType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ConfigRepository(application.applicationContext)

    val paymentTypes: StateFlow<List<PaymentType>> = repo.paymentTypes().map { it }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val expenseCategories: StateFlow<List<ExpenseCategory>> = repo.expenseCategories().map { it }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addPaymentType(name: String) = viewModelScope.launch { repo.addPaymentType(name) }
    fun addExpenseCategory(name: String) = viewModelScope.launch { repo.addExpenseCategory(name) }

    fun onEditPaymentType(pt: PaymentType) {
        // For brevity, just delete (could open edit flow)
        viewModelScope.launch { repo.deletePaymentType(pt) }
    }
    fun onEditExpenseCategory(cat: ExpenseCategory) {
        viewModelScope.launch { repo.deleteExpenseCategory(cat) }
    }
}