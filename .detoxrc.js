/** @type {Detox.DetoxConfig} */
module.exports = {
  testRunner: {
    args: {
      '$0': 'jest',
      config: 'e2e/jest.config.js'
    },
    jest: {
      setupTimeout: 120000
    }
  },
  apps: {
    'ios.debug': {
      start: 'yarn example start',
      type: 'ios.app',
      binaryPath: 'example/ios/build/Build/Products/Debug-iphonesimulator/SourcepointCmpExample.app',
      build: 'xcodebuild -workspace example/ios/SourcepointCmpExample.xcworkspace -scheme SourcepointCmpExample -configuration Debug -sdk iphonesimulator -derivedDataPath example/ios/build'
    },
    'android.debug': {
      start: 'yarn example start',
      type: 'android.apk',
      binaryPath: 'example/android/app/build/outputs/apk/debug/app-debug.apk',
      build: 'cd example/android && ./gradlew assembleDebug assembleAndroidTest -DtestBuildType=debug',
      reversePorts: [8081]
    },
    'ios.release': {
      type: 'ios.app',
      binaryPath: 'example/ios/build/Build/Products/Release-iphonesimulator/SourcepointCmpExample.app',
      build: 'xcodebuild -workspace example/ios/SourcepointCmpExample.xcworkspace -scheme SourcepointCmpExample -configuration Release -sdk iphonesimulator -derivedDataPath example/ios/build'
    },
    'android.release': {
      type: 'android.apk',
      binaryPath: 'example/android/app/build/outputs/apk/release/app-release.apk',
      build: 'cd example/android && ./gradlew assembleRelease assembleAndroidTest -DtestBuildType=release',
      reversePorts: [8081]
    }
  },
  devices: {
    simulator: {
      type: 'ios.simulator',
      device: {
        type: 'iPhone 15'
      }
    },
    emulator: {
      type: 'android.emulator',
      device: {
        avdName: 'Pixel_9_API_34'
      }
    },
    // attached: {
    //   type: 'android.attached',
    //   device: {
    //     adbName: '.*'
    //   }
    // },
  },
  configurations: {
    'ios.sim.debug': {
      device: 'simulator',
      app: 'ios.debug'
    },
    'android.emu.debug': {
      device: 'emulator',
      app: 'android.debug'
    },
    'ios.sim.release': {
      device: 'simulator',
      app: 'ios.release'
    },
    'android.emu.release': {
      device: 'emulator',
      app: 'android.release'
    }
    // 'android.att.debug': {
    //   device: 'attached',
    //   app: 'android.debug'
    // },
    // 'android.att.release': {
    //   device: 'attached',
    //   app: 'android.release'
    // },
  }
};
