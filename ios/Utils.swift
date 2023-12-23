//
//  Utils.swift
//  react-native-sourcepoint-cmp
//
//  Created by Andre Herculano on 21.12.23.
//

import Foundation
import ConsentViewController

extension JSONEncoder {
    func encodeResult<T: Encodable>(_ value: T) -> Result<Data, Error> {
        Result { try self.encode(value) }
    }
}

extension JSONDecoder {
    func decode<T: Decodable>(_ type: T.Type, from: Data) -> Result<T, Error> {
        Result { try self.decode(type, from: from) }
    }
}

extension UserDefaults {
    func setObject<T: Encodable>(_ value: T, forKey defaultName: String) {
        let encoder = JSONEncoder()
        if let encoded = try? encoder.encodeResult(value).get() {
            self.set(encoded, forKey: defaultName)
        }
    }

    func object<T>(ofType type: T.Type, forKey defaultName: String) -> T? where T: Decodable {
        let decoder = JSONDecoder()
        if let data = self.data(forKey: defaultName),
           let object = try? decoder.decode(type, from: data).get() {
            return object
        }
        return nil
    }

    func removeObjects(forKeys keys: [String]) {
        keys.forEach { removeObject(forKey: $0) }
    }
}

extension SPUserData {
    func toDictionary() -> [String: Any]? {
        guard let jsonData = try? JSONEncoder().encode(self) else {
            return nil
        }
        return try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any]
    }
}
