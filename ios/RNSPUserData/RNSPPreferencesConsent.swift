import ConsentViewController

struct RNSPPreferencesConsent: Encodable {
  struct Status: Encodable {
    let categoryId: Int
    let channels: [Channel]
    let changed: Bool?
    let dateConsented: SPDate?
    let subType: SubType?
  }

  struct Channel: Encodable {
    let id: Int
    let status: Bool
  }

  enum SubType: String, Encodable {
    case AIPolicy, TermsAndConditions, PrivacyPolicy, LegalPolicy, TermsOfSale, Unknown

    init(from subType: SPPreferencesConsent.SubType) {
      switch subType {
      case .AIPolicy: self = .AIPolicy
      case .TermsAndConditions: self = .TermsAndConditions
      case .PrivacyPolicy: self = .PrivacyPolicy
      case .LegalPolicy: self = .LegalPolicy
      case .TermsOfSale: self = .TermsOfSale
      default: self = .Unknown
      }
    }
  }

  let dateCreated: SPDate
  let uuid: String?
  let status: [Status]
  let rejectedStatus: [Status]
}

extension RNSPPreferencesConsent.Status {
  init(from status: SPPreferencesConsent.Status) {
    categoryId = status.categoryId
    channels = status.channels.map { RNSPPreferencesConsent.Channel(id: $0.id, status: $0.status) }
    changed = status.changed
    dateConsented = status.dateConsented
    subType = status.subType.flatMap { RNSPPreferencesConsent.SubType(from: $0) }
  }
}

extension RNSPPreferencesConsent.Channel {
  init(from channel: SPPreferencesConsent.Channel) {
    id = channel.id
    status = channel.status
  }
}

extension RNSPPreferencesConsent {
  init?(from preferences: SPPreferencesConsent?) {
    guard let preferences = preferences else { return nil }

    self.init(
      dateCreated: preferences.dateCreated,
      uuid: preferences.uuid,
      status: preferences.status.map { Status(from: $0) },
      rejectedStatus: preferences.rejectedStatus.map { Status(from: $0) }
    )
  }
}
