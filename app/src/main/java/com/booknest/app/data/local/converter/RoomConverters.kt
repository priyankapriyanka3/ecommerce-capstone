package com.booknest.app.data.local.converter

import androidx.room.TypeConverter
import com.booknest.app.domain.model.Address
import com.booknest.app.domain.model.OrderItem
import com.booknest.app.domain.model.OrderStatus
import com.booknest.app.domain.model.PaymentMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromOrderItemList(value: List<OrderItem>?): String {
        return gson.toJson(value ?: emptyList<OrderItem>())
    }

    @TypeConverter
    fun toOrderItemList(value: String?): List<OrderItem> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<OrderItem>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromAddress(value: Address?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAddress(value: String?): Address? {
        if (value.isNullOrEmpty()) return null
        return gson.fromJson(value, Address::class.java)
    }

    @TypeConverter
    fun fromAddressList(value: List<Address>?): String {
        return gson.toJson(value ?: emptyList<Address>())
    }

    @TypeConverter
    fun toAddressList(value: String?): List<Address> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Address>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? {
        if (value.isNullOrEmpty()) return null
        return gson.fromJson(value, PaymentMethod::class.java)
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus?): String {
        return value?.name ?: OrderStatus.PLACED.name
    }

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus {
        return try {
            if (value.isNullOrEmpty()) OrderStatus.PLACED else OrderStatus.valueOf(value)
        } catch (e: Exception) {
            OrderStatus.PLACED
        }
    }
}
