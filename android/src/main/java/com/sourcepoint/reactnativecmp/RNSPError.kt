package com.sourcepoint.reactnativecmp

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.FailedToDeleteCustomConsent
import com.sourcepoint.cmplibrary.exception.FailedToLoadMessages
import com.sourcepoint.cmplibrary.exception.FailedToPostCustomConsent
import com.sourcepoint.cmplibrary.exception.NoIntentFoundForUrl
import com.sourcepoint.cmplibrary.exception.NoInternetConnectionException
import com.sourcepoint.cmplibrary.exception.RenderingAppException
import com.sourcepoint.cmplibrary.exception.ReportActionException
import com.sourcepoint.cmplibrary.exception.UnableToDownloadRenderingApp
import com.sourcepoint.cmplibrary.exception.UnableToLoadRenderingApp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class RNSPErrorName {
  Unknown,
  NoInternetConnection,
  LoadMessagesError,
  RenderingAppError,
  ReportActionError,
  ReportCustomConsentError,
  AndroidNoIntentFound;

  companion object {
    fun from(throwable: Throwable) = when (throwable) {
      is NoInternetConnectionException -> NoInternetConnection
      is FailedToLoadMessages -> LoadMessagesError
      is UnableToDownloadRenderingApp, is UnableToLoadRenderingApp, is RenderingAppException -> RenderingAppError
      is ReportActionException -> ReportActionError
      is FailedToPostCustomConsent, is FailedToDeleteCustomConsent -> ReportCustomConsentError
      is NoIntentFoundForUrl -> AndroidNoIntentFound
      else -> Unknown
    }
  }
}

data class RNSPError(val name: RNSPErrorName, val description: String, val campaignType: CampaignType?) {
  companion object {
    fun from(throwable: Throwable) = RNSPError(
      name = RNSPErrorName.from(throwable),
      description = (throwable as? ConsentLibExceptionK)?.description ?: "No description available",
      campaignType = null
    )
  }

  fun toMap() = mutableMapOf(
    "name" to name.name,
    "description" to description,
  ).apply {
    campaignType?.let { this["campaignType"] = it.name }
  }

  fun toJsonString(): String = Json.encodeToString(toMap())
}
