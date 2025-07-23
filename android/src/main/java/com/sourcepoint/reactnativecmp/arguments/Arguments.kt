package com.sourcepoint.reactnativecmp.arguments

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.ReadableArray
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

fun WritableArray.pushAny(value: Any?) {
  when (value) {
    is Int -> pushInt(value)
    is Long -> pushInt(value.toInt())
    is Double -> pushDouble(value)
    is Float -> pushDouble(value.toDouble())
    is Boolean -> pushBoolean(value)
    is String -> pushString(value)
    is JsonElement -> pushJsonElement(value)
    is Map<*, *> -> pushMap(value)
    is Iterable<*> -> pushArray(value)
    else -> {}
  }
}

fun WritableArray.pushJsonElement(value: JsonElement) {
  try {
    pushJsonPrimitive(value.jsonPrimitive)
  } catch (_: IllegalArgumentException) {
    try {
      pushJsonObject(value.jsonObject)
    } catch (_: IllegalArgumentException) {
      try {
        pushJsonArray(value.jsonArray)
      } catch (_: IllegalArgumentException) {

      }
    }
  }
}

fun WritableArray.pushJsonPrimitive(value: JsonPrimitive) {
  when {
    value.isString -> pushString(value.content)
    (value.booleanOrNull != null) -> pushBoolean(value.boolean)
    (value.longOrNull != null) -> pushInt(value.long.toInt())
    (value.intOrNull != null) -> pushInt(value.int)
    (value.doubleOrNull != null) -> pushDouble(value.double)
    (value.floatOrNull != null) -> pushDouble(value.float.toDouble())
    else -> {}
  }
}

fun WritableArray.pushJsonArray(value: JsonArray) {
  pushArray(Arguments.createArray().apply {
    value.forEach { this.pushJsonElement(it) }
  })
}

fun WritableArray.pushJsonObject(value: JsonObject) {
  pushMap(Arguments.createMap().apply {
    value.keys.forEach { key -> value[key]?.let { putJsonElement(key, it) } }
  })
}

fun WritableArray.pushMap(value: Map<*,*>) {
  pushMap(Arguments.createMap().apply {
    value.keys.forEach { key ->
      (key as? String)?.let { putAny(it, value[key]) }
    }
  })
}

fun WritableArray.pushArray(value: Iterable<*>) {
  pushArray(Arguments.createArray().apply {
    value.forEach { pushAny(it) }
  })
}

fun WritableMap.putAny(name: String, value: Any?) {
  when (value) {
    is Int -> putInt(name, value)
    is Long -> putInt(name, value.toInt())
    is Double -> putDouble(name, value)
    is Float -> putDouble(name, value.toDouble())
    is Boolean -> putBoolean(name, value)
    is String -> putString(name, value)
    is JsonElement -> putJsonElement(name, value)
    is Map<*, *> -> putMap(name, value)
    is Iterable<*> -> putArray(name, value)
    else -> {}
  }
}

fun WritableMap.putJsonElement(name: String, value: JsonElement) {
  try {
    putJsonPrimitive(name, value.jsonPrimitive)
  } catch (_: IllegalArgumentException) {
    try {
      putJsonObject(name, value.jsonObject)
    } catch (_: IllegalArgumentException) {
      try {
        putJsonArray(name, value.jsonArray)
      } catch (_: IllegalArgumentException) {

      }
    }
  }
}

fun WritableMap.putJsonPrimitive(name: String, value: JsonPrimitive) {
  when {
    value.isString -> putString(name, value.content)
    (value.booleanOrNull != null) -> putBoolean(name, value.boolean)
    (value.longOrNull != null) -> putInt(name, value.long.toInt())
    (value.intOrNull != null) -> putInt(name, value.int)
    (value.doubleOrNull != null) -> putDouble(name, value.double)
    (value.floatOrNull != null) -> putDouble(name, value.float.toDouble())
    else -> {}
  }
}

fun WritableMap.putJsonArray(name: String, value: JsonArray) {
  putArray(name, Arguments.createArray().apply {
    value.forEach { this.pushJsonElement(it) }
  })
}

fun WritableMap.putJsonObject(name: String, value: JsonObject) {
  putMap(name, Arguments.createMap().apply {
    value.keys.forEach { key -> value[key]?.let { putJsonElement(key, it) } }
  })
}

fun WritableMap.putMap(name: String, value: Map<*, *>) {
  putMap(name, Arguments.createMap().apply {
    value.keys.forEach { key ->
      (key as? String)?.let { putAny(it, value[key]) }
    }
  })
}

fun WritableMap.putArray(name: String, value: Iterable<*>) {
  putArray(name, Arguments.createArray().apply {
    value.forEach { this.pushAny(it) }
  })
}

fun ReadableMap.getLongOrNull(name: String) =
  if (hasKey(name) && !isNull(name)) {
    getLong(name)
  } else {
    null
  }

fun ReadableArray.toStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.size()) {
        this.getString(i)?.let { list.add(it) }
    }
    return list
}
