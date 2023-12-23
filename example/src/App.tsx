import React, { useState, useEffect, useCallback } from 'react'
import { View, Text, SafeAreaView, Button, StyleSheet } from 'react-native'

import { SPConsentManager } from '@sourcepoint/react-native-cmp'

import UserDataView from './UserDataView'

enum SDKStatus {
  NotStarted = 'Not Started',
  Running = 'Running',
  Finished = 'Finished',
  Errored = 'Errored',
}

const config = {
  accountId: 22,
  propertyId: 16893,
  propertyName: 'mobile.multicampaign.demo',
  gdprPMId: '488393',
  ccpaPMId: '509688',
}

const consentManager = new SPConsentManager()
consentManager.build(config.accountId, config.propertyId, config.propertyName)

export default function App() {
  const [userData, setUserData] = useState<Record<string, unknown>>({})
  const [sdkStatus, setSDKStatus] = useState<SDKStatus>(SDKStatus.NotStarted)

  useEffect(() => {
    consentManager.onFinished(() => {
      setSDKStatus(SDKStatus.Finished)
      consentManager.getUserData().then(setUserData)
    })
    consentManager.onAction((actionType) => console.log(actionType))
    consentManager.onError((description) => {
      setSDKStatus(SDKStatus.Errored)
      console.error(description)
    })
    consentManager.getUserData().then(setUserData)
    consentManager.loadMessage()
    setSDKStatus(SDKStatus.Running)
    return () => {
      consentManager.dispose()
    }
  }, [])

  const onLoadMessagePress = useCallback(() => {
    consentManager.loadMessage()
    setSDKStatus(SDKStatus.Running)
  }, [])

  const onGDPRPMPress = useCallback(() => {
    setSDKStatus(SDKStatus.Running)
    consentManager.loadGDPRPrivacyManager(config.gdprPMId)
  }, [])

  const onCCPAPMPress = useCallback(() => {
    setSDKStatus(SDKStatus.Running)
    consentManager.loadCCPAPrivacyManager(config.ccpaPMId)
  }, [])

  const onClearDataPress = useCallback(() => {
    consentManager.clearLocalData()
    consentManager.getUserData().then(setUserData)
  }, [])

  const disable = sdkStatus === SDKStatus.Running

  return (
    <SafeAreaView style={styles.container}>
      <View>
        <Text style={styles.title}>Sourcepoint CMP</Text>
        <Button
          title="Load Messages"
          onPress={onLoadMessagePress}
          disabled={disable}
        />
        <Button
          title="Load GDPR PM"
          onPress={onGDPRPMPress}
          disabled={disable}
        />
        <Button
          title="Load CCPA PM"
          onPress={onCCPAPMPress}
          disabled={disable}
        />
        <Button title="Clear All" onPress={onClearDataPress} />
        <Text style={styles.status}>{sdkStatus}</Text>
      </View>
      <UserDataView data={userData} />
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  title: {
    textAlign: 'center',
    fontSize: 20,
  },
  status: {
    textAlign: 'center',
    color: '#999',
  },
})
