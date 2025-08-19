package com.sourcepoint.reactnativecmp.arguments

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.MessageLanguage.ENGLISH

data class BuildOptions(
  val language: MessageLanguage,
  val messageTimeoutInSeconds: Long,
  val dismissMessageOnBackPress: Boolean
) {
  val messageTimeoutInMilliseconds = messageTimeoutInSeconds * 1000L

  constructor(options: ReadableMap?) : this(
    language = MessageLanguage.entries.find { it.value == options?.getString("language") } ?: ENGLISH,
    messageTimeoutInSeconds = options?.getDoubleOrNull("messageTimeoutInSeconds")?.toLong() ?: 30L,
    dismissMessageOnBackPress = if (options?.hasKey("dismissMessageOnBackPress") ?: false) options?.getBoolean("dismissMessageOnBackPress") ?: true else true
  )
}
