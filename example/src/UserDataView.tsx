import { ScrollView, View, Text, StyleSheet } from 'react-native';

import { TestableText } from './TestableText';
import type { SPUserData } from '@sourcepoint/react-native-cmp';

export default ({ data, authId }: UserDataViewProps) => (
  <View style={styles.container}>
    <Text style={styles.header}>
      {authId ? `User Data (${authId})` : `User Data`}
    </Text>
    <TestableText testID="gdpr.uuid">{data?.gdpr?.uuid}</TestableText>
    <TestableText testID="gdpr.consentStatus">
      {data?.gdpr?.statuses?.consentedAll ? 'consentedAll' : 'rejectedAll'}
    </TestableText>
    <TestableText testID="usnat.uuid">{data?.usnat?.uuid}</TestableText>
    <TestableText testID="usnat.consentStatus">
      {data?.usnat?.statuses?.consentedAll ? 'consentedAll' : 'rejectedAll'}
    </TestableText>
    <ScrollView style={styles.container}>
      <Text testID="userData" style={styles.userDataText}>
        {JSON.stringify(data, null, 2)}
      </Text>
    </ScrollView>
  </View>
);

type UserDataViewProps = {
  data: SPUserData;
  authId?: string;
};

const styles = StyleSheet.create({
  container: {
    padding: 8,
  },
  header: {
    fontSize: 18,
  },
  userDataText: {
    fontSize: 10,
    paddingBottom: 300,
  },
});
