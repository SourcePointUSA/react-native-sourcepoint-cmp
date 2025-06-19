//
//  RNSPCampaigns.swift
//  Pods
//
//  Created by Andre Herculano on 6/6/25.
//

import Foundation
import ConsentViewController

@objc public enum RNSPCampaignEnv: Int {
    case Stage = 0
    case Public = 1

  func toSP() -> SPCampaignEnv {
    SPCampaignEnv(rawValue: self.rawValue)!
  }
}

@objcMembers public class RNSPCampaign: NSObject {
    @objc public let targetingParams: [String: String]
    @objc public let groupPmId: String?
    @objc public let supportLegacyUSPString: Bool

    @objc public init(targetingParams: [String: String], groupPmId: String?, supportLegacyUSPString: Bool) {
        self.targetingParams = targetingParams
        self.groupPmId = groupPmId
        self.supportLegacyUSPString = supportLegacyUSPString
    }

    public func toSP() -> SPCampaign {
        SPCampaign(
            targetingParams: targetingParams,
            groupPmId: groupPmId,
            supportLegacyUSPString: supportLegacyUSPString
        )
    }
}

@objcMembers public class RNSPCampaigns: NSObject {
    @objc public let gdpr, ccpa, usnat, ios14, preferences, globalcmp: RNSPCampaign?
    @objc public let environment: RNSPCampaignEnv

    @objc public init(
        gdpr: RNSPCampaign? = nil,
        ccpa: RNSPCampaign? = nil,
        usnat: RNSPCampaign? = nil,
        ios14: RNSPCampaign? = nil,
        preferences: RNSPCampaign? = nil,
        globalcmp: RNSPCampaign? = nil,
        environment: RNSPCampaignEnv = .Public
    ) {
        self.gdpr = gdpr
        self.ccpa = ccpa
        self.usnat = usnat
        self.ios14 = ios14
        self.preferences = preferences
        self.globalcmp = globalcmp
        self.environment = environment
    }

    public func toSP() -> SPCampaigns {
        return SPCampaigns(
            gdpr: gdpr?.toSP(),
            ccpa: ccpa?.toSP(),
            usnat: usnat?.toSP(),
            ios14: ios14?.toSP(),
            globalcmp: globalcmp?.toSP(),
            preferences: preferences?.toSP(),
            environment: environment.toSP()
        )
    }
}
