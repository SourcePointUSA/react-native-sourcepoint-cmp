import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sourcepoint-cmp' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const SourcepointCmp = NativeModules.SourcepointCmp !== undefined ?
  NativeModules.SourcepointCmp :
  new Proxy({}, {
    get() {
      throw new Error(LINKING_ERROR);
    },
  });
export class SPConsentManager {
  accountId: number;
  propId: number;
  propName: string;
  emitter: NativeEventEmitter

  constructor (accountId: number, propId: number, propName: string) {
    SourcepointCmp.build(accountId, propId, propName)
    this.emitter = new NativeEventEmitter(SourcepointCmp)
    this.accountId = accountId;
    this.propId = propId;
    this.propName = propName;
  }

  onFinished(callback: () => void) {
    this.emitter.removeAllListeners('onSPFinished')
    this.emitter.addListener('onSPFinished', callback);
  }

  getUserData(): Promise<Record<string, unknown>> {
    return SourcepointCmp.getUserData();
  }

  loadMessage() {
    SourcepointCmp.loadMessage();
  }

  clearLocalData() {
    SourcepointCmp.clearLocalData();
  }

  loadGDPRPrivacyManager(pmId: string) {
    SourcepointCmp.loadGDPRPrivacyManager(pmId);
  }

  loadCCPAPrivacyManager(pmId: string) {
    SourcepointCmp.loadCCPAPrivacyManager(pmId);
  }
}
