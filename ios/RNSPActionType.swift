//
//  RNSPActionType.swift
//  sourcepoint-react-native-cmp
//
//  Created by Andre Herculano on 31/5/24.
//

import Foundation
import ConsentViewController

@objc public enum RNSPActionType: Int, CustomStringConvertible {
    case acceptAll, rejectAll, showPrivacyManager, saveAndExit, dismiss, pmCancel, unknown

  public var description: String {
    return switch self {
      case .acceptAll: "AcceptAll"
      case .rejectAll: "RejectAll"
      case .showPrivacyManager: "ShowPrivacyManager"
      case .saveAndExit: "SaveAndExit"
      case .dismiss: "Dismiss"
      case .pmCancel: "PMCancel"
      case .unknown: "Unknown"
    }
  }

  public init(from actionType: SPActionType){
        switch actionType {
        case .AcceptAll: self = .acceptAll
        case .RejectAll: self = .rejectAll
        case .SaveAndExit: self = .saveAndExit
        case .ShowPrivacyManager: self = .showPrivacyManager
        case .Dismiss: self = .dismiss
        case .PMCancel: self = .pmCancel
        default: self = .unknown
        }
    }
}
