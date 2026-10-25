package io.github.pandora615.hyperessentials.island
import io.github.pandora615.hyperessentials.island.plugins.notifications.DeferredNotifications
import org.junit.Assert.*
import org.junit.Test
class DeferredNotificationsTest {
    @Test fun deliversNotificationsReceivedBeforeWakeOnce() {
        val queue = DeferredNotifications<String>()
        queue.put("first", "message 1"); queue.put("second", "message 2")
        assertEquals(listOf("message 1", "message 2"), queue.drain())
        assertTrue(queue.drain().isEmpty())
    }
    @Test fun cancelledAndUpdatedNotificationsAreNotReplayedStale() {
        val queue = DeferredNotifications<String>()
        queue.put("first", "old"); queue.put("first", "new")
        queue.put("cancelled", "private"); queue.remove("cancelled")
        assertEquals(listOf("new"), queue.drain())
    }
    @Test fun screenOffQueueIsBoundedAndClearedOnStop() {
        val queue = DeferredNotifications<Int>(2)
        (1..5).forEach { queue.put(it.toString(), it) }
        assertEquals(listOf(4, 5), queue.drain())
        queue.put("key", 1); queue.clear()
        assertTrue(queue.drain().isEmpty())
    }
}
