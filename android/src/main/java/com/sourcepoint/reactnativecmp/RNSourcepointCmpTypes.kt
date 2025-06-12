package com.sourcepoint.reactnativecmp

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

fun campaignsEnvFrom(rawValue: String?): CampaignsEnv? =
  when (rawValue) {
    "Public" -> CampaignsEnv.PUBLIC
    "Stage" -> CampaignsEnv.STAGE
    else -> { null }
}

data class SPCampaign(
  val rawTargetingParam: ReadableMap?,
  val supportLegacyUSPString: Boolean
) {
  val targetingParams = rawTargetingParam?.toHashMap()?.map { TargetingParam(it.key, it.value.toString()) } ?: emptyList()
}

data class SPCampaigns(
  val gdpr: SPCampaign?,
  val usnat: SPCampaign?,
  val environment: CampaignsEnv?
)

enum class RNSourcepointActionType {
  acceptAll, rejectAll, showPrivacyManager, saveAndExit, dismiss, pmCancel, unknown;

  companion object {
    fun from(spAction: ActionType): RNSourcepointActionType =
      when (spAction) {
        ActionType.ACCEPT_ALL -> acceptAll
        ActionType.REJECT_ALL -> rejectAll
        ActionType.SHOW_OPTIONS -> showPrivacyManager
        ActionType.SAVE_AND_EXIT -> saveAndExit
        ActionType.MSG_CANCEL -> dismiss
        ActionType.PM_DISMISS -> pmCancel
        else -> unknown
      }
  }
}

fun ReadableMap.SPCampaign() = SPCampaign(
  rawTargetingParam = this.getMap("targetingParams"),
  supportLegacyUSPString = if(this.hasKey("supportLegacyUSPString")) this.getBoolean("supportLegacyUSPString") else false
)

fun ReadableMap.SPCampaigns() = SPCampaigns(
  gdpr = this.getMap("gdpr")?.SPCampaign(),
  usnat = this.getMap("usnat")?.SPCampaign(),
  environment = campaignsEnvFrom(rawValue = this.getString("environment"))
)
