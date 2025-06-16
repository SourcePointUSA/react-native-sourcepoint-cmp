package com.sourcepoint.reactnativecmp.consents

import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.exposed.Consentable

data class RNSPConsentable(override val id: String, override val consented: Boolean): Consentable, RNMappable {
  constructor(spConsentable: Consentable): this(
    id = spConsentable.id,
    consented = spConsentable.consented
  )

  override fun toRN(): ReadableMap = createMap().apply {
    putString("id", id)
    putBoolean("consented", consented)
  }
}
