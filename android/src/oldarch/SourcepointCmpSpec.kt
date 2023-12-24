package com.sourcepointcmp

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise

abstract class SourcepointCmpSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun build(accountId: Int, propertyId: Int, propertyName: String)
  abstract fun loadMessage()
  abstract fun clearLocalData()
  abstract fun getUserData(promise: Promise)
  abstract fun loadGDPRPrivacyManager(pmId: String)
  abstract fun loadCCPAPrivacyManager(pmId: String)
  abstract fun supportedEvents(): Array<String>
}
