Sourcepoint's React Native package allows you to surface a Sourcepoint CMP message on applications built using the Reactive Native framework.

# Table of Contents

- [Install Sourcepoint package](#install-sourcepoint-package)
- [Configuration overview](#configuration-overview)
- [React example](#react-example)
- [Complete app examples](#complete-app-examples)

## Install Sourcepoint package

Use the node package manager install command to install the Sourcepoint React Native package:

```sh
npm install @sourcepoint/react-native-cmp
```

## Configuration overview

In order to use the `SPConsentManager`  you will need to perform the following:

1. [Instantiate and call build with your configuration](#instantiate-and-call-build-with-your-configuration)
2. [Set up callbacks in instance of `SPConsentManager`](#set-up-callbacks-in-instance-of-spconsentmanager)
3. [Call `loadMessages`](#call-loadmessages) 
4. [Retrieve user data with `getUserData`](#retrieve-user-data-with-getuserdata)

In the sections below, we will review each of the steps in more detail:

### Instantiate and call build with your configuration

In your app, you can setup the SPConsent manager in a external file or on your app. In the example below we use `useRef`
to keep a reference of the `SPConsentManager`. 

>It is important to notice that we wrap the initialisation of `SPConsentManager` in a `useEffect` and call `consentManager.current?.dispose()` to avoid memory leaks.

```ts
const consentManager = useRef<SPConsentManager | null>();

useEffect(() => {
  consentManager.current = new SPConsentManager();
  consentManager.current?.build(
    config.accountId,
    config.propertyId,
    config.propertyName,
    config.campaigns
  );

  return () => {
    consentManager.current?.dispose();
  };
}
```
The following attributes should be replaced with your organization's details:

| **Attribute**         | **Data Type** | **Description**                                                                                                                                                                                |
|-----------------------|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `config.accountId`    | Number        | Value associates the property with your organization's Sourcepoint account. Retrieved by contacting your Sourcepoint Account Manager or via the **My Account** page in the Sourcepoint portal. |
| `config.propertyId`   | Number        | ID for property found in the Sourcepoint portal                                                                                                                                                |
| `config.propertyName` | String        | Name of property found in the Sourcepoint portal                                                                                                                                               |
| `config.campaigns`    | Object        | Campaigns launched on the property through the Sourcepoint portal. Accepts `gdpr: {}` and/or `usnat: {}`. See table below for information on each campaign type.                               |

Refer to the table below regarding the different campaigns that can be implemented:

>**NOTE**: Only include the campaign objects for which there is a campaign enabled on the property within the Sourcepoint portal.

| **Campaign object** | **Description**                                                 |
|---------------------|-----------------------------------------------------------------|
| `gdpr: {}`          | Used if your property runs a GDPR TCF or GDPR Standard campaign |
| `usnat: {}`         | Used if your property runs a U.S. Multi-State Privacy campaign  |

### Set up callbacks in instance of `SPConsentManager`

`SPConsentManager` communicates with your app through a series of callbacks. Review the table below for available callbacks:

| **Callback**                                     | **Description**                                                                                                                                                                                                                        |
|--------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `onSPUIReady(callback: () => {})`                | Called if the server determines a message should be displayed. The native SDKs will take care of showing the message.                                                                                                                  |
| `onAction(callback: (action: string) => {})`     | Called when the user takes an action (e.g. Accept All) within the consent message. `action: string` is going to be replaced with an enum.                                                                                              |
| `onSPUIFinished(callback: () => {})`             | Called when the native SDKs is done removing the consent UI from the foreground.                                                                                                                                                       |
| `onFinished(callback: () => {})`                 | Called when all UI and network processes are finished. User consent is stored on the local storage of each platform (`UserDefaults` for iOS and `SharedPrefs` for Android). And it is safe to retrieve consent data with `getUserData` |
| `onError(callback: (description: string) => {})` | Called if something goes wrong.                                                                                                                                                                                                        |

### Call `loadMessages`

After instantiating and setting up `SPConsentManager` and configuring its callbacks, it is time to call `loadMessages`. 

Calling `loadMessages` will initiate the message, contact Sourcepoint's servers, and it may or may not display a message, depending on the scenario configured in the Sourcepoint portal for the property's message campaign.

>This can be done at any stage of your app's lifecycle. Ideally you will want to call it as early as possible, in order to have consent for your vendors.

```ts
consentManager.current?.loadMessage();
```

### Retrieve user data with `getUserData`
`getUserData` returns a `Promise<SPUserData>`. You can call this function at any point in your app's lifecycle, but consent may or may not yet be ready. The safest place to call it is inside the callback `onSPFinished`.

```ts
consentManager.current?.onFinished(() => {
      consentManager.current?.getUserData().then(setUserData);
    });
```

## React example

In the example below, you can find a fully configured example in React:
 
```jsx
import React, { useState, useEffect, useRef } from 'react';
import { View, Text, SafeAreaView } from 'react-native';

import { SPConsentManager, SPCampaignEnvironment } from '@sourcepoint/react-native-cmp';

export default function App() {
  const [userData, setUserData] = useState<SPUserData>({});
  const consentManager = useRef<SPConsentManager | null>();

  useEffect(() => {
    // setup
    consentManager.current = new SPConsentManager();
    consentManager.current?.build(
      22,
      16893,
      "mobile.multicampaign.demo",
      {
        gdpr: {},
        // usnat: {} // uncomment this if you have a usnat campaign set up
      }
    );

    // configure callbacks
    consentManager.current?.onSPUIReady(() => {
      console.log("Consent UI is ready to be displayed")
    });
    consentManager.current?.onSPUIFinished(() => {
      console.log("Consent UI is finished")
    });
    consentManager.current?.onFinished(() => {
      consentManager.current?.getUserData().then(setUserData);
    });
    consentManager.current?.onAction((action) => {
      console.log(`User took action ${action}`)
    });
    consentManager.current?.onError(console.error);

    consentManager.current?.loadMessage();

    return () => {
      consentManager.current?.dispose();
    };
  }, []);
  return (
    <SafeAreaView>
      <View>
        <Text>{JSON.stringify(userData, null, 2)}</Text>
      </View>
    </SafeAreaView>
)
```

## Implementing authenticated consent
In a nutshell, you provide an identifier for the current user (username, user id, uuid or any unique string) and we'll take care of associating the consent profile to that identifier.

In order to use the authenticated consent all you need to do is replace `.loadMessage()` with `.loadMessage({ authId: "JohnDoe"}))`.

If our APIs have a consent profile associated with that token `"JohnDoe"` the SDK will bring the consent profile from the server, overwriting whatever was stored in the device. If none is found, the session will be treated as a new user.

## Complete App examples

Complete app examples for iOS and Android can be found in the [`/example`](/example/) folder of the SDK. 

## License

MIT

