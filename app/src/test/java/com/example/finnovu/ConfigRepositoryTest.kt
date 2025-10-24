package com.example.finnovu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.finnovu.data.AppDatabase
import com.example.finnovu.data.ConfigDao
import com.example.finnovu.data.PaymentType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConfigRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ConfigDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
        dao = db.configDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndReadPaymentType() = runBlocking {
        val id = dao.insertPaymentType(PaymentType(name = "Card"))
        val list = dao.getPaymentTypes().first()
        assertEquals(1, list.size)
        assertEquals("Card", list[0].name)
    }
}