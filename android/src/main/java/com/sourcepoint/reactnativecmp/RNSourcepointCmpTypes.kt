package com.sourcepoint.reactnativecmp

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.ActionType.*
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

fun campaignsEnvFrom(rawValue: String?): CampaignsEnv? =
  when (rawValue) {
    "Public" -> CampaignsEnv.PUBLIC
    "Stage" -> CampaignsEnv.STAGE
    else -> { null }
}

data class SPCampaign(
  val rawTargetingParam: ReadableMap?,
  val supportLegacyUSPString: Boolean,
  val groupPmId: String? = null,
) {
  val targetingParams = rawTargetingParam?.toHashMap()?.map { TargetingParam(it.key, it.value.toString()) } ?: emptyList()
}

data class SPCampaigns(
  val gdpr: SPCampaign?,
  val usnat: SPCampaign?,
  val preferences: SPCampaign?,
  val environment: CampaignsEnv?
)

enum class RNSourcepointActionType {
  acceptAll, rejectAll, showPrivacyManager, saveAndExit, dismiss, pmCancel, unknown;

  companion object {
    fun from(spAction: ActionType): RNSourcepointActionType =
      when (spAction) {
        ACCEPT_ALL -> acceptAll
        REJECT_ALL -> rejectAll
        SHOW_OPTIONS -> showPrivacyManager
        SAVE_AND_EXIT -> saveAndExit
        MSG_CANCEL -> dismiss
        PM_DISMISS -> pmCancel
        else -> unknown
      }
  }
}

fun ReadableMap.SPCampaign() = SPCampaign(
  rawTargetingParam = getMap("targetingParams"),
  supportLegacyUSPString = if(hasKey("supportLegacyUSPString")) getBoolean("supportLegacyUSPString") else false,
  groupPmId = getString("id")
)

fun ReadableMap.SPCampaigns() = SPCampaigns(
  gdpr = this.getMap("gdpr")?.SPCampaign(),
  usnat = this.getMap("usnat")?.SPCampaign(),
  preferences = this.getMap("preferences")?.SPCampaign(),
  environment = campaignsEnvFrom(rawValue = this.getString("environment"))
)
