package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createArray
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.exposed.PreferencesConsent

data class RNSPPreferencesConsent(
  val dateCreated: String?,
  val uuid: String?,
  val status: List<Status>,
  val rejectedStatus: List<Status>
): RNMappable {
  data class Status(
    val categoryId: Int,
    val channels: List<Channel>,
    val changed: Boolean?,
    val dateConsented: String?,
    val subType: String?
  ): RNMappable {
    data class Channel(val id: Int, val status: Boolean): RNMappable {
      override fun toRN(): ReadableMap = createMap().apply {
        putInt("id", id)
        putBoolean("status", status)
      }
    }

    override fun toRN(): ReadableMap = createMap().apply {
      putInt("categoryId", categoryId)
      putArray("channels", createArray().apply { channels.forEach { pushMap(it.toRN()) } })
      changed?.let { putBoolean("changed", it) }
      putString("dateConsented", dateConsented)
      putString("subType", subType)
    }
  }

  constructor(preferences: PreferencesConsent) : this(
    dateCreated = preferences.dateCreated?.toString(),
    uuid = preferences.uuid,
    status = preferences.status?.map { status ->
      Status(
        categoryId = status.categoryId,
        channels = status.channels?.map { channel ->
          Status.Channel(id = channel.id, status = channel.status)
        } ?: emptyList(),
        changed = status.changed,
        dateConsented = status.dateConsented?.toString(),
        subType = status.subType?.toString()
      )
    } ?: emptyList(),
    rejectedStatus = preferences.rejectedStatus?.map { status ->
      Status(
        categoryId = status.categoryId,
        channels = status.channels?.map { channel ->
          Status.Channel(id = channel.id, status = channel.status)
        } ?: emptyList(),
        changed = status.changed,
        dateConsented = status.dateConsented?.toString(),
        subType = status.subType?.toString()
      )
    } ?: emptyList()
  )

  override fun toRN(): ReadableMap = createMap().apply {
    putString("dateCreated", dateCreated)
    putString("uuid", uuid)
    putArray("status", createArray().apply { status.forEach { pushMap(it.toRN()) } })
    putArray("rejectedStatus", createArray().apply { rejectedStatus.forEach { pushMap(it.toRN()) } })
  }
}
