package com.sourcepoint.reactnativecmp.arguments

import com.facebook.react.bridge.ReadableMap
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.MessageLanguage.ENGLISH

data class BuildOptions(
  val language: MessageLanguage,
) {
  constructor(options: ReadableMap?) : this(
    language = MessageLanguage.entries.find { it.value == options?.getString("language") } ?: ENGLISH,
  )
}
