package com.sourcepoint.reactnativecmp

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

abstract class RNSourcepointCmpSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  abstract fun build(accountId: Int, propertyId: Int, propertyName: String, campaigns: ReadableMap, messageLanguage: String)
  abstract fun loadMessage(params: ReadableMap?)
  abstract fun clearLocalData()
  abstract fun getUserData(promise: Promise)
  abstract fun loadGDPRPrivacyManager(pmId: String)
  abstract fun loadUSNatPrivacyManager(pmId: String)
  abstract fun supportedEvents(): Array<String>
}
