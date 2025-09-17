import type {
  Spec,
  SPCampaigns,
  SPUserData,
  LoadMessageParams,
  SPAction,
  SPBuildOptions,
  GDPRConsent,
  SPCampaignType,
  SPError
} from './NativeReactNativeCmp';
import ReactNativeCmp, { SPMessageLanguage } from './NativeReactNativeCmp';
import type { EventEmitter } from 'react-native/Libraries/Types/CodegenTypes';

export * from './NativeReactNativeCmp';

const defaultBuildOptions: SPBuildOptions = {
  language: SPMessageLanguage.ENGLISH,
  messageTimeoutInSeconds: 30,
}

export default class SPConsentManager implements Spec {
  /** intended to be used by the SDK only */
  internalOnAction: EventEmitter<string> = ReactNativeCmp.internalOnAction;
  /** intended to be used by the SDK only */
  internalOnError: EventEmitter<string> = ReactNativeCmp.internalOnError;

  onSPUIReady: EventEmitter<void> = ReactNativeCmp.onSPUIReady;
  onSPUIFinished: EventEmitter<void> = ReactNativeCmp.onSPUIFinished;
  onFinished: EventEmitter<void> = ReactNativeCmp.onFinished;
  onMessageInactivityTimeout: EventEmitter<void> = ReactNativeCmp.onMessageInactivityTimeout;

  onAction(handler: (action: SPAction) => void) {
    ReactNativeCmp.internalOnAction((stringifiedAction) => {
      handler(JSON.parse(stringifiedAction) as SPAction);
    });
  }

  onError(handler: (error: SPError) => void) {
    ReactNativeCmp.internalOnError((stringifiedError) => {
      handler(JSON.parse(stringifiedError) as SPError);
    });
  }

  getConstants?(): {} {
    throw new Error('Method not implemented.');
  }

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

  loadMessage(params: LoadMessageParams = { authId: undefined }) {
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

  rejectAll(campaignType: SPCampaignType) {
    ReactNativeCmp.rejectAll(campaignType)
  }
}
