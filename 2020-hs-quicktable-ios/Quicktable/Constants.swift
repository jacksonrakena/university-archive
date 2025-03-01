//
//  Utils.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import Combine
import SwiftUI
import UIKit
import CoreData
import EventKit

class Constants {
    static var abyssalDecoder: JSONDecoder { get {
        let decoder = JSONDecoder()
        let format = DateFormatter()
        format.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        decoder.dateDecodingStrategy = .formatted(format)
        return decoder
        }}
    static var spiderDecoder: JSONDecoder { get {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .formatted(pcsDateFormatter)
        return decoder
        }
    }
    
    static var pcsDateFormatter: DateFormatter { get {
        let df = DateFormatter()
        df.dateFormat = pcsDateFormat
        return df
        }}
    
    static let pcsDateFormat = "dd/MM/YYYY"
}
