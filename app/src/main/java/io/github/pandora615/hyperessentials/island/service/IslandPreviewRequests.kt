package io.github.pandora615.hyperessentials.island.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object IslandPreviewRequests {
    private val _queue = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val queue: SharedFlow<Unit> = _queue

    fun showQueue() {
        _queue.tryEmit(Unit)
    }
}
