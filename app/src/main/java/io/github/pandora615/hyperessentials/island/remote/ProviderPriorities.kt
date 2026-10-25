package io.github.pandora615.hyperessentials.island.remote

import android.content.Context
import io.github.pandora615.hyperessentials.data.repository.SettingsRepository
import io.github.pandora615.hyperessentials.island.model.IslandPriority
import io.github.pandora615.hyperessentials.island.model.IslandPriorityEntries

object ProviderPriorities {
    private const val FALLBACK = 58

    fun entries(context: Context, settings: SettingsRepository): List<IslandPriorityEntries.Entry> {
        val trust = ProviderTrustStore(settings)
        return ProviderDiscovery.query(context)
            .filter { trust.isEnabled(it.packageName) }
            .map { IslandPriorityEntries.providerEntry(it.packageName, (it.priority ?: FALLBACK).coerceIn(IslandPriority.NOTIFICATION + 1, IslandPriority.DEFAULT)) }
    }
}
