package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.reactnativecmp.arguments.putAny

data class RNSPGDPRConsent  (
  val uuid: String?,
  val applies: Boolean,
  val createdDate: String?,
  val expirationDate: String?,
  val euconsent: String?,
  val vendorGrants: Map<String, GDPRPurposeGrants>,
  val statuses: Statuses,
  val tcfData: Map<String, Any?>
): RNMappable {
  data class Statuses(val consentedAll: Boolean?, val consentedAny: Boolean?, val rejectedAny: Boolean?): RNMappable {
    constructor(status: ConsentStatus?): this(
      consentedAll = status?.consentedAll,
      consentedAny = status?.consentedToAny, // TODO: verify if this should be `consentedAny` instead
      rejectedAny = status?.rejectedAny
    )

    override fun toRN(): ReadableMap = createMap().apply {
      consentedAll?.let { putBoolean("consentedAll", it) }
      consentedAny?.let { putBoolean("consentedAny", it) }
      rejectedAny?.let { putBoolean("rejectedAny", it) }
    }
  }

  constructor(gdpr: GDPRConsent) : this(
    uuid = gdpr.uuid,
    applies = gdpr.applies,
    createdDate = null,
    expirationDate = null,
    euconsent = gdpr.euconsent,
    vendorGrants = gdpr.grants,
    statuses = Statuses(status = gdpr.consentStatus),
    tcfData = gdpr.tcData
  )

  override fun toRN(): ReadableMap = createMap().apply {
    putString("uuid", uuid)
    putBoolean("applies", applies)
    putString("createdDate", createdDate)
    putString("expirationDate", expirationDate)
    putString("euconsent", euconsent)
    putMap("vendorGrants", vendorGrants.toRN())
    putMap("statuses", statuses.toRN())
    putAny("tcfData", tcfData)
  }
}

fun Map<String, GDPRPurposeGrants>.toRN(): ReadableMap = createMap().apply {
  keys.forEach { vendorId ->
    this@toRN[vendorId]?.let { putMap(vendorId, it.toRN()) }
  }
}

fun GDPRPurposeGrants.toRN(): ReadableMap = createMap().apply {
  putBoolean("granted", granted)
  putMap("purposes", createMap().apply {
    purposeGrants.keys.forEach { purposeId ->
      purposeGrants[purposeId]?.let { putBoolean(purposeId, it) }
    }
  })
}
