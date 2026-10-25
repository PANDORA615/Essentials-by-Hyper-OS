/*
 * Copyright (c) 2026 sameerasw.com
 * License: MIT License
 *
 * Feature Module: Background Services & Receivers
 * File: AppUpdateWorker.kt
 * Description: Background service component for AppUpdateWorker.kt.
 */

package io.github.pandora615.hyperessentials.services

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.data.repository.UpdateRepository
import io.github.pandora615.hyperessentials.utils.UpdateNotificationHelper

class AppUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("AppUpdateWorker", "Executing periodic update check")
        val context = applicationContext
        val settingsRepository = SettingsRepository(context)
        val updateRepository = UpdateRepository()

        return try {
            val isAutoUpdateEnabled =
                settingsRepository.getBoolean(
                    SettingsRepository.KEY_AUTO_UPDATE_ENABLED,
                    true,
                )
            if (!isAutoUpdateEnabled) {
                return Result.success()
            }

            val currentVersion =
                try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (e: Exception) {
                    "0.0"
                } ?: "0.0"

            val isPreReleaseEnabled =
                settingsRepository.getBoolean(
                    SettingsRepository.KEY_CHECK_PRE_RELEASES_ENABLED,
                    false,
                )

            val updateInfo =
                updateRepository.checkForUpdates(context, isPreReleaseEnabled, currentVersion)

            if (updateInfo != null && updateInfo.isUpdateAvailable) {
                io.github.pandora615.hyperessentials.viewmodels.MainViewModel.cachedIsUpdateAvailable = true
                io.github.pandora615.hyperessentials.viewmodels.MainViewModel.cachedUpdateInfo = updateInfo

                if (updateInfo.downloadUrl.isNotEmpty()) {
                    val isNotifEnabled =
                        settingsRepository.getBoolean(
                            SettingsRepository.KEY_UPDATE_NOTIFICATION_ENABLED,
                            true,
                        )
                    if (isNotifEnabled) {
                        UpdateNotificationHelper.showUpdateNotification(
                            context,
                            updateInfo.versionName,
                            updateInfo.downloadUrl,
                        )
                    }
                }
            }

            settingsRepository.putLong(
                SettingsRepository.KEY_LAST_UPDATE_CHECK_TIME,
                System.currentTimeMillis(),
            )

            Result.success()
        } catch (e: Exception) {
            Log.e("AppUpdateWorker", "Error during periodic update check", e)
            Result.retry()
        }
    }
}
