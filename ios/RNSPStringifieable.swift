//
//  RNSPStringifieable.swift
//  Pods
//
//  Created by Andre Herculano on 5/9/25.
//

import ConsentViewController

@objc public protocol RNSPStringifieable {
  @objc var defaultStringEncoded: String { get }
  @objc var dictionaryEncoded: [String: Any] { get }
  @objc var stringifiedJson: String { get }

  @objc optional func stringifiedJson(default: String) -> String
}

public extension RNSPStringifieable {
  func stringifiedJson() -> String {
    if let jsonData = try? JSONSerialization.data(withJSONObject: dictionaryEncoded),
       let jsonString = String(data: jsonData, encoding: .utf8) {
        return jsonString
    } else {
      return defaultStringEncoded
    }
  }
}
