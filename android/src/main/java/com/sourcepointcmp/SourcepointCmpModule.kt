package com.sourcepointcmp

import android.view.View
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure

import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepoint.cmplibrary.util.userConsents
import org.json.JSONObject

class SourcepointCmpModule internal constructor(context: ReactApplicationContext) :
  SourcepointCmpSpec(context), SpClient {
    enum class SDKEvent {
      onSPUIReady, onSPUIFinished, onAction, onSPFinished, onError
    }

  private var spConsentLib: SpConsentLib? = null
  override fun getName() = NAME
  @ReactMethod
  override fun build(accountId: Int, propertyId: Int, propertyName: String) {
    val config = SpConfigDataBuilder().apply {
      addAccountId(accountId)
      addPropertyName(propertyName)
      addCampaign(CampaignType.GDPR)
      addCampaign(CampaignType.CCPA)
    }.build()

    reactApplicationContext.currentActivity?.let {
      spConsentLib = makeConsentLib(config, it, this)
    } ?: run {
      onError(Error("No activity found when building the SDK"))
    }
  }

  private fun runOnMainThread(runnable: () -> Unit) {
    reactApplicationContext.runOnUiQueueThread(runnable)
  }
  @ReactMethod
  override fun loadMessage() {
    runOnMainThread { spConsentLib?.loadMessage(View.generateViewId()) }
  }
  @ReactMethod
  override fun clearLocalData() {
    clearAllData(reactApplicationContext)
  }
  @ReactMethod
  override fun getUserData(promise: Promise) {
    promise.resolve(userConsentsToWriteableMap(userConsents(reactApplicationContext)))
  }
  @ReactMethod
  override fun loadGDPRPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, CampaignType.GDPR) }
  }
  @ReactMethod
  override fun loadCCPAPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, CampaignType.CCPA) }
  }
  @ReactMethod
  override fun supportedEvents() = SDKEvent.values().map { name }.toTypedArray()
  private fun sendEvent(event: SDKEvent, params: WritableMap? = null) = reactApplicationContext
    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    .emit(event.name, params)

  companion object {
    const val NAME = "SourcepointCmp"
  }

  override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
    sendEvent(SDKEvent.onAction, createMap().apply { putString("actionType", consentAction.actionType.name) })
    return consentAction
  }

  override fun onConsentReady(consent: SPConsents) {
  }

  override fun onError(error: Throwable) {
    sendEvent(SDKEvent.onError, createMap().apply { putString("description", error.message) })
  }

  @Deprecated("onMessageReady callback will be removed in favor of onUIReady. Currently this callback is disabled.")
  override fun onMessageReady(message: JSONObject) {}

  override fun onNativeMessageReady(
    message: MessageStructure,
    messageController: NativeMessageController
  ) {}

  override fun onNoIntentActivitiesFound(url: String) {}

  override fun onSpFinished(sPConsents: SPConsents) {
    sendEvent(SDKEvent.onSPFinished)
  }

  override fun onUIFinished(view: View) {
    spConsentLib?.removeView(view)
    sendEvent(SDKEvent.onSPUIFinished)
  }

  override fun onUIReady(view: View) {
    spConsentLib?.showView(view)
    sendEvent(SDKEvent.onSPUIReady)
  }

  private fun userConsentsToWriteableMap(consents: SPConsents) = createMap().apply {
    consents.ccpa?.let { ccpa ->
      putMap("ccpa", createMap().apply {
        putString("uuid", ccpa.consent.uuid)
        putBoolean("applies", ccpa.consent.applies)
        putString("uspstring", ccpa.consent.uspstring)
      })
    }

    consents.gdpr?.let { gdpr ->
      putMap("gdpr", createMap().apply {
        putString("uuid", gdpr.consent.uuid)
        putBoolean("applies", gdpr.consent.applies)
        putString("euconsent", gdpr.consent.euconsent)
      })
    }
  }
}
