import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';
import type { EventEmitter } from 'react-native/Libraries/Types/CodegenTypes';

export type SPCampaign = {
  groupPmId?: string;
  targetingParams?: { [key: string]: string };
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
  preferences?: SPCampaign;
  environment?: SPCampaignEnvironment;
};

export type GDPRConsentStatus = {
  consentedAll?: boolean;
  consentedAny?: boolean;
  rejectedAny?: boolean;
};

export type USNatConsentStatus = {
  consentedAll?: boolean;
  consentedAny?: boolean;
  rejectedAny?: boolean;
  sellStatus?: boolean;
  shareStatus?: boolean;
  sensitiveDataStatus?: boolean;
  gpcStatus?: boolean;
};

export type GDPRVendorGrant = {
  granted: boolean;
  purposes: { [key: string]: boolean };
};

export type GDPRConsent = {
  applies: boolean;
  uuid?: string;
  expirationDate?: string;
  createdDate?: string;
  euconsent?: string;
  vendorGrants: { [key: string]: GDPRVendorGrant };
  statuses?: GDPRConsentStatus;
  tcfData?: { [key: string]: string };
};

export type Consentable = {
  consented: boolean;
  id: string;
};

export type ConsentSection = {
  id: number;
  name: string;
  consentString: string;
};

export type USNatConsent = {
  applies: boolean;
  uuid?: string;
  expirationDate?: string;
  createdDate?: string;
  consentSections: Array<ConsentSection>;
  statuses?: USNatConsentStatus;
  vendors: Array<Consentable>;
  categories: Array<Consentable>;
  gppData?: { [key: string]: string };
};

export type SPUserData = {
  gdpr?: GDPRConsent;
  usnat?: USNatConsent;
  preferences?: PreferencesConsent;
};

export type LoadMessageParams = {
  authId?: string;
};

export type SPAction = {
  actionType: SPActionType;
  customActionId?: string;
};

export const enum PreferencesSubType {
  AIPolicy = 'AIPolicy',
  TermsAndConditions = 'TermsAndConditions',
  PrivacyPolicy = 'PrivacyPolicy',
  LegalPolicy = 'LegalPolicy',
  TermsOfSale = 'TermsOfSale',
  Unknown = 'Unknown',
}

export type PreferencesChannel = {
  id: number;
  status: boolean;
};

export type PreferencesStatus = {
  categoryId: number;
  channels: PreferencesChannel[];
  changed?: boolean;
  dateConsented?: string;
  subType?: PreferencesSubType;
};

export type PreferencesConsent = {
  dateCreated: string;
  uuid?: string;
  status: PreferencesStatus[];
  rejectedStatus: PreferencesStatus[];
};

export interface Spec extends TurboModule {
  build(
    accountId: number,
    propertyId: number,
    propertyName: string,
    campaigns: SPCampaigns
  ): void;
  getUserData(): Promise<SPUserData>;
  loadMessage(params?: LoadMessageParams): void;
  clearLocalData(): void;
  loadGDPRPrivacyManager(pmId: string): void;
  loadUSNatPrivacyManager(pmId: string): void;

  readonly onAction: EventEmitter<SPAction>;
  readonly onSPUIReady: EventEmitter<void>;
  readonly onSPUIFinished: EventEmitter<void>;
  readonly onFinished: EventEmitter<void>;
  readonly onError: EventEmitter<{ description: string }>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ReactNativeCmp');
