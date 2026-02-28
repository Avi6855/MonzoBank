package com.avinashpatil.app.monzobank.domain.repository

data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val osVersion: String,
    val appVersion: String,
    val manufacturer: String,
    val model: String
)

interface DeviceRepository {
    suspend fun getDeviceInfo(): Result<DeviceInfo>
    suspend fun registerDevice(deviceInfo: DeviceInfo): Result<Unit>
    suspend fun updateDeviceInfo(deviceInfo: DeviceInfo): Result<Unit>
}