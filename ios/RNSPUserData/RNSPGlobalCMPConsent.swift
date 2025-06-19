//
//  RNSPGlobalCMPConsent.swift
//  sourcepoint-react-native-cmp
//
//  Created by Andre Herculano on 22/5/24.
//

import Foundation
import ConsentViewController

struct RNSPGlobalCMPConsent: Encodable {
    let uuid: String?
    let applies: Bool
    let vendors, categories: [RNSPConsentable]
}

extension RNSPGlobalCMPConsent {
    init?(from globalCMP: SPGlobalCmpConsent?) {
        guard let globalCMP = globalCMP else { return nil }

        self.init(
          uuid: globalCMP.uuid,
          applies: globalCMP.applies,
          vendors: globalCMP.vendors.map { RNSPConsentable(from: $0) },
          categories: globalCMP.categories.map { RNSPConsentable(from: $0) }
        )
    }
} 
