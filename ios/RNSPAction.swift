//
//  RNAction.swift
//  Pods
//
//  Created by Andre Herculano on 5/9/25.
//

@objcMembers public class RNSPAction: NSObject {
  public let type: RNSPActionType
  public let customActionId: String?

  @objc public init(type: RNSPActionType, customActionId: String?) {
    self.type = type
    self.customActionId = customActionId
  }
}

@objc extension RNSPAction: RNSPStringifieable {
  public var stringifiedJson: String { stringifiedJson() }
  public var defaultStringEncoded: String { "{\"actionType\":\"unknown\"}" }
  public var dictionaryEncoded: [String: Any] {
    [
      "actionType": type.description,
      "customActionId": customActionId
    ].compactMapValues { $0 }
  }
}
