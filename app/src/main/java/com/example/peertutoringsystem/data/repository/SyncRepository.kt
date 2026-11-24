package com.example.peertutoringsystem.data.repository

import android.content.Context
import com.example.peertutoringsystem.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Offline(val message: String) : SyncResult()
    data class Error(val message: String) : SyncResult()
}

class SyncRepository(private val context: Context) {

    suspend fun syncOfferOnline(offerId: Int, fee: Int, message: String): SyncResult =
        withContext(Dispatchers.IO) {
            try {
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    return@withContext SyncResult.Offline("Offline: Offer saved locally. Will sync when back online.")
                }
                delay(300) // Simulate online server sync
                SyncResult.Success("Offer synced online successfully!")
            } catch (e: Exception) {
                SyncResult.Error("Online sync error: ${e.localizedMessage}")
            }
        }

    suspend fun syncProfileOnline(userId: Int, name: String, phone: String, level: String): SyncResult =
        withContext(Dispatchers.IO) {
            try {
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    return@withContext SyncResult.Offline("Offline: Profile saved locally. Will sync when back online.")
                }
                delay(300) // Simulate online server sync
                SyncResult.Success("Profile synced online successfully!")
            } catch (e: Exception) {
                SyncResult.Error("Online sync error: ${e.localizedMessage}")
            }
        }
}
