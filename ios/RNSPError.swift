//
//  RNError.swift
//  Pods
//
//  Created by Andre Herculano on 5/9/25.
//

import ConsentViewController

public enum RNSPErrorName: String {
    case unknown = "Unknown"
    case noInternetConnection = "NoInternetConnection"
    case loadMessagesError = "LoadMessagesError"
    case renderingAppError = "RenderingAppError"
    case reportActionError = "ReportActionError"
    case reportCustomConsentError = "ReportCustomConsentError"
    case androidNoIntentFound = "AndroidNoIntentFound" // (iOS: no equivalent)

    init(_ error: SPError) {
        switch error {

        // MARK: - No Internet
        case is NoInternetConnection:
            self = .noInternetConnection

        // MARK: - Load Messages errors (/get_messages, /message/gdpr, /message/ccpa)
        case is InvalidResponseGetMessagesEndpointError,
             is InvalidResponseMessageGDPREndpointError,
             is InvalidResponseMessageCCPAEndpointError:
            self = .loadMessagesError

        // MARK: - Rendering App / WebView / JSReceiver issues
        case is RenderingAppError,
             is RenderingAppTimeoutError,
             is UnableToInjectMessageIntoRenderingApp,
             is UnableToLoadJSReceiver,
             is WebViewError,
             is WebViewConnectionTimeOutError,
             is InvalidEventPayloadError,
             is InvalidOnActionEventPayloadError,
             is InvalidURLError:
            self = .renderingAppError

        // MARK: - Report Action
        case is ReportActionError,
             is InvalidReportActionEvent:
            self = .reportActionError

        // MARK: - Custom Consent reporting
        case is InvalidResponseCustomError,
             is InvalidResponseDeleteCustomError,
             is PostingCustomConsentWithoutConsentUUID:
            self = .reportCustomConsentError

        default:
            self = .unknown
        }
    }
}

@objcMembers public class RNSPError: NSObject {
  public let name: RNSPErrorName
  public let errorDescription: String
  public let campaignType: SPCampaignType?

  init (name: RNSPErrorName, description: String, campaignType: SPCampaignType?) {
    self.name = name
    self.errorDescription = description
    self.campaignType = campaignType
  }

  convenience init(_ error: SPError) {
    self.init(
      name: RNSPErrorName(error),
      description: error.description,
      campaignType: error.campaignType
    )
  }
}

@objc extension RNSPError: RNSPStringifieable {
  public var stringifiedJson: String { stringifiedJson() }
  public var defaultStringEncoded: String { "{\"name\":\"Unknown\",\"description\":\"unknown error\"}" }
  public var dictionaryEncoded: [String: Any] {
    [
      "name": name.rawValue,
      "description": errorDescription,
      "campaignType": campaignType?.rawValue
    ].compactMapValues { $0 }
  }
}
