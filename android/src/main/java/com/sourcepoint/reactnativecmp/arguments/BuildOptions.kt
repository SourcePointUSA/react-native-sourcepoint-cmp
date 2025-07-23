package com.sourcepoint.reactnativecmp.arguments

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.MessageLanguage.ENGLISH

data class BuildOptions(
  val language: MessageLanguage,
  val messageTimeoutInSeconds: Long,
) {
  constructor(options: ReadableMap?) : this(
    language = MessageLanguage.entries.find { it.value == options?.getString("language") } ?: ENGLISH,
    messageTimeoutInSeconds = options?.getLongOrNull("messageTimeoutInSeconds") ?: 30000
  )
}
