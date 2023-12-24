//
//  SourcepointCmp.swift
//  react-native-sourcepoint-cmp
//
//  Created by Andre Herculano on 21.12.23.
//

import ConsentViewController
import Foundation
import React

@objc(SourcepointCmp)
@objcMembers class SourcepointCmp: RCTEventEmitter {
    @objc public static var shared: SourcepointCmp?

    var consentManager: SPConsentManager?

    override init() {
        super.init()
        SourcepointCmp.shared = self
    }

    open override func supportedEvents() -> [String] {
        ["onSPUIReady", "onSPUIFinished", "onAction", "onSPFinished", "onError"]
    }

    func getUserData(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(consentManager?.userData.toDictionary() ?? [:])
    }

    // TODO: move campaigns to the js build method
    func build(_ accountId: Int, propertyId: Int, propertyName: String) {
        SourcepointCmp.shared?.consentManager = SPConsentManager(
            accountId: accountId,
            propertyName: try! SPPropertyName(propertyName),
            campaigns: SPCampaigns(gdpr: SPCampaign(), ccpa: SPCampaign()),
            delegate: self
        )
    }

    func loadMessage() {
        consentManager?.loadMessage()
    }

    func clearLocalData() {
        SPConsentManager.clearAllData()
    }

    func loadGDPRPrivacyManager(_ pmId: String) {
        consentManager?.loadGDPRPrivacyManager(withId: pmId)
    }

    func loadCCPAPrivacyManager(_ pmId: String) {
        consentManager?.loadCCPAPrivacyManager(withId: pmId)
    }
}

extension SourcepointCmp: SPDelegate {
    weak var rootViewController: UIViewController? {
        UIApplication.shared.delegate?.window??.rootViewController
    }

    func onAction(_ action: SPAction, from controller: UIViewController) {
        SourcepointCmp.shared?.sendEvent(withName: "onAction", body: ["actionType": action.type.description])
    }

    func onSPUIReady(_ controller: UIViewController) {
        SourcepointCmp.shared?.sendEvent(withName: "onSPUIReady", body: [])
        controller.modalPresentationStyle = .overFullScreen
        rootViewController?.present(controller, animated: true)
    }

    func onSPUIFinished(_ controller: UIViewController) {
        SourcepointCmp.shared?.sendEvent(withName: "onSPUIFinished", body: [])
        rootViewController?.dismiss(animated: true)
    }

    func onSPFinished(userData: SPUserData) {
        SourcepointCmp.shared?.sendEvent(withName: "onSPFinished", body: [])
    }

    func onError(error: SPError) {
        SourcepointCmp.shared?.sendEvent(withName: "onError", body: ["description": error.description])
        print("Something went wrong", error)
    }
}
