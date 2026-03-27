package com.pallisahayak.core.model.sync

enum class SyncStatus(val value: String) {
    PENDING("pending"),
    SYNCED("synced"),
    CONFLICT("conflict");

    companion object {
        fun fromValue(value: String): SyncStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}

data class SyncResult(
    val pushed: Int,
    val pulled: Int,
    val conflicts: Int = 0,
    val serverTimestamp: Long = 0L,
)
