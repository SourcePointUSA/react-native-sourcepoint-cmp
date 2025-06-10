#import "ReactNativeCmp.h"
#import "ReactNativeCmp-Swift.h"

@implementation ReactNativeCmp {
  ReactNativeCmpImpl *sdk;
}
RCT_EXPORT_MODULE(ReactNativeCmpImpl)

- (void)build:(double)accountId propertyId:(double)propertyId propertyName:(nonnull NSString *)propertyName campaigns:(JS::NativeReactNativeCmp::SPCampaigns &)campaigns {
  if (sdk == nil) {
    sdk = [[ReactNativeCmpImpl alloc] init];
  }

  RNSPCampaigns *internalCampaigns = [[RNSPCampaigns alloc] initWithGdpr: [[RNSPCampaign alloc] initWithTargetingParams:nil groupPmId:nil supportLegacyUSPString:false]
                                                                    ccpa:nil
                                                                   usnat:nil
                                                                   ios14:nil
                                                             environment:RNSPCampaignEnvPublic];

  [sdk
   build:(NSInteger)accountId
   propertyId:(NSInteger)propertyId
   propertyName:propertyName
   campaigns: internalCampaigns
   delegate: self
  ];
}

- (void)loadMessage:(JS::NativeReactNativeCmp::LoadMessageParams &)params {
  [sdk loadMessage: [[RNSPLoadMessageParams alloc] initWithAuthId:params.authId()]];
}

- (void)clearLocalData {
  [sdk clearLocalData];
}

- (void)getUserData:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject { 
  [sdk getUserData:resolve reject:nil];
}

- (void)loadGDPRPrivacyManager:(nonnull NSString *)pmId { 
  [sdk loadGDPRPrivacyManager: pmId];
}

- (void)loadUSNatPrivacyManager:(nonnull NSString *)pmId {
  [sdk loadUSNatPrivacyManager: pmId];
}

// MARK: SPDelegate
- (void)onAction:(RNAction*)action {
  [self emitOnAction: [action toDictionary]];
}

- (void)onErrorWithDescription:(NSString * _Nonnull)description { 
  [self emitOnError: @{ @"description": description }];
}

- (void)onFinished { 
  [self emitOnFinished];
}

- (void)onSPUIFinished { 
  [self emitOnSPUIFinished];
}

- (void)onSPUIReady { 
  [self emitOnSPUIReady];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeReactNativeCmpSpecJSI>(params);
}
@end
