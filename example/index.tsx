import * as React from 'react';
import { AppRegistry } from 'react-native';
import App from './src/App';

import { SPConsentManager } from 'react-native-sourcepoint-cmp';

const SPConfig = {
  accountId: 22,
  propertyId: 999,
  propertyName: "mobile.multicampaign.demo",
  gdprPMId: "488393",
  ccpaPMId: "509688"
}

const manager = new SPConsentManager(
  SPConfig.accountId,
  SPConfig.propertyId,
  SPConfig.propertyName
);

const PrivacyCompliantApp = () => <App consentManager={manager} config={SPConfig} />

AppRegistry.registerComponent('main', () => PrivacyCompliantApp);
