//
//  RNGDPRConsent.swift
//  sourcepoint-react-native-cmp
//
//  Created by Andre Herculano on 22/5/24.
//

import Foundation
import ConsentViewController

// encapsulates GDPR consent
struct RNSPGDPRConsent: Encodable {
    struct Statuses: Encodable {
        let consentedAll, consentedAny, rejectedAny: Bool?
    }

    let uuid, euconsent: String?
    let applies: Bool?
    let expirationDate, createdDate: SPDate?
    let vendorGrants: SPGDPRVendorGrants
    let statuses: Statuses
    let tcfData: SPJson?
}

extension RNSPGDPRConsent.Statuses {
    init(from status: ConsentStatus) {
        consentedAll = status.consentedAll
        consentedAny = status.consentedToAny
        rejectedAny = status.rejectedAny
    }
}

extension RNSPGDPRConsent {
    init?(from gdpr: SPGDPRConsent?) {
        guard let gdpr = gdpr else { return nil }

        self.init(
            uuid: gdpr.uuid,
            euconsent: gdpr.euconsent,
            applies: gdpr.applies,
            expirationDate: nil,
            createdDate: gdpr.dateCreated,
            vendorGrants: gdpr.vendorGrants,
            statuses: Statuses(from: gdpr.consentStatus),
            tcfData: gdpr.tcfData
        )
    }
}
