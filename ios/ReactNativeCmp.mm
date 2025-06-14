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

  RNSPCampaign *gdpr = nil;
  RNSPCampaign *usnat = nil;

  if (campaigns.gdpr().has_value()) {
    auto gdprCampaign = campaigns.gdpr().value();
    NSDictionary *targetingParams = (NSDictionary *)gdprCampaign.targetingParams();

    gdpr = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                               groupPmId:nil
                                  supportLegacyUSPString:false];
  }

  if (campaigns.usnat().has_value()) {
    auto usnatCampaign = campaigns.usnat().value();
    NSDictionary *targetingParams = (NSDictionary *)usnatCampaign.targetingParams();
    BOOL legacy = usnatCampaign.supportLegacyUSPString().value_or(false);

    usnat = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                                groupPmId:nil
                                   supportLegacyUSPString:legacy];
  }

  RNSPCampaigns *internalCampaigns = [[RNSPCampaigns alloc] initWithGdpr: gdpr
                                                                    ccpa:nil
                                                                   usnat:usnat
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
