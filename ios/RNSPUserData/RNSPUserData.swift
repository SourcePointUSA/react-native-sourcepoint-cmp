//
//  RNSPUserData.swift
//  sourcepoint-react-native-cmp
//
//  Created by Andre Herculano on 21/5/24.
//

import Foundation
import ConsentViewController

struct RNSPUserData: Encodable {
    let gdpr: RNSPGDPRConsent?
    let usnat: RNSPUSNatConsent?
    let preferences: RNSPPreferencesConsent?
    let globalcmp: RNSPGlobalCMPConsent?

    init(
        gdpr: RNSPGDPRConsent? = nil,
        usnat: RNSPUSNatConsent? = nil,
        preferences: RNSPPreferencesConsent? = nil,
        globalcmp: RNSPGlobalCMPConsent? = nil
    ) {
        self.gdpr = gdpr
        self.usnat = usnat
        self.preferences = preferences
        self.globalcmp = globalcmp
    }
}

extension RNSPUserData {
    init(from sdkConsents: SPUserData?) {
        guard let sdkConsents = sdkConsents else {
            self.init()
            return
        }

        self.init(
            gdpr: RNSPGDPRConsent(from: sdkConsents.gdpr?.consents),
            usnat: RNSPUSNatConsent(from: sdkConsents.usnat?.consents),
            preferences: RNSPPreferencesConsent(from: sdkConsents.preferences?.consents),
            globalcmp: RNSPGlobalCMPConsent(from: sdkConsents.globalcmp?.consents)
        )
    }
}
