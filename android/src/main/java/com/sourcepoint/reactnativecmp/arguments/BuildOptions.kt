package com.sourcepoint.reactnativecmp.arguments

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.MessageLanguage.ENGLISH

data class BuildOptions(
  val language: MessageLanguage,
  val messageTimeoutInSeconds: Long,
  val androidDismissMessageOnBackPress: Boolean
) {
  val messageTimeoutInMilliseconds = messageTimeoutInSeconds * 1000L

  constructor(options: ReadableMap?) : this(
    language = MessageLanguage.entries.find { it.value == options?.getString("language") } ?: ENGLISH,
    messageTimeoutInSeconds = options?.getDoubleOrNull("messageTimeoutInSeconds")?.toLong() ?: 30L,
    androidDismissMessageOnBackPress = options?.getBooleanOrNull("androidDismissMessageOnBackPress") ?: true
  )
}
