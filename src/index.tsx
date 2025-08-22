import type {
  Spec,
  SPCampaigns,
  SPUserData,
  LoadMessageParams,
  SPAction,
  SPBuildOptions,
  GDPRConsent,
} from './NativeReactNativeCmp';
import ReactNativeCmp, { SPMessageLanguage } from './NativeReactNativeCmp';
import type { EventEmitter } from 'react-native/Libraries/Types/CodegenTypes';

export * from './NativeReactNativeCmp';

const defaultBuildOptions: SPBuildOptions = {
  language: SPMessageLanguage.ENGLISH,
  messageTimeoutInSeconds: 30,
}

export default class SPConsentManager implements Spec {
  build(
    accountId: number,
    propertyId: number,
    propertyName: string,
    campaigns: SPCampaigns,
    options: SPBuildOptions = defaultBuildOptions,
  ) {
    ReactNativeCmp.build(accountId, propertyId, propertyName, campaigns, options);
  }

  getUserData(): Promise<SPUserData> {
    return ReactNativeCmp.getUserData();
  }

  loadMessage(params?: LoadMessageParams) {
    ReactNativeCmp.loadMessage(params);
  }

  clearLocalData() {
    ReactNativeCmp.clearLocalData();
  }

  loadGDPRPrivacyManager(pmId: string) {
    ReactNativeCmp.loadGDPRPrivacyManager(pmId);
  }

  loadUSNatPrivacyManager(pmId: string) {
    ReactNativeCmp.loadUSNatPrivacyManager(pmId);
  }

  loadGlobalCmpPrivacyManager(pmId: string) {
    ReactNativeCmp.loadGlobalCmpPrivacyManager(pmId);
  }

  loadPreferenceCenter(id: string) {
    ReactNativeCmp.loadPreferenceCenter(id);
  }

  dismissMessage(): void {
    ReactNativeCmp.dismissMessage();
  }

  postCustomConsentGDPR(
    vendors: string[],
    categories: string[],
    legIntCategories: string[],
    callback: (consent: GDPRConsent) => void
  ) {
    ReactNativeCmp.postCustomConsentGDPR(vendors, categories, legIntCategories, callback);
  }

  postDeleteCustomConsentGDPR(
    vendors: string[],
    categories: string[],
    legIntCategories: string[],
    callback: (consent: GDPRConsent) => void
  ) {
    ReactNativeCmp.postDeleteCustomConsentGDPR(vendors, categories, legIntCategories, callback);
  }

  onAction: EventEmitter<SPAction> = ReactNativeCmp.onAction;
  onActionSimplified: EventEmitter<string> = ReactNativeCmp.onActionSimplified;
  onSPUIReady: EventEmitter<void> = ReactNativeCmp.onSPUIReady;
  onSPUIFinished: EventEmitter<void> = ReactNativeCmp.onSPUIFinished;
  onFinished: EventEmitter<void> = ReactNativeCmp.onFinished;
  onMessageInactivityTimeout: EventEmitter<void> = ReactNativeCmp.onMessageInactivityTimeout;
  onError: EventEmitter<{ description: string }> = ReactNativeCmp.onError;
}
