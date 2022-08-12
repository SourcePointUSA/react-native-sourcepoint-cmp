import ConsentViewController
import Foundation

@objc(SourcepointCmp)
@objcMembers class SourcepointCmp: RCTEventEmitter {
    @objc public static var shared: SourcepointCmp?

    var consentManager: SPConsentManager?

    override init() {
        super.init()
        SourcepointCmp.shared = self
    }

    open override func supportedEvents() -> [String] {
        ["onSPFinished", "onAction"]
    }

    func getUserData(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(consentManager?.userData.toDictionary() ?? [:])
    }

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
        SourcepointCmp.shared?.sendEvent(withName: "onAction", body: action.type.description)
    }

    func onSPUIReady(_ controller: UIViewController) {
        controller.modalPresentationStyle = .overFullScreen
        rootViewController?.present(controller, animated: true)
    }

    func onSPUIFinished(_ controller: UIViewController) {
        rootViewController?.dismiss(animated: true)
    }

    func onSPFinished(userData: SPUserData) {
        SourcepointCmp.shared?.sendEvent(withName: "onSPFinished", body: [])
    }

    func onError(error: SPError) {
        print("Something went wrong", error)
    }
}
