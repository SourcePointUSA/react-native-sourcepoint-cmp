import type { TurboModule } from 'react-native'
import { TurboModuleRegistry } from 'react-native'

export interface SourcepointCmpSpec extends TurboModule {
  build(accountId: number, propId: number, propName: string): void
  getUserData(): Promise<Record<string, unknown>>
  loadMessage(): void
  clearLocalData(): void
  loadGDPRPrivacyManager(pmId: string): void
  loadCCPAPrivacyManager(pmId: string): void

  onFinished(callback: () => void): void
  onAction(callback: (action: string) => void): void
  onSPUIReady(callback: () => void): void
  onSPUIFinished(callback: () => void): void
  onError(callback: (description: string) => void): void

  dispose(): void
}

export default TurboModuleRegistry.getEnforcing<SourcepointCmpSpec>(
  'SourcepointCmp'
)
