package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.exposed.SPConsents

interface RNMappable {
  fun toRN(): ReadableMap
}

data class RNSPUserData(
  val gdpr: RNSPGDPRConsent?,
  val usnat: RNSPUSNatConsent?,
  val preferences: RNSPPreferencesConsent?,
  val globalCMP: RNSPGlobalCMPConsent?
): RNMappable {
  constructor(spData: SPConsents): this(
    gdpr = spData.gdpr?.let { RNSPGDPRConsent(gdpr = it.consent)},
    usnat = spData.usNat?.let { RNSPUSNatConsent(usnat = it.consent)},
    preferences = spData.preferences?.let { RNSPPreferencesConsent(preferences = it.consent) },
    globalCMP = spData.globalcmp?.let { RNSPGlobalCMPConsent(globalCMP = it.consent) }
  )

  override fun toRN(): ReadableMap = createMap().apply {
    gdpr?.let { putMap("gdpr", it.toRN()) }
    usnat?.let { putMap("usnat", it.toRN()) }
    preferences?.let { putMap("preferences", it.toRN()) }
    globalCMP?.let { putMap("globalcmp", it.toRN()) }
  }
}
