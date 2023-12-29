import { NativeModules, Platform, NativeEventEmitter } from 'react-native'
import type { SourcepointCmpSpec } from './NativeSourcepointCmp'

const LINKING_ERROR =
  `The package '@sourcepoint/react-native-cmp' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n'

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null

const SourcepointCmpModule = isTurboModuleEnabled
  ? require('./NativeSourcepointCmp').default
  : NativeModules.SourcepointCmp

const SourcepointCmp = SourcepointCmpModule
  ? SourcepointCmpModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR)
        },
      }
    )

export class SPConsentManager implements SourcepointCmpSpec {
  emitter = new NativeEventEmitter(SourcepointCmp)

  build(accountId: number, propId: number, propName: string) {
    SourcepointCmp.build(accountId, propId, propName)
  }

  getUserData(): Promise<Record<string, unknown>> {
    return SourcepointCmp.getUserData()
  }

  loadMessage() {
    SourcepointCmp.loadMessage()
  }

  clearLocalData() {
    SourcepointCmp.clearLocalData()
  }

  loadGDPRPrivacyManager(pmId: string) {
    SourcepointCmp.loadGDPRPrivacyManager(pmId)
  }

  loadCCPAPrivacyManager(pmId: string) {
    SourcepointCmp.loadCCPAPrivacyManager(pmId)
  }

  onAction(callback: (action: string) => void): void {
    this.emitter.removeAllListeners('onAction')
    this.emitter.addListener('onAction', callback)
  }

  onSPUIReady(callback: () => void): void {
    this.emitter.removeAllListeners('onSPUIReady')
    this.emitter.addListener('onSPUIReady', callback)
  }

  onSPUIFinished(callback: () => void): void {
    this.emitter.removeAllListeners('onSPUIFinished')
    this.emitter.addListener('onSPUIFinished', callback)
  }

  onFinished(callback: () => void) {
    this.emitter.removeAllListeners('onSPFinished')
    this.emitter.addListener('onSPFinished', callback)
  }

  onError(callback: (description: string) => void): void {
    this.emitter.removeAllListeners('onError')
    this.emitter.addListener('onError', callback)
  }

  dispose(): void {
    SourcepointCmp.supportedEvents()?.forEach(this.emitter.removeAllListeners)
  }
}
