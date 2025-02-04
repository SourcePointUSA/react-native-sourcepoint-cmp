
#ifdef RCT_NEW_ARCH_ENABLED
#import "Spec.h"

@interface RNSourcepointCmp : NSObject <Spec>
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RNSourcepointCmp, RCTEventEmitter)

RCT_EXTERN_METHOD(build:(int)accountId propertyId:(int)propertyId propertyName:(NSString *)propertyName campaigns:(SPCampaigns*)campaigns messageLanguage:(NSString *)messageLanguage)

RCT_EXTERN_METHOD(loadMessage: (SPLoadMessageParams *)params)
RCT_EXTERN_METHOD(clearLocalData)
RCT_EXTERN_METHOD(loadGDPRPrivacyManager:(NSString *)pmId)
RCT_EXTERN_METHOD(loadUSNatPrivacyManager:(NSString *)pmId)

RCT_EXTERN_METHOD(getUserData: (RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(supportedEvents)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
(const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeSpecJSI>(params);
}
#endif

@end
#endif

