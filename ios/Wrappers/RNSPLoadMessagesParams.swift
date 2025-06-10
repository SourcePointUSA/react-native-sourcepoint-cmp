//
//  RNSPLoadMessagesParams.swift
//  Pods
//
//  Created by Andre Herculano on 6/6/25.
//

@objcMembers public class RNSPLoadMessageParams: NSObject {
    let authId: String?

    public init(authId: String?) {
        self.authId = authId
    }
}
