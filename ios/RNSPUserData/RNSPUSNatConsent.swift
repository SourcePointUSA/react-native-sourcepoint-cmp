//
//  RNSPUSNatConsent.swift
//  sourcepoint-react-native-cmp
//
//  Created by Andre Herculano on 22/5/24.
//

import Foundation
import ConsentViewController

// Encapsulates USNat (sometimes called MSPS) consent.
// This is an adapter of `SPUSNatConsent`
struct RNSPUSNatConsent: Encodable {
    struct Statuses: Encodable {
        let consentedAll, consentedAny, rejectedAny,
            sellStatus, shareStatus,
            sensitiveDataStatus, gpcStatus: Bool?
    }

    struct Consentable: Encodable {
        let id: String
        let consented: Bool
    }

    struct ConsentSection: Encodable {
        let id: Int
        let name, consentString: String
    }

    // the uuid given to a consent profile (user) it can be nil if the profile is not yet created
    let uuid: String?

    let expirationDate, createdDate: SPDate?

    // a list of consent sections (id, name and consent string) applicable to that user
    let consentSections: [ConsentSection]

    let statuses: Statuses?

    // this data is stored at the "root level" of the `UserDefaults` as specified by
    // https://github.com/InteractiveAdvertisingBureau/Global-Privacy-Platform/blob/main/Core/CMP%20API%20Specification.md#in-app-details
    let gppData: SPJson?

    // a list of vendors/categories identified by id, indicating if they are consented or rejected based on the boolean `consented`
    let vendors, categories: [Consentable]
}

extension RNSPUSNatConsent.Statuses {
    init(from status: SPUSNatConsent.Statuses) {
        consentedAll = status.consentedToAll
        consentedAny = status.consentedToAny
        rejectedAny = status.rejectedAny
        sellStatus = status.sellStatus
        shareStatus = status.shareStatus
        sensitiveDataStatus = status.sensitiveDataStatus
        gpcStatus = status.gpcStatus
    }
}

extension RNSPUSNatConsent.Consentable {
    init(from consentable: SPConsentable) {
        id = consentable.id
        consented = consentable.consented
    }
}

extension RNSPUSNatConsent.ConsentSection {
    init(from section: SPUSNatConsent.ConsentString) {
        id = section.sectionId
        name = section.sectionName
        consentString = section.consentString
    }
}

extension RNSPUSNatConsent {
    init?(from usnat: SPUSNatConsent?) {
        guard let usnat = usnat else { return nil }

        self.init(
            uuid: usnat.uuid,
            expirationDate: nil,
            createdDate: nil,
            consentSections: usnat.consentStrings.map { ConsentSection(from: $0) },
            statuses: Statuses(from: usnat.statuses),
            gppData: usnat.GPPData,
            vendors: usnat.vendors.map { Consentable(from: $0) },
            categories: usnat.categories.map { Consentable(from: $0) }
        )
    }
}
