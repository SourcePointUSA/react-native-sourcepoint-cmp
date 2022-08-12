import * as React from 'react';
import { StyleSheet, View, ScrollView, SafeAreaView, Button, Text } from 'react-native';
import JSONTree from 'react-native-json-tree';

export default function App({ consentManager, config }) {
  const [userData, setUserData] = React.useState<{}>({});

  React.useEffect(() => {
    consentManager.onFinished(() => {
      consentManager.getUserData().then(setUserData);
    })
    consentManager.loadMessage();
    consentManager.getUserData().then(setUserData);
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.buttonsContainer}>
        <Button title="Load Messages" onPress={() => {
          consentManager.loadMessage();
        }}/>
        <Button title="Load GDPR PM" onPress={() => {
          consentManager.loadGDPRPrivacyManager(config.gdprPMId);
        }}/>
        <Button title="Load CCPA PM" onPress={() => {
          consentManager.loadCCPAPrivacyManager(config.ccpaPMId);
        }}/>
        <Button title="Clear All" onPress={() => {
          consentManager.clearLocalData()
          consentManager.getUserData().then(setUserData)
        }}/>
      </View>
      <ScrollView style={styles.dataContainer} horizontal={true} >
        <ScrollView>
          <JSONTree
            data={userData}
            labelRenderer={([key, ..._]) => <Text style={{ fontWeight: 'bold', fontSize: 16 }}>{key}</Text>}
            valueRenderer={raw => <Text style={{ fontStyle: 'italic', fontSize: 16 }}>{raw}</Text>}
          />
        </ScrollView>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'flex-start',
    padding: 20,
  },
  buttonsContainer: {
    flex: 1,
    alignItems: 'center',
    width: '100%',
    minHeight: 160,
    flexGrow: 0
  },
  dataContainer: {
    flex: 1,
    width: '100%',
    flexGrow: 1
  }
});
