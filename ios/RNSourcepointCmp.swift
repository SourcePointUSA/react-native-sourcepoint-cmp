//
//  RNSourcepointCmp.swift
//  react-native-sourcepoint-cmp
//
//  Created by Andre Herculano on 21.12.23.
//

import ConsentViewController
import Foundation
import React

@objc(RNSourcepointCmp)
@objcMembers class RNSourcepointCmp: RCTEventEmitter {
    @objc public static var shared: RNSourcepointCmp?

    var consentManager: SPConsentManager?

    override init() {
        super.init()
        RNSourcepointCmp.shared = self
    }

    open override func supportedEvents() -> [String] {
        ["onSPUIReady", "onSPUIFinished", "onAction", "onSPFinished", "onError"]
    }

    func getUserData(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(RNSPUserData(from: consentManager?.userData).toDictionary())
    }

    func build(_ accountId: Int, propertyId: Int, propertyName: String, campaigns: SPCampaigns, messageLanguage: String) {
        let manager = SPConsentManager(
            accountId: accountId,
            propertyId: propertyId,
            propertyName: try! SPPropertyName(propertyName),
            campaigns: campaigns,
            delegate: self
        )

        if !messageLanguage.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            manager.messageLanguage = messageLanguage
        }

        manager.messageTimeoutInSeconds = 30
        RNSourcepointCmp.shared?.consentManager = manager
    }

    func loadMessage(_ params: SPLoadMessageParams) {
        consentManager?.loadMessage(forAuthId: params.authId, pubData: nil)
    }

    // TODO: fix an issue with `SPConsentManager.clearAllData` returning in-memory data
    // SPConsentManager.clearAllData() clears all data from UserDefaults, but SPCoordinator
    // keeps a copy of it in-memory. When calling `getUserData` right after, returns its
    // in-memory copy.
    func clearLocalData() {
        SPConsentManager.clearAllData()
    }

    func loadGDPRPrivacyManager(_ pmId: String) {
        consentManager?.loadGDPRPrivacyManager(withId: pmId)
    }

    func loadUSNatPrivacyManager(_ pmId: String) {
        consentManager?.loadUSNatPrivacyManager(withId: pmId)
    }
}

extension RNSourcepointCmp: SPDelegate {
    weak var rootViewController: UIViewController? {
        UIApplication.shared.delegate?.window??.rootViewController
    }

    func onAction(_ action: SPAction, from controller: UIViewController) {
        RNSourcepointCmp.shared?.sendEvent(
            withName: "onAction",
            body: [
              "actionType": RNSourcepointActionType(from: action.type).rawValue,
              "customActionId": action.customActionId ?? "",
            ]
        )
    }

    func onSPUIReady(_ controller: UIViewController) {
        RNSourcepointCmp.shared?.sendEvent(withName: "onSPUIReady", body: [])
        controller.modalPresentationStyle = .overFullScreen
        rootViewController?.present(controller, animated: true)
    }

    func onSPUIFinished(_ controller: UIViewController) {
        RNSourcepointCmp.shared?.sendEvent(withName: "onSPUIFinished", body: [])
        rootViewController?.dismiss(animated: true)
    }

    func onSPFinished(userData: SPUserData) {
        RNSourcepointCmp.shared?.sendEvent(withName: "onSPFinished", body: [])
    }

    func onError(error: SPError) {
        RNSourcepointCmp.shared?.sendEvent(withName: "onError", body: ["description": error.description])
        print("Something went wrong", error)
    }
}
