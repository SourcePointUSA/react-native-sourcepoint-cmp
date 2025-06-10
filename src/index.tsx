import type {
  Spec,
  SPCampaigns,
  SPUserData,
  LoadMessageParams,
  SPAction,
} from './NativeReactNativeCmp';
import ReactNativeCmp from './NativeReactNativeCmp';
import type { EventEmitter } from 'react-native/Libraries/Types/CodegenTypes';

export * from './NativeReactNativeCmp';

export default class SPConsentManager implements Spec {
  build(
    accountId: number,
    propertyId: number,
    propertyName: string,
    campaigns: SPCampaigns
  ) {
    ReactNativeCmp.build(accountId, propertyId, propertyName, campaigns);
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

  onAction: EventEmitter<SPAction> = ReactNativeCmp.onAction;
  onSPUIReady: EventEmitter<void> = ReactNativeCmp.onSPUIReady;
  onSPUIFinished: EventEmitter<void> = ReactNativeCmp.onSPUIFinished;
  onFinished: EventEmitter<void> = ReactNativeCmp.onFinished;
  onError: EventEmitter<{ description: string }> = ReactNativeCmp.onError;
}
