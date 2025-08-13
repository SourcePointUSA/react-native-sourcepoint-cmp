#import <ReactNativeCmpSpec/ReactNativeCmpSpec.h>
#import <React/RCTEventEmitter.h>

#if __has_include(<ReactNativeCmp/ReactNativeCmp-Swift.h>)
  #import <ReactNativeCmp/ReactNativeCmp-Swift.h>
#else
  #import "ReactNativeCmp-Swift.h"
#endif

@interface ReactNativeCmp : NativeReactNativeCmpSpecBase <NativeReactNativeCmpSpec, ReactNativeCmpImplDelegate>

@end
