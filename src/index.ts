import { NativeModules, Platform, NativeEventEmitter } from 'react-native';
import type {
  Spec,
  SPCampaigns,
  SPUserData,
  LoadMessageParams,
  SPActionType,
} from './types';

import { Language } from './types';

const LINKING_ERROR =
  `The package '@sourcepoint/react-native-cmp' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const RNSourcepointCmpModule = isTurboModuleEnabled
  ? require('./NativeSourcepointCmp').default
  : NativeModules.RNSourcepointCmp;

const RNSourcepointCmp = RNSourcepointCmpModule
  ? RNSourcepointCmpModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export * from './types';

export type * from './types';

export class SPConsentManager implements Spec {
  emitter = new NativeEventEmitter(RNSourcepointCmp);

  build(
    accountId: number,
    propertyId: number,
    propertyName: string,
    campaigns: SPCampaigns,
    messageLanguage?: Language
  ) {
    RNSourcepointCmp.build(accountId, propertyId, propertyName, campaigns, messageLanguage || '');
  }

  getUserData(): Promise<SPUserData> {
    return RNSourcepointCmp.getUserData();
  }

  loadMessage(params?: LoadMessageParams) {
    RNSourcepointCmp.loadMessage(params);
  }

  clearLocalData() {
    RNSourcepointCmp.clearLocalData();
  }

  loadGDPRPrivacyManager(pmId: string) {
    RNSourcepointCmp.loadGDPRPrivacyManager(pmId);
  }

  loadUSNatPrivacyManager(pmId: string) {
    RNSourcepointCmp.loadUSNatPrivacyManager(pmId);
  }

  onAction(
    callback: (body: {
      actionType: SPActionType;
      customActionId: string;
    }) => void
  ): void {
    this.emitter.removeAllListeners('onAction');
    this.emitter.addListener('onAction', callback);
  }

  onSPUIReady(callback: () => void): void {
    this.emitter.removeAllListeners('onSPUIReady');
    this.emitter.addListener('onSPUIReady', callback);
  }

  onSPUIFinished(callback: () => void): void {
    this.emitter.removeAllListeners('onSPUIFinished');
    this.emitter.addListener('onSPUIFinished', callback);
  }

  onFinished(callback: (userData: SPUserData) => void) {
    this.emitter.removeAllListeners('onSPFinished');
    this.emitter.addListener('onSPFinished', callback);
  }

  onError(callback: (description: string) => void): void {
    this.emitter.removeAllListeners('onError');
    this.emitter.addListener('onError', callback);
  }

  dispose(): void {
    RNSourcepointCmp.supportedEvents()?.forEach(
      this.emitter.removeAllListeners
    );
  }

  addListener(_eventName: string): void {}
  removeListeners(_count: number): void {}
  getConstants?(): {};
}
