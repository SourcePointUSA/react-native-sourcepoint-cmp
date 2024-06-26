import * as React from 'react';
import { Text, StyleSheet } from 'react-native';

export type TestableTextProps = {
  testID?: string;
} & React.PropsWithChildren;

export const TestableText = ({ testID, children }: TestableTextProps) => (
  <Text style={styles.container} testID={testID}>
    {children}
  </Text>
);

const styles = StyleSheet.create({
  container: {
    height: 0.01,
  },
});
