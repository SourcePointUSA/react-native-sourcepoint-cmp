package com.sourcepoint.reactnativecmp

import android.view.View
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.ConfigOption.SUPPORT_LEGACY_USPSTRING
import com.sourcepoint.cmplibrary.creation.SpConfigDataBuilder
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.data.network.util.CampaignType.*
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepoint.cmplibrary.util.userConsents
import com.sourcepoint.reactnativecmp.arguments.BuildOptions
import com.sourcepoint.reactnativecmp.arguments.toList
import com.sourcepoint.reactnativecmp.consents.RNSPGDPRConsent
import com.sourcepoint.reactnativecmp.consents.RNSPUserData

data class SPLoadMessageParams(val authId: String?) {
  constructor(fromReadableMap: ReadableMap?) : this(authId = fromReadableMap?.getString("authId"))
}

class ReactNativeCmpModule(reactContext: ReactApplicationContext) : NativeReactNativeCmpSpec(reactContext),
  SpClient {
  private var spConsentLib: SpConsentLib? = null

  override fun getName() = NAME

  override fun build(
    accountId: Double,
    propertyId: Double,
    propertyName: String,
    campaigns: ReadableMap,
    options: ReadableMap?,
  ) {
    val convertedCampaigns = campaigns.SPCampaigns()
    val parsedOptions = BuildOptions(options)
    val config = SpConfigDataBuilder().apply {
      addAccountId(accountId.toInt())
      addPropertyName(propertyName)
      addPropertyId(propertyId.toInt())
      addMessageTimeout(parsedOptions.messageTimeoutInMilliseconds)
      addMessageLanguage(parsedOptions.language)
      convertedCampaigns.gdpr?.let {
        addCampaign(campaignType = GDPR, params = it.targetingParams, groupPmId = it.groupPmId)
      }
      convertedCampaigns.usnat?.let {
        addCampaign(
          campaignType = USNAT,
          params = it.targetingParams,
          groupPmId = it.groupPmId,
          configParams = if(it.supportLegacyUSPString) setOf(SUPPORT_LEGACY_USPSTRING) else emptySet()
        )
      }
      convertedCampaigns.preferences?.let {
        addCampaign(campaignType = PREFERENCES, params = it.targetingParams, groupPmId = it.groupPmId)
      }
      convertedCampaigns.globalcmp?.let {
        addCampaign(campaignType = GLOBALCMP, params = it.targetingParams, groupPmId = it.groupPmId)
      }
    }.build()

    reactApplicationContext.currentActivity?.let {
      spConsentLib = makeConsentLib(config, it, this, parsedOptions.dismissMessageOnBackPress)
    } ?: run {
      onError(Error("No activity found when building the SDK"))
    }
  }

  private fun runOnMainThread(runnable: () -> Unit) {
    reactApplicationContext.runOnUiQueueThread(runnable)
  }

  override fun loadMessage(params: ReadableMap?) {
    val parsedParams = SPLoadMessageParams(fromReadableMap = params)

    runOnMainThread {
      spConsentLib?.loadMessage(authId = parsedParams.authId, cmpViewId = View.generateViewId())
    }
  }

  override fun clearLocalData() {
    clearAllData(reactApplicationContext)
  }

  override fun getUserData(promise: Promise) {
    promise.resolve(userConsentsToWriteableMap(userConsents(reactApplicationContext)))
  }

  override fun loadGDPRPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, GDPR) }
  }

  override fun loadUSNatPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, USNAT) }
  }

  override fun loadGlobalCmpPrivacyManager(pmId: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(pmId, GLOBALCMP) }
  }

  override fun loadPreferenceCenter(id: String) {
    runOnMainThread { spConsentLib?.loadPrivacyManager(id, PREFERENCES) }
  }

  override fun dismissMessage() {
    runOnMainThread { spConsentLib?.dismissMessage() }
  }

  override fun postCustomConsentGDPR(
    vendors: ReadableArray,
    categories: ReadableArray,
    legIntCategories: ReadableArray,
    callback: Callback
  ) {
    runOnMainThread {
      spConsentLib?.customConsentGDPR(
        vendors.toList(),
        categories.toList(),
        legIntCategories.toList(),
        success = { consents ->
          if (consents?.gdpr != null) {
            callback.invoke(RNSPGDPRConsent(consents.gdpr!!.consent).toRN())
          } else {
            callback.invoke(RNSPGDPRConsent(applies = true).toRN())
          }
        }
      )
    }
  }

  override fun postDeleteCustomConsentGDPR(
    vendors: ReadableArray,
    categories: ReadableArray,
    legIntCategories: ReadableArray,
    callback: Callback)
  {
    runOnMainThread {
      spConsentLib?.deleteCustomConsentTo(
        vendors.toList(),
        categories.toList(),
        legIntCategories.toList(),
        success = { consents ->
          if (consents?.gdpr != null) {
            callback.invoke(RNSPGDPRConsent(consents.gdpr!!.consent).toRN())
          } else {
            callback.invoke(RNSPGDPRConsent(applies = true).toRN())
          }
        }
      )
    }
  }

  companion object {
    const val NAME = "ReactNativeCmp"
  }

  override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
    emitOnAction(createMap().apply {
      putString("actionType", RNSourcepointActionType.from(consentAction.actionType).name)
      putString("customActionId", consentAction.customActionId)
    })
    return consentAction
  }

  override fun onConsentReady(consent: SPConsents) {}

  override fun onError(error: Throwable) {
    emitOnError(createMap().apply { putString("description", error.message) })
  }

  override fun onNoIntentActivitiesFound(url: String) {}

  override fun onSpFinished(sPConsents: SPConsents) {
    emitOnFinished()
  }

  override fun onMessageInactivityTimeout() {
    emitOnMessageInactivityTimeout()
  }

  override fun onUIFinished(view: View) {
    spConsentLib?.removeView(view)
    emitOnSPUIFinished()
  }

  override fun onUIReady(view: View) {
    spConsentLib?.showView(view)
    emitOnSPUIReady()
  }

  private fun userConsentsToWriteableMap(consents: SPConsents) = RNSPUserData(consents).toRN()
}
