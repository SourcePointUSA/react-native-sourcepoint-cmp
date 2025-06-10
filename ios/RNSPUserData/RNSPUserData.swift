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

    init(
        gdpr: RNSPGDPRConsent? = nil,
        usnat: RNSPUSNatConsent? = nil
    ) {
        self.gdpr = gdpr
        self.usnat = usnat
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
            usnat: RNSPUSNatConsent(from: sdkConsents.usnat?.consents)
        )
    }
}
