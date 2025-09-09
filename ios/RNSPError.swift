//
//  RNError.swift
//  Pods
//
//  Created by Andre Herculano on 5/9/25.
//

import ConsentViewController

public enum RNSPErrorName: String {
  case unknown = "Unknown"

  init(_ error: SPError) {
    switch error {
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
