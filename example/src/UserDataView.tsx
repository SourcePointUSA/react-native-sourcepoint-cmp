import * as React from 'react'
import { ScrollView, View, Text, StyleSheet } from 'react-native'
import JSONTree from 'react-native-json-tree'

import { TestableText } from './TestableText'

export default ({ data }: UserDataViewProps) => (
  <View style={styles.container}>
    <Text style={styles.header}>Local User Data</Text>
    <TestableText testID="gdpr.uuid">{data?.gdpr?.consents?.uuid}</TestableText>
    <TestableText testID="ccpa.uuid">{data?.ccpa?.consents?.uuid}</TestableText>
    <ScrollView>
      <ScrollView horizontal>
        <JSONTree
          data={data}
          theme={theme}
          hideRoot
          shouldExpandNode={jsonExpandNodes}
          sortObjectKeys
        />
      </ScrollView>
    </ScrollView>
  </View>
)

const jsonExpandNodes = (_keyName: string, _data: any, level: number) =>
  level <= 2 ? true : false

type UserDataViewProps = {
  data: Record<string | number | symbol, any>
}

const styles = StyleSheet.create({
  container: {
    padding: 10,
  },
  header: {
    fontSize: 18,
  },
})

const theme = {
  scheme: 'default',
  author: 'chris kempson (http://chriskempson.com)',
  base00: '#181818',
  base01: '#282828',
  base02: '#383838',
  base03: '#585858',
  base04: '#b8b8b8',
  base05: '#d8d8d8',
  base06: '#e8e8e8',
  base07: '#f8f8f8',
  base08: '#ab4642',
  base09: '#dc9656',
  base0A: '#f7ca88',
  base0B: '#a1b56c',
  base0C: '#86c1b9',
  base0D: '#7cafc2',
  base0E: '#ba8baf',
  base0F: '#a16946',
}
