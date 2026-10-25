package io.github.pandora615.hyperessentials.island.plugins.notifications

/** Bounded pending notifications; cancellation removes entries before the screen wakes. */
class DeferredNotifications<T>(private val limit: Int = 20) {
    private val pending = linkedMapOf<String, T>()
    fun put(key: String, value: T) {
        pending.remove(key)
        pending[key] = value
        while (pending.size > limit) pending.remove(pending.keys.first())
    }
    fun remove(key: String) { pending.remove(key) }
    fun clear() { pending.clear() }
    fun drain(): List<T> = pending.values.toList().also { pending.clear() }
}
