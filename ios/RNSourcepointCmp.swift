//
//  RNSourcepointCmp.swift
//  react-native-sourcepoint-cmp
//
//  Created by Andre Herculano on 21.12.23.
//

import ConsentViewController
import Foundation
import React

@objcMembers public class RNBuildOptions: NSObject {
  public let language: String
  public let messageTimeout: Int

  @objc public init(language: String, messageTimeout: Int) {
    self.language = language
    self.messageTimeout = messageTimeout
  }
}

@objcMembers public class RNAction: NSObject {
  public let type: RNSourcepointActionType
  public let customActionId: String?

  @objc public init(type: RNSourcepointActionType, customActionId: String?) {
    self.type = type
    self.customActionId = customActionId
  }

  func toDictionary() -> [String: Any] {
    ["actionType": type.description, "customActionId": customActionId]
  }

  @objc public func stringifiedJson() -> String {
    if let jsonData = try? JSONSerialization.data(withJSONObject: toDictionary()),
       let jsonString = String(data: jsonData, encoding: .utf8) {
        return jsonString
    } else {
      return "{\"actionType:\"unknown\"}"
    }
  }
}

@objc public protocol ReactNativeCmpImplDelegate {
  func onAction(_ action: RNAction)
  func onSPUIReady()
  func onSPUIFinished()
  func onFinished()
  func onMessageInactivityTimeout()
  func onError(description: String)
}

@objcMembers public class ReactNativeCmpImpl: NSObject {
  @objc public static var shared: ReactNativeCmpImpl?

  private var objcDelegate: CMPDelegateHandler?

  var consentManager: SPConsentManager?
  weak var delegate: ReactNativeCmpImplDelegate?

  public override init() {
    super.init()
    objcDelegate = CMPDelegateHandler(parent: self)
    Self.shared = self
  }

  public func getUserData(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    resolve(RNSPUserData(from: consentManager?.userData).toDictionary())
  }

  public func loadMessage(_ params: RNSPLoadMessageParams) {
    consentManager?.loadMessage(forAuthId: params.authId, pubData: nil)
  }

  // TODO: fix an issue with `SPConsentManager.clearAllData` returning in-memory data
  // SPConsentManager.clearAllData() clears all data from UserDefaults, but SPCoordinator
  // keeps a copy of it in-memory. When calling `getUserData` right after, returns its
  // in-memory copy.
  public func clearLocalData() {
    SPConsentManager.clearAllData()
  }

  public func loadGDPRPrivacyManager(_ pmId: String) {
    consentManager?.loadGDPRPrivacyManager(withId: pmId)
  }

  public func loadUSNatPrivacyManager(_ pmId: String) {
    consentManager?.loadUSNatPrivacyManager(withId: pmId)
  }

  public func loadGlobalCMPPrivacyManager(_ pmId: String) {
    consentManager?.loadGlobalCmpPrivacyManager(withId: pmId)
  }

  public func loadPreferenceCenter(_ id: String) {
    consentManager?.loadPreferenceCenter(withId: id)
  }

  public func postCustomConsentGDPR(_ vendors: [String], _ categories: [String], _ legIntCategories: [String], _ callback: @escaping RCTResponseSenderBlock) {
    consentManager?.customConsentGDPR(vendors: vendors, categories: categories, legIntCategories: legIntCategories) { consents in
      callback([RNSPGDPRConsent(from: consents)])
    }
  }

  public func postDeleteCustomConsentGDPR(_ vendors: [String], _ categories: [String], _ legIntCategories: [String], _ callback: @escaping RCTResponseSenderBlock) {
    consentManager?.deleteCustomConsentGDPR(vendors: vendors, categories: categories, legIntCategories: legIntCategories) { consents in
      callback([RNSPGDPRConsent(from: consents)])
    }
  }

  weak var rootViewController: UIViewController? {
    UIApplication.shared.delegate?.window??.rootViewController
  }

  public func build(
    accountId: Int,
    propertyId: Int,
    propertyName: String,
    campaigns: RNSPCampaigns,
    options: RNBuildOptions,
    delegate: ReactNativeCmpImplDelegate?
  ) {
    let manager = SPConsentManager(
      accountId: accountId,
      propertyId: propertyId,
      propertyName: try! SPPropertyName(propertyName),
      campaigns: campaigns.toSP(),
      delegate: objcDelegate
    )
    self.delegate = delegate
    manager.messageLanguage = SPMessageLanguage.init(rawValue: options.language) ?? .English
    manager.messageTimeoutInSeconds = TimeInterval(options.messageTimeout)
    Self.shared?.consentManager = manager
  }

  public func onAction(_ action: SPAction, from controller: UIViewController) {
    delegate?.onAction(RNAction(type: RNSourcepointActionType(from: action.type), customActionId: action.customActionId))
  }

  public func onSPUIReady(_ controller: UIViewController) {
    controller.modalPresentationStyle = .overFullScreen
    rootViewController?.present(controller, animated: true)
    delegate?.onSPUIReady()
  }

  public func onSPUIFinished(_ controller: UIViewController) {
    rootViewController?.dismiss(animated: true)
    delegate?.onSPUIFinished()
  }

  public func onSPFinished(userData: SPUserData) {
    delegate?.onFinished()
  }

  public func onMessageInactivityTimeout() {
    delegate?.onMessageInactivityTimeout()
  }

  public func onError(error: SPError) {
    print("Something went wrong", error)
    delegate?.onError(description: error.description)
  }

  public func dismissMessage() {
    consentManager?.dismissMessage()
  }
}

private class CMPDelegateHandler: NSObject, SPDelegate {
  weak var parent: ReactNativeCmpImpl?

  init(parent: ReactNativeCmpImpl?) {
    self.parent = parent
  }

  func onAction(_ action: ConsentViewController.SPAction, from controller: UIViewController) {
    parent?.onAction(action, from: controller)
  }

  func onSPUIReady(_ controller: UIViewController) {
    parent?.onSPUIReady(controller)
  }

  func onSPUIFinished(_ controller: UIViewController) {
    parent?.onSPUIFinished(controller)
  }

  func onSPFinished(userData: SPUserData) {
    parent?.onSPFinished(userData: userData)
  }

  func onMessageInactivityTimeout() {
    parent?.onMessageInactivityTimeout()
  }

  func onError(error: SPError) {
    parent?.onError(error: error)
  }
}
