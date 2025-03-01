//
//  PropertyWrappers.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation

@propertyWrapper
struct UDValue<T> {
    let key: String
    let defaultValue: T
    
    var wrappedValue: T {
        get {
            guard let obj = UserDefaults.init(suiteName: "group.quicktable")!.object(forKey: key) else { return defaultValue }
            return obj as! T
        }
        set(newValue) {
            UserDefaults.init(suiteName: "group.quicktable")!.set(newValue, forKey: key)
        }
    }
}
