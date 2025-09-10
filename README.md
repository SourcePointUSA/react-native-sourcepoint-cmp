Sourcepoint's React Native package allows you to surface a Sourcepoint CMP message on applications built using the Reactive Native framework.

# Table of Contents

- [Prerequisites](#prerequisites)
- [Install Sourcepoint package](#install-sourcepoint-package)
- [Configuration overview](#configuration-overview)
- [React example](#react-example)
- [Complete app examples](#complete-app-examples)

## Prerequisites

Sourcepoint's React Native SDK v1.0.0+ utilizes and is only compatible with [React Native's new architecture](#https://reactnative.dev/architecture/landing-page). Any projects implementing the latest version of the SDK will need to ensure their project uses the new architecture.

## Install Sourcepoint package

Use the node package manager install command to install the Sourcepoint React Native package:

```sh
npm install @sourcepoint/react-native-cmp
```

## Configuration overview

In order to use the `SPConsentManager` you will need to perform the following:

1. [Instantiate and call build with your configuration](#instantiate-and-call-build-with-your-configuration)
2. [Set up callbacks in instance of `SPConsentManager`](#set-up-callbacks-in-instance-of-spconsentmanager)
3. [Call `loadMessages`](#call-loadmessages)
4. [Retrieve user data with `getUserData`](#retrieve-user-data-with-getuserdata)

In the sections below, we will review each of the steps in more detail:

### Instantiate and call build with your configuration

In your app, you can setup the SPConsent manager in a external file or on your app. In the example below we use `useRef`
to keep a reference of the `SPConsentManager`.

> It is important to notice that we wrap the initialisation of `SPConsentManager` in a `useEffect` and set its reference to `null` to avoid memory leaks.

```ts
const consentManager = useRef<SPConsentManager | null>();

useEffect(() => {
  consentManager.current = new SPConsentManager();
  consentManager.current?.build(
    config.accountId,
    config.propertyId,
    config.propertyName,
    config.campaigns,
    config.buildOptions
  );

  return () => {
    consentManager.current = null;
  };
}
```

The following attributes should be replaced with your organization's details:

| **Attribute**         | **Data Type** | **Description**                                                                                                                                                                                   |
| --------------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `config.accountId`    | Number        | Value associates the property with your organization's Sourcepoint account. Retrieved by contacting your Sourcepoint Account Manager or via the **My Account** page in the Sourcepoint portal.    |
| `config.propertyId`   | Number        | ID for property found in the Sourcepoint portal                                                                                                                                                   |
| `config.propertyName` | String        | Name of property found in the Sourcepoint portal                                                                                                                                                  |
| `config.campaigns`    | Object        | Campaigns launched on the property through the Sourcepoint portal. Accepts `gdpr: {}`, `usnat: {}`, `preferences: {}` and `globalcmp: {}`. See table below for information on each campaign type. |
| `config.buildOptions` | Object?       | Check `SPBuildOptions` type for more information. |

Refer to the table below regarding the different campaigns that can be implemented:

> **NOTE**: Only include the campaign objects for which there is a campaign enabled on the property within the Sourcepoint portal.

| **Campaign object** | **Description**                                                 |
| ------------------- | --------------------------------------------------------------- |
| `gdpr: {}`          | Used if your property runs a GDPR TCF or GDPR Standard campaign |
| `usnat: {}`         | Used if your property runs a U.S. Multi-State Privacy campaign  |
| `preferences: {}`   | Used if your property runs a Preferences campaign               |
| `globalCMP: {}`     | Used if your property runs a Global CMP campaign                |

### Set up callbacks in instance of `SPConsentManager`

`SPConsentManager` communicates with your app through a series of callbacks. Review the table below for available callbacks:

| **Callback**                                     | **Description**                                                                                                                                                                                                                        |
| ------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `onSPUIReady(callback: () => {})`                | Called if the server determines a message should be displayed. The native SDKs will take care of showing the message.                                                                                                                  |
| `onAction(callback: (action: string) => {})`     | Called when the user takes an action (e.g. Accept All) within the consent message. `action: string` is going to be replaced with an enum.                                                                                              |
| `onSPUIFinished(callback: () => {})`             | Called when the native SDKs is done removing the consent UI from the foreground.                                                                                                                                                       |
| `onFinished(callback: () => {})`                 | Called when all UI and network processes are finished. User consent is stored on the local storage of each platform (`UserDefaults` for iOS and `SharedPrefs` for Android). And it is safe to retrieve consent data with `getUserData` |
| `onMessageInactivityTimeout(callback: () => {})` | Called when the user becomes inactive while viewing a consent message. This allows your app to respond to user inactivity events.                                                                                                      |
| `onError(callback: (error: SPError) => {})` | Called if something goes wrong.                                                                                                                                                                                                        |

### Call `loadMessages`

After instantiating and setting up `SPConsentManager` and configuring its callbacks, it is time to call `loadMessages`.

Calling `loadMessages` will initiate the message, contact Sourcepoint's servers, and it may or may not display a message, depending on the scenario configured in the Sourcepoint portal for the property's message campaign.

> This can be done at any stage of your app's lifecycle. Ideally you will want to call it as early as possible, in order to have consent for your vendors.

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

#### `SPUserData`

Is structured by campaign type.

```ts
type SPUserData = {
  gdpr?: GDPRConsent;
  usnat?: USNatConsent;
  preferences?: PreferencesConsent;
  globalcmp?: GlobalCMPConsent;
};
```

`GDPRConsent`:

```ts
type GDPRConsent = {
  applies: boolean;
  uuid?: string;
  expirationDate?: string;
  createdDate?: string;
  euconsent?: string;
  vendorGrants: { [key: string]: GDPRVendorGrant };
  statuses?: GDPRConsentStatus;
  tcfData?: { [key: string]: string };
};
```

`USNatConsent`:

```ts
type USNatConsent = {
  applies: boolean;
  uuid?: string;
  expirationDate?: string;
  createdDate?: string;
  consentSections: Array<ConsentSection>;
  statuses?: USNatConsentStatus;
  vendors: Array<Consentable>;
  categories: Array<Consentable>;
  gppData?: { [key: string]: string };
};
```

`PreferencesConsent`:

```ts
type PreferencesConsent = {
  dateCreated: string;
  uuid?: string;
  status: PreferencesStatus[];
  rejectedStatus: PreferencesStatus[];
};
```

| **`PreferencesConsent` Property** | **Data Type** | **Description**                                                                                                                              |
| --------------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `dateCreated`                     | String        | Date the end-user preferences record was created.                                                                                            |
| `uuid`                            | String        | Unique user ID generated for end-user for which preferences record is stored against.                                                        |
| `status`                          | Array         | An array of objects for each category that has been accepted by the end user. Please review the structure of this object in the table below. |
| `rejectedStatus`                  | Array         | An array of objects for each category that has been rejected by the end user. Please review the structure of this object in the table below. |

The following table details the structure of category objects that can be returned in the `status` or `rejectedStatus` array.

| **Property**    | **Data Type** | **Description**                                                                                                                                                                                                                                                                                                                            |
| --------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `categoryId`    | Number        | ID of category as found in the Preferences configuration                                                                                                                                                                                                                                                                                   |
| `channels`      | Array         | Returned when the category is a Marketing Preference category. Object `{id: number, status: boolean}` represents the channel accepted/rejected by the end-user. Available responses for `id` are: 0 = Email, 1 = SMS, 2 = Whatsapp, 3 = Phone, 4 = Mail, 5 = Test.<br><br> `status` reflects whether the end-user has accepted the channel |
| `changed`       | Boolean       | For internal purposes only.                                                                                                                                                                                                                                                                                                                |
| `dateConsented` | String        | Date end-user made their decision on the Marketing Preference or Legal Preference category                                                                                                                                                                                                                                                 |
| `subType`       | String        | Returned when the category is a Legal Preference category and reflects the **Document Type** selected for the Legal Preference category.                                                                                                                                                                                                   |

`GlobalCMPConsent`:

```ts
type GlobalCMPConsent = {
  applies: boolean;
  uuid?: string;
  expirationDate?: string;
  createdDate?: string;
  vendors: Array<Consentable>;
  categories: Array<Consentable>;
};
```

| **Property**     | **Data Type** | **Description**                                                                                                                                                                                                                                                  |
| ---------------- | ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `applies`        | Boolean       | `true`: end-user's region is in the framework territories \| `false`: end-user's region is not in the framework territories                                                                                                                                      |
| `uuid`           | String        | Unique user ID generated for end-user for which Global Enterprise consent record is stored against.                                                                                                                                                              |
| `expirationDate` | String        | Date the end-user's consent record expires.                                                                                                                                                                                                                      |
| `createdDate`    | String        | Date the end-user's consent record was created.                                                                                                                                                                                                                  |
| `vendors`        | Array         | Vendors configured in your Global Enterprise vendor list and their consent status. `{id: string, consented: boolean}` <br><br> `id` is the ID of the privacy choice. <br><br> `consented` refers to whetherthe privacy choice was opted into by the end-user     |
| `categories`     | Array         | Privacy choices that are applicable to the end-users region and opt-in status for each. `{id: string, consented: boolean}`.<br><br> `id` is the ID of the privacy choice.<br><br> `consented` refers to whetherthe privacy choice was opted into by the end-user |

## React example

In the example below, you can find a fully configured example in React:

```jsx
import React, { useState, useEffect, useRef } from 'react';
import { View, Text, SafeAreaView, SPMessageLanguage } from 'react-native';

import SPConsentManager, { SPCampaignEnvironment, SPUserData } from '@sourcepoint/react-native-cmp';

export default function App() {
  const [userData, setUserData] = useState<SPUserData>({});
  const consentManager = useRef<SPConsentManager | null>(null);

  useEffect(() => {
    // setup
    consentManager.current = new SPConsentManager();
    consentManager.current?.build(
      22,                           // account id
      16893,                        // property id
      "mobile.multicampaign.demo",  // property name
      {                             // campaigns used by your property
        // add only the campaigns your property uses
        gdpr: {},
        // usnat: {},
        // preferences: {},
        // globalcmp: {}
      },
      {
        // in order to override the message language, make sure the option "Use Browser Default"
        // is disabled in the Sourcepoint dashboard
        language: SPMessageLanguage.ENGLISH,
        messageTimeoutInSeconds: 20,
        // Allows Android users to dismiss the consent message on back press. True by default. Set it to false if you wish to prevent this users from dismissing the message on back press.
        androidDismissMessageOnBackPress: true,
      }
    );

    // configure callbacks
    consentManager.current?.onSPUIReady(() => {
      console.log("Consent UI is ready and will be presented.")
    });
    consentManager.current?.onSPUIFinished(() => {
      console.log("Consent UI is finished and will be dismissed.")
    });
    consentManager.current?.onFinished(() => {
      consentManager.current?.getUserData().then(setUserData);
    });
    consentManager.current?.onAction(({ actionType }) => {
          console.log(`User took action ${actionType}`)
    });
    consentManager.current?.onMessageInactivityTimeout(() => {
      console.log("User became inactive")
    });
    consentManager.current?.onError(console.error)

    consentManager.current?.loadMessage();

    return () => {
      consentManager.current = null;
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

## The `SPError` object
```ts
export type SPErrorName =
  | "Unknown"
  | "NoInternetConnection"
  | "LoadMessagesError"
  | "RenderingAppError"
  | "ReportActionError"
  | "ReportCustomConsentError"
  | "AndroidNoIntentFound"
  | string;

export type SPError = {
  name: SPErrorName;
  description: string;
  campaignType?: SPCampaignType;
};
```
Notice `campaignType` is optional. Not all errors cases contain that information and on Android that data is not provided by the native SDK.

## Complete App example

Complete app example for iOS and Android can be found in the [`/example`](/example/) folder of the SDK.

## License

MIT
