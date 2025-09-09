//
//  RNSPCampaignType.swift
//  Pods
//
//  Created by Andre Herculano on 5/9/25.
//

import Foundation
import ConsentViewController

enum RNSPCampaignType: String {
  case gdpr, usnat, preferences, globalcmp, unknown
  
  init(_ campaignType: SPCampaignType) {
    switch campaignType {
    case .gdpr: self = .gdpr
    case .usnat: self = .usnat
    case .preferences: self = .preferences
    case .globalcmp: self = .globalcmp
    default: self = .unknown
    }
  }
  
  public init(rawValue: String) {
    switch rawValue.lowercased() {
    case "gdpr": self = .gdpr
    case "usnat": self = .usnat
    case "preferences": self = .preferences
    case "globalcmp": self = .globalcmp
    default: self = .unknown
    }
  }
  
  func toSP() -> SPCampaignType {
    switch self {
    case .gdpr: return .gdpr
    case .usnat: return .usnat
    case .preferences: return .preferences
    case .globalcmp: return .globalcmp
    default: return .unknown
    }
  }
}
