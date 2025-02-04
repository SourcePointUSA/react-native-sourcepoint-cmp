import type { TurboModule } from 'react-native';

export type SPCampaign = {
  targetingParams?: Object;
  supportLegacyUSPString?: boolean;
};

export const enum SPCampaignEnvironment {
  Public = 'Public',
  Stage = 'Stage',
}

export const enum SPActionType {
  acceptAll = 'acceptAll',
  rejectAll = 'rejectAll',
  saveAndExit = 'saveAndExit',
  showOptions = 'showOptions',
  dismiss = 'dismiss',
  pmCancel = 'pmCancel',
  unknown = 'unknown',
}

export type SPCampaigns = {
  gdpr?: SPCampaign;
  usnat?: SPCampaign;
  environment?: SPCampaignEnvironment;
};

export interface CampaignConsent<Consent extends ConcreteConsent> {
  applies: boolean;
  consents: Consent;
}

export interface ConcreteConsent {
  uuid?: String;
  // TODO: uncomment once both GDPR and USNAT expose those two attributes
  // expirationDate?: String;
  // createdDate?: String;
}

export type GDPRConsentStatus = {
  consentedAll?: Boolean;
  consentedAny?: Boolean;
  rejectedAny?: Boolean;
};

export type USNatConsentStatus = {
  consentedAll?: Boolean;
  consentedAny?: Boolean;
  rejectedAny?: Boolean;
  sellStatus?: Boolean;
  shareStatus?: Boolean;
  sensitiveDataStatus?: Boolean;
  gpcStatus?: Boolean;
};

export type GDPRVendorGrant = {
  granted: Boolean;
  purposes: Record<string, Boolean>;
};

export type GDPRConsent = {
  uuid?: String;
  // expirationDate?: String;
  // createdDate?: String;
  euconsent?: String;
  vendorGrants: Record<string, GDPRVendorGrant>;
  statuses?: GDPRConsentStatus;
  tcfData?: Object;
};

export type Consentable = {
  consented: Boolean;
  id: String;
};

export type ConsentSection = {
  id: Number;
  name: String;
  consentString: String;
};

export type USNatConsent = {
  uuid?: String;
  // expirationDate?: String;
  // createdDate?: String;
  consentSections: Array<ConsentSection>;
  statuses?: USNatConsentStatus;
  gppData?: Object;
  vendors: [Consentable];
  categories: [Consentable];
};

export type SPUserData = {
  gdpr?: CampaignConsent<GDPRConsent>;
  usnat?: CampaignConsent<USNatConsent>;
};

export type LoadMessageParams = {
  authId?: string;
};

export interface Spec extends TurboModule {
  build(
    accountId: number,
    propertyId: number,
    propertyName: string,
    campaigns: SPCampaigns,
    messageLanguage?: string
  ): void;
  getUserData(): Promise<SPUserData>;
  loadMessage(params?: LoadMessageParams): void;
  clearLocalData(): void;
  loadGDPRPrivacyManager(pmId: string): void;
  loadUSNatPrivacyManager(pmId: string): void;

  onAction(callback: (body: { actionType: SPActionType }) => void): void;
  onSPUIReady(callback: () => void): void;
  onSPUIFinished(callback: () => void): void;
  onFinished(callback: () => void): void;
  onError(callback: (description: string) => void): void;

  dispose(): void;

  addListener(eventName: string): void;
  removeListeners(count: number): void;
}
