package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createArray
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.exposed.GlobalCmpConsent

data class RNSPGlobalCMPConsent(
  val uuid: String?,
  val applies: Boolean,
  val createdDate: String?,
  val expirationDate: String?,
  val vendors: List<RNSPConsentable>,
  val categories: List<RNSPConsentable>
): RNMappable {
  constructor(globalCMP: GlobalCmpConsent) : this(
    uuid = globalCMP.uuid,
    applies = globalCMP.applies,
    createdDate = globalCMP.dateCreated,
    expirationDate = null,
    vendors = globalCMP.vendors?.map { RNSPConsentable(it) } ?: emptyList(),
    categories = globalCMP.categories?.map { RNSPConsentable(it) } ?: emptyList()
  )

  override fun toRN(): ReadableMap = createMap().apply {
    putString("uuid", uuid)
    putBoolean("applies", applies)
    putString("createdDate", createdDate)
    putString("expirationDate", expirationDate)
    putArray("vendors", createArray().apply { vendors.forEach { pushMap(it.toRN()) } })
    putArray("categories", createArray().apply { categories.forEach { pushMap(it.toRN()) } })
  }
}
