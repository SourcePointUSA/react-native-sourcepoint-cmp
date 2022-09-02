package com.reactnativesourcepointcmp

import android.R.id.content
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.*
import com.facebook.react.bridge.Arguments.createMap
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

class SourcepointCmpModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), SpClient {
  var spConsentLib: SpConsentLib? = null

  override fun getName(): String {
    return "SourcepointCmp"
  }

  private fun sendEvent(eventName: String, params: WritableMap?) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  @ReactMethod
  fun build(accountId: Int, propertyId: Int, propertyName: String) {
    val config = SpConfigDataBuilder().apply {
      addAccountId(accountId)
      addPropertyName(propertyName)
      addCampaign(CampaignType.GDPR)
      addCampaign(CampaignType.CCPA)
    }.build()
    /// TODO: remove optional unwrapping !!
    spConsentLib = makeConsentLib(config, reactApplicationContext.currentActivity!!, this)
  }

  private fun runOnMainThread(runnable: () -> Unit) {
    reactApplicationContext.runOnUiQueueThread(runnable)
  }

  @ReactMethod
  fun loadMessage() {
    runOnMainThread { spConsentLib?.loadMessage(View.generateViewId()) }
  }

  @ReactMethod
  fun clearLocalData() {
    clearAllData(reactApplicationContext)
  }

  @ReactMethod
  fun loadGDPRPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, CampaignType.GDPR) }
  }

  @ReactMethod
  fun loadCCPAPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, CampaignType.CCPA) }
  }

  @ReactMethod
  fun getUserData(promise: Promise) {
    /// TODO: convert SPConsents to WritableMap
    val userData = userConsents(reactApplicationContext).
    val thing = createMap().apply {
      putMap("gdpr", createMap().apply {
        putBoolean("applies", userData.gdpr?.consent?.applies ?: false)
        putString("uuid", userData.gdpr?.consent?.uuid ?: "unset")
      })
    }

    promise.resolve(thing)
  }

    @Deprecated("")
    override fun onMessageReady(message: JSONObject) { /* ... */ }
    override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) { /* ... */ }
    override fun onNoIntentActivitiesFound(url: String) {}
    override fun onError(error: Throwable) { /* ... */ }
    override fun onConsentReady(consent: SPConsents) { /* ... */ }
    override fun onAction(view: View, consentAction: ConsentAction) : ConsentAction{  return consentAction }

    override fun onUIFinished(view: View) {
      spConsentLib?.removeView(view)
    }

    override fun onUIReady(view: View) {
      spConsentLib?.showView(view)
    }

    override fun onSpFinished(sPConsents: SPConsents) {
      sendEvent("onSPFinished", null)
    }
//
//    private fun removeView(view: View) {
//      reactApplicationContext.currentActivity?.findViewById<View>(R.id.content)?.let {
//        it.post {
//          (it as ViewGroup).removeView(view)
//        }
//      }
//    }
//
//    private fun showView(view: View) {
//      if (view.parent == null) {
//        reactApplicationContext.currentActivity?.findViewById<View>(content)?.let {
//          it.post {
//            view.layoutParams = ViewGroup.LayoutParams(0, 0)
//            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//            view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//            view.bringToFront()
//            view.requestLayout()
//            (it as ViewGroup).addView(view)
//          }
//        }
//      }
//    }
}
