package com.example.finnovu

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private const val DATASTORE_NAME = "finnovu_settings_prefs"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object SettingsDataStore {
    private val PAYMENT_TYPES_KEY = stringPreferencesKey("payment_types_json")
    private val EXPENSE_CATEGORIES_KEY = stringPreferencesKey("expense_categories_json")

    private fun listToJson(list: List<String>): String = JSONArray(list).toString()

    private fun jsonToList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            val out = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                out.add(arr.optString(i))
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun paymentTypesFlow(context: Context, default: List<String> = listOf("Cash", "Card")): Flow<List<String>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[PAYMENT_TYPES_KEY]
            val parsed = jsonToList(json)
            if (parsed.isEmpty()) default else parsed
        }

    fun expenseCategoriesFlow(context: Context, default: List<String> = listOf("Food", "Rent", "Transport")): Flow<List<String>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[EXPENSE_CATEGORIES_KEY]
            val parsed = jsonToList(json)
            if (parsed.isEmpty()) default else parsed
        }

    suspend fun savePaymentTypes(context: Context, list: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[PAYMENT_TYPES_KEY] = listToJson(list)
        }
    }

    suspend fun saveExpenseCategories(context: Context, list: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[EXPENSE_CATEGORIES_KEY] = listToJson(list)
        }
    }

    // convenience mutators
    suspend fun addPaymentType(context: Context, value: String) {
        val current = paymentTypesFlow(context).map { it }.let { flow ->
            // collect once
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        savePaymentTypes(context, current + value)
    }

    suspend fun updatePaymentType(context: Context, index: Int, value: String) {
        val current = paymentTypesFlow(context).map { it }.let { flow ->
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        val mutable = current.toMutableList()
        if (index in mutable.indices) {
            mutable[index] = value
            savePaymentTypes(context, mutable)
        }
    }

    suspend fun removePaymentType(context: Context, index: Int) {
        val current = paymentTypesFlow(context).map { it }.let { flow ->
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        val mutable = current.toMutableList()
        if (index in mutable.indices) {
            mutable.removeAt(index)
            savePaymentTypes(context, mutable)
        }
    }

    suspend fun addExpenseCategory(context: Context, value: String) {
        val current = expenseCategoriesFlow(context).map { it }.let { flow ->
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        saveExpenseCategories(context, current + value)
    }

    suspend fun updateExpenseCategory(context: Context, index: Int, value: String) {
        val current = expenseCategoriesFlow(context).map { it }.let { flow ->
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        val mutable = current.toMutableList()
        if (index in mutable.indices) {
            mutable[index] = value
            saveExpenseCategories(context, mutable)
        }
    }

    suspend fun removeExpenseCategory(context: Context, index: Int) {
        val current = expenseCategoriesFlow(context).map { it }.let { flow ->
            kotlin.runCatching {
                kotlinx.coroutines.flow.first(flow)
            }.getOrNull() ?: emptyList()
        }
        val mutable = current.toMutableList()
        if (index in mutable.indices) {
            mutable.removeAt(index)
            saveExpenseCategories(context, mutable)
        }
    }
}
