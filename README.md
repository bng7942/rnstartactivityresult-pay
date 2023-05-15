# react-native-start-activity-for-result

React Native implementation for Android's native `startActivityForResult()` and `onActivityResult()` methods. This can be used for integrations with other apps that require using the previously mentioned methods.

##  Usage

```
import startActivityForResult from 'react-native-start-activity-for-result';

startActivityForResult(returnKey, options);
```

## Example

```javascript
import startActivityForResult from 'react-native-start-activity-for-result';

export const openExternalAppAndGetToken = async (uri: string, extra: Object) => {
    const token = await startActivityForResult('external_app_token', {uri, extra});
    return token;
};
```

## Parameters

| Parameter         | Type          | Description       |
| ----------------- | ------------- | ----------------- |
| returnKey         | string        | The key used for getting data back from the launched app. The launched app has to use `putExtra()` method when returning data. More information [here](https://developer.android.com/reference/android/content/Intent#putExtra(java.lang.String,%20android.os.Parcelable)) |
| options           | Object        | Options that can be passed to the lanched intent (below more information) |
| options.action    | string        | The used intent action (default value is `Intent.ACTION_VIEW` = "android.intent.action.VIEW"). See more information [here](https://developer.android.com/reference/android/content/Intent#standard-activity-actions). |
| options.uri       | [Uri](https://developer.android.com/reference/android/net/Uri) | The data passed for the intent (uses [`Intent.setData(Uri data)`](https://developer.android.com/reference/android/content/Intent#setData(android.net.Uri))). |
| options.extra     | Object        | All of the extras that are passed for the intent (uses [`Intent.putExtras(Bundle extras)`](https://developer.android.com/reference/android/content/Intent#putExtras(android.os.Bundle))). |

More information about Android intents and their structure [here](https://developer.android.com/reference/android/content/Intent#intent-structure).