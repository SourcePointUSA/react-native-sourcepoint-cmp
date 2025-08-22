package com.sourcepoint.reactnativecmp

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

interface SPEventEmitter {
  fun emitOnAction(params: ReadableMap?)
  fun emitOnSPUIReady()
  fun emitOnSPUIFinished()
  fun emitOnFinished()
  fun emitOnMessageInactivityTimeout()
  fun emitOnError(params: ReadableMap?)
}

class EventEmitter(val reactContext: ReactApplicationContext): SPEventEmitter {
  private enum class EventName(val value: String) {
    OnAction("onAction"),
    OnSPUIReady("onSPUIReady"),
    OnSPUIFinished("onSPUIFinished"),
    OnFinished("onFinished"),
    OnMessageInactivityTimeout("onMessageInactivityTimeout"),
    OnError("onError")
  }

  private fun emitEvent(name: EventName, params: ReadableMap? = null) {
    if (!reactContext.hasActiveReactInstance()) return

    try {
      reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit(name.value, params)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }

  override fun emitOnAction(params: ReadableMap?) {
    emitEvent(EventName.OnAction, params)
  }

  override fun emitOnSPUIReady() {
    emitEvent(EventName.OnSPUIReady)
  }

  override fun emitOnSPUIFinished() {
    emitEvent(EventName.OnSPUIFinished)
  }

  override fun emitOnFinished() {
    emitEvent(EventName.OnFinished)
  }

  override fun emitOnMessageInactivityTimeout() {
    emitEvent(EventName.OnMessageInactivityTimeout)
  }

  override fun emitOnError(params: ReadableMap?) {
    emitEvent(EventName.OnError, params)
  }
}
