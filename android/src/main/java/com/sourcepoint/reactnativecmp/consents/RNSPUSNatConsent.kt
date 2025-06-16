package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createArray
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentData.ConsentString
import com.sourcepoint.cmplibrary.model.exposed.UsNatConsent
import com.sourcepoint.cmplibrary.model.exposed.UsNatStatuses
import com.sourcepoint.reactnativecmp.arguments.putAny

data class RNSPUSNatConsent(
  val uuid: String?,
  val applies: Boolean,
  val createdDate: String?,
  val expirationDate: String?,
  val consentSections: List<ConsentSection>,
  val statuses: Statuses,
  val gppData:  Map<String, Any?>,
  val vendors: List<RNSPConsentable>,
  val categories: List<RNSPConsentable>
): RNMappable {
  data class ConsentSection(val id: Int?, val name: String?, val consentString: String?) {
    constructor(section: ConsentString): this(
      id = section.sectionId,
      name = section.sectionName,
      consentString = section.consentString
    )

    fun toRN(): ReadableMap = createMap().apply {
      id?.let { putInt("id", it) }
      putString("name", name)
      putString("consentString", consentString)
    }
  }

  data class Statuses(
    val consentedAll: Boolean?,
    val consentedAny: Boolean?,
    val rejectedAny: Boolean?,
    val sellStatus: Boolean?,
    val shareStatus: Boolean?,
    val sensitiveDataStatus: Boolean?,
    val gpcStatus: Boolean?,
  ): RNMappable {
    constructor(status: UsNatStatuses) : this(
      consentedAll = status.consentedToAll,
      consentedAny = status.consentedToAny,
      rejectedAny = status.rejectedAny,
      sellStatus = status.sellStatus,
      shareStatus = status.shareStatus,
      sensitiveDataStatus = status.sensitiveDataStatus,
      gpcStatus = status.gpcStatus,
    )

    override fun toRN(): ReadableMap = createMap().apply {
      consentedAll?.let { putBoolean("consentedAll", it) }
      consentedAny?.let { putBoolean("consentedAny", it) }
      rejectedAny?.let { putBoolean("rejectedAny", it) }
      sellStatus?.let { putBoolean("sellStatus", it) }
      shareStatus?.let { putBoolean("shareStatus", it) }
      sensitiveDataStatus?.let { putBoolean("sensitiveDataStatus", it) }
      gpcStatus?.let { putBoolean("gpcStatus", it) }
    }
  }

  constructor(usnat: UsNatConsent) : this(
    uuid = usnat.uuid,
    applies = usnat.applies,
    createdDate = usnat.dateCreated,
    expirationDate = null,
    consentSections = usnat.consentStrings?.map { ConsentSection(section = it) } ?: emptyList(),
    statuses = Statuses(status = usnat.statuses),
    gppData = usnat.gppData,
    vendors = usnat.vendors?.map { RNSPConsentable(it) } ?: emptyList(),
    categories = usnat.categories?.map { RNSPConsentable(it) } ?: emptyList(),
  )

  override fun toRN(): ReadableMap = createMap().apply {
    putString("uuid", uuid)
    putBoolean("applies", applies)
    putString("createdDate", createdDate)
    putString("expirationDate", expirationDate)
    putArray("consentSections", createArray().apply { consentSections.forEach { pushMap(it.toRN()) } })
    putMap("statuses", statuses.toRN())
    putAny("gppData", gppData)
    putArray("vendors", createArray().apply { vendors.forEach { pushMap(it.toRN()) } })
    putArray("categories", createArray().apply { categories.forEach { pushMap(it.toRN()) } })
  }
}
