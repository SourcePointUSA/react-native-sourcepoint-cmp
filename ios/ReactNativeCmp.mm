#import "ReactNativeCmp.h"

#if __has_include(<ReactNativeCmp/ReactNativeCmp-Swift.h>)
  #import <ReactNativeCmp/ReactNativeCmp-Swift.h>
#else
  #import "ReactNativeCmp-Swift.h"
#endif

@implementation ReactNativeCmp {
  ReactNativeCmpImpl *sdk;
}
RCT_EXPORT_MODULE(ReactNativeCmpImpl)

- (void)build:(double)accountId propertyId:(double)propertyId propertyName:(nonnull NSString *)propertyName campaigns:(JS::NativeReactNativeCmp::SPCampaigns &)campaigns options:(JS::NativeReactNativeCmp::SPBuildOptions &)options {
  if (sdk == nil) {
    sdk = [[ReactNativeCmpImpl alloc] init];
  }

  RNSPCampaign *gdpr = nil;
  RNSPCampaign *usnat = nil;
  RNSPCampaign *preferences = nil;
  RNSPCampaign *globalcmp = nil;

  if (campaigns.gdpr().has_value()) {
    auto gdprCampaign = campaigns.gdpr().value();
    NSDictionary *targetingParams = (NSDictionary *)gdprCampaign.targetingParams();

    gdpr = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                               groupPmId:gdprCampaign.groupPmId()
                                  supportLegacyUSPString:false];
  }

  if (campaigns.usnat().has_value()) {
    auto usnatCampaign = campaigns.usnat().value();
    NSDictionary *targetingParams = (NSDictionary *)usnatCampaign.targetingParams();
    BOOL legacy = usnatCampaign.supportLegacyUSPString().value_or(false);

    usnat = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                                groupPmId:usnatCampaign.groupPmId()
                                   supportLegacyUSPString:legacy];
  }

  if (campaigns.preferences().has_value()) {
    auto preferencesCampaign = campaigns.preferences().value();
    NSDictionary *targetingParams = (NSDictionary *)preferencesCampaign.targetingParams();

    preferences = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                                      groupPmId:preferencesCampaign.groupPmId()
                                         supportLegacyUSPString:false];
  }

  if (campaigns.globalcmp().has_value()) {
    auto globalCMPCampaign = campaigns.globalcmp().value();
    NSDictionary *targetingParams = (NSDictionary *)globalCMPCampaign.targetingParams();

    globalcmp = [[RNSPCampaign alloc] initWithTargetingParams:targetingParams ?: @{}
                                                      groupPmId:globalCMPCampaign.groupPmId()
                                         supportLegacyUSPString:false];
  }

  RNSPCampaigns *internalCampaigns = [[RNSPCampaigns alloc] initWithGdpr: gdpr
                                                                    ccpa:nil
                                                                   usnat:usnat
                                                                   ios14:nil
                                                             preferences:preferences
                                                             globalcmp: globalcmp
                                                             environment:RNSPCampaignEnvPublic];

  RNBuildOptions *buildOptions = [
    [RNBuildOptions alloc]
      initWithLanguage: options.language()
      messageTimeout: options.messageTimeoutInSeconds().has_value() ? (NSInteger)options.messageTimeoutInSeconds().value(): 30
  ];

  [sdk
   buildWithAccountId:(NSInteger)accountId
   propertyId:(NSInteger)propertyId
   propertyName:propertyName
   campaigns: internalCampaigns
   options: buildOptions
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
  [sdk getUserData:resolve reject:reject];
}

- (void)loadGDPRPrivacyManager:(nonnull NSString *)pmId {
  [sdk loadGDPRPrivacyManager: pmId];
}

- (void)loadUSNatPrivacyManager:(nonnull NSString *)pmId {
  [sdk loadUSNatPrivacyManager: pmId];
}

- (void)loadGlobalCmpPrivacyManager:(nonnull NSString *)pmId { 
  [sdk loadGlobalCMPPrivacyManager: pmId];
}

- (void)loadPreferenceCenter:(nonnull NSString *)id { 
  [sdk loadPreferenceCenter: id];
}

- (void)dismissMessage { 
  [sdk dismissMessage];
}

- (void)postCustomConsentGDPR:(NSArray *)vendors
               categories:(NSArray *)categories
         legIntCategories:(NSArray *)legIntCategories
                 callback:(RCTResponseSenderBlock)callback {
  [sdk postCustomConsentGDPR:vendors :categories :legIntCategories :callback];
}

- (void)postDeleteCustomConsentGDPR:(NSArray *)vendors
                     categories:(NSArray *)categories
               legIntCategories:(NSArray *)legIntCategories
                       callback:(RCTResponseSenderBlock)callback{
  [sdk postDeleteCustomConsentGDPR:vendors :categories :legIntCategories :callback];
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

- (void)onMessageInactivityTimeout {
  [self emitOnMessageInactivityTimeout];
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
