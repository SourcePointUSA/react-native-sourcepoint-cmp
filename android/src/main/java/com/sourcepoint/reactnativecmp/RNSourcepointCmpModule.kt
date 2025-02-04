package com.sourcepoint.reactnativecmp

import android.view.View
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepoint.cmplibrary.util.userConsents
import com.sourcepoint.reactnativecmp.consents.RNSPUserData
import org.json.JSONObject

data class SPLoadMessageParams(val authId: String?) {
  constructor(fromReadableMap: ReadableMap?) : this(authId = fromReadableMap?.getString("authId"))
}

class RNSourcepointCmpModule internal constructor(context: ReactApplicationContext) :
  RNSourcepointCmpSpec(context) , SpClient {
    enum class SDKEvent {
      onSPUIReady, onSPUIFinished, onAction, onSPFinished, onError
    }

  private var spConsentLib: SpConsentLib? = null

  override fun getName() = NAME

  @ReactMethod
  override fun build(accountId: Int, propertyId: Int, propertyName: String, campaigns: ReadableMap, messageLanguage: String) {
    val convertedCampaigns = campaigns.SPCampaigns()

    val config = SpConfigDataBuilder().apply {
      addAccountId(accountId)
      addPropertyName(propertyName)
      addPropertyId(propertyId)
      addMessageTimeout(30000)

      if (messageLanguage.isNotBlank()) {
        addMessageLanguage(MessageLanguage.SPANISH)
      }

      convertedCampaigns.gdpr?.let {
        addCampaign(campaignType = CampaignType.GDPR, params = it.targetingParams, groupPmId = null)
      }
      convertedCampaigns.usnat?.let {
        addCampaign(
          campaignType = CampaignType.USNAT,
          params = it.targetingParams,
          groupPmId = null,
          configParams = if(it.supportLegacyUSPString) setOf(ConfigOption.SUPPORT_LEGACY_USPSTRING) else emptySet()
        )
      }
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
  override fun loadMessage(params: ReadableMap?) {
    val parsedParams = SPLoadMessageParams(fromReadableMap = params)

    runOnMainThread { spConsentLib?.loadMessage(
      authId = parsedParams.authId,
      cmpViewId = View.generateViewId()
    ) }
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
  override fun loadUSNatPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, CampaignType.USNAT) }
  }

  @ReactMethod
  override fun supportedEvents() = SDKEvent.entries.map { name }.toTypedArray()
  private fun sendEvent(event: SDKEvent, params: WritableMap? = null) = reactApplicationContext
    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    .emit(event.name, params)

  @ReactMethod
  fun addListener(eventName: String) {}

  @ReactMethod
  fun removeListeners(count: Int) {}

  companion object {
    const val NAME = "RNSourcepointCmp"
  }

  override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
    sendEvent(SDKEvent.onAction, createMap().apply {
      putString("actionType", RNSourcepointActionType.from(consentAction.actionType).name)
      putString("customActionId", consentAction.customActionId ?: "")
    })
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

  private fun userConsentsToWriteableMap(consents: SPConsents) = RNSPUserData(consents).toRN()
}
