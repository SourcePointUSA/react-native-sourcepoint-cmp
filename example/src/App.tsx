import { useState, useEffect, useCallback, useRef } from 'react';
import {
  View,
  Text,
  SafeAreaView,
  Button,
  StyleSheet,
  TextInput,
} from 'react-native';
import { LaunchArguments } from 'react-native-launch-arguments';

import SPConsentManager, {
  SPCampaignEnvironment,
} from '@sourcepoint/react-native-cmp';
import type { SPCampaigns, SPUserData } from '@sourcepoint/react-native-cmp';
import type { LaunchArgs } from './LaunchArgs';

import UserDataView from './UserDataView';

enum SDKStatus {
  NotStarted = 'Not Started',
  Networking = 'Networking',
  Presenting = 'Presenting',
  Finished = 'Finished',
  Errored = 'Errored',
}

const launchArgs = LaunchArguments.value<LaunchArgs>();

const config = {
  accountId: 22,
  propertyId: 16893,
  propertyName: 'mobile.multicampaign.demo',
  gdprPMId: '488393',
  usnatPMId: '988851',
  globalCmpPMId: '1323762',
  preferencesCenterId: '1306779',
  campaigns: {
    gdpr: {},
    usnat: { supportLegacyUSPString: true },
    preferences: {},
    globalcmp: {},
    environment: SPCampaignEnvironment.Public,
  } as SPCampaigns,
  ...launchArgs?.config,
};

export default function App() {
  const [userData, setUserData] = useState<SPUserData>({});
  const [sdkStatus, setSDKStatus] = useState<SDKStatus>(SDKStatus.NotStarted);
  const [authIdInput, setAuthIdInput] = useState(launchArgs.authId);
  const [authId, setAuthId] = useState<string | undefined>(launchArgs.authId);
  const consentManager = useRef<SPConsentManager | null>(null);

  useEffect(() => {
    consentManager.current = new SPConsentManager();
    consentManager.current?.build(
      config.accountId,
      config.propertyId,
      config.propertyName,
      config.campaigns
    );

    if (launchArgs.clearData === true) {
      consentManager.current?.clearLocalData();
    }

    consentManager.current?.onSPUIReady(() => {
      setSDKStatus(SDKStatus.Presenting);
    });

    // consentManager.current?.onSPUIFinished(() => { });

    consentManager.current?.onFinished(() => {
      setSDKStatus(SDKStatus.Finished);
      consentManager.current?.getUserData().then(setUserData);
    });

    consentManager.current?.onAction(({ actionType }) =>
      console.log(`action: ${actionType}`)
    );

    consentManager.current?.onError((description) => {
      setSDKStatus(SDKStatus.Errored);
      console.error(description);
    });

    consentManager.current?.getUserData().then(setUserData);

    consentManager.current?.loadMessage({ authId });

    setSDKStatus(SDKStatus.Networking);

    return () => {
      consentManager.current = null;
    };
  }, [authId]);

  const onLoadMessagePress = useCallback(() => {
    consentManager.current?.loadMessage({ authId });
    setSDKStatus(SDKStatus.Networking);
  }, [authId]);

  const onGDPRPMPress = useCallback(() => {
    setSDKStatus(SDKStatus.Networking);
    consentManager.current?.loadGDPRPrivacyManager(config.gdprPMId);
  }, []);

  const onUSNATPMPress = useCallback(() => {
    setSDKStatus(SDKStatus.Networking);
    consentManager.current?.loadUSNatPrivacyManager(config.usnatPMId);
  }, []);

  const onGlobalCMPPress = useCallback(() => {
    setSDKStatus(SDKStatus.Networking);
    consentManager.current?.loadGlobalCmpPrivacyManager(config.globalCmpPMId);
  }, []);

  const onPreferencesPress = useCallback(() => {
    setSDKStatus(SDKStatus.Networking);
    consentManager.current?.loadPreferenceCenter(config.preferencesCenterId);
  }, []);

  const onClearDataPress = useCallback(() => {
    consentManager.current?.clearLocalData();
    consentManager.current?.build(
      config.accountId,
      config.propertyId,
      config.propertyName,
      config.campaigns
    );
    setUserData({});
  }, []);

  const disable =
    sdkStatus === SDKStatus.Networking || sdkStatus === SDKStatus.Presenting;

  return (
    <SafeAreaView style={styles.container}>
      <View>
        <Text style={styles.title}>Sourcepoint CMP</Text>
        <TextInput
          value={authIdInput}
          placeholder="(AuthId: optional, press enter to submit)"
          onChangeText={setAuthIdInput}
          onSubmitEditing={() => setAuthId(authIdInput)}
          style={styles.authIdInput}
          autoCapitalize="none"
          autoCorrect={false}
          autoComplete="off"
          clearButtonMode="always"
          returnKeyType="done"
        />
        <Button
          title={authId ? `Load Messages (${authId})` : 'Load Messages'}
          onPress={onLoadMessagePress}
          disabled={disable}
        />
        <Button
          title="Load GDPR PM"
          onPress={onGDPRPMPress}
          disabled={disable || config.campaigns.gdpr == undefined}
        />
        <Button
          title="Load USNAT PM"
          onPress={onUSNATPMPress}
          disabled={disable || config.campaigns.usnat == undefined}
        />
        <Button
          title="Load GlobalCMP PM"
          onPress={onGlobalCMPPress}
          disabled={disable || config.campaigns.globalcmp == undefined}
        />
        <Button
          title="Load Preferences Center"
          onPress={onPreferencesPress}
          disabled={disable || config.campaigns.preferences == undefined}
        />
        <Button title="Clear All" onPress={onClearDataPress} />
        <Text testID="sdkStatus" style={styles.status}>
          {sdkStatus}
        </Text>
      </View>
      <UserDataView data={userData} authId={authId} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  title: {
    textAlign: 'center',
    fontSize: 22,
  },
  status: {
    textAlign: 'center',
    color: '#999',
  },
  authIdInput: {
    marginVertical: 12,
    marginHorizontal: 'auto',
    width: '90%',
    padding: 8,
    fontSize: 18,
    textAlign: 'center',
    borderWidth: 1,
    borderColor: '#999',
  },
});
