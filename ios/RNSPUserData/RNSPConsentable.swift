import ConsentViewController

struct RNSPConsentable: Encodable {
  let id: String
  let consented: Bool
}

extension RNSPConsentable {
  init(from consentable: SPConsentable) {
    id = consentable.id
    consented = consentable.consented
  }
}
