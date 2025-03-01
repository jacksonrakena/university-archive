//
//  Models.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation

typealias ErrorHandler<T> = ((T) -> Void)?

struct SchoolEvent: Decodable {
    public var startTime, endTime: Date
    public var isAllDay: Bool
    public var name, description, author, authorTitle, appliesTo, location: String
    public var imageUrl, associatedLink, colour: String?
}

struct DataMessage : Decodable {
    var d: [PeriodSlot]
}

enum Person {
    case student(id: Int)
    case teacher(id: Int)
}

enum TimeType {
    case fromTime, toTime
}

struct UserInformationResponse: Decodable {
    public var d: [UserInformationStruct]
}

struct UserInformationStruct: Decodable {
    public var StudentName, StudentKey, GivenName, HomeTeacher, Dean, BirthDate, AgeToday, ExamNo, ImageNameEncrypted, KnownAs: String
    public var StudentID: Int32
    
    public var birthDate: Date { get {
        return BirthDate.asPcsDate()!
    }}
}

struct UserProfileConstructed {
    public var aspxAuthKey, memberCode, firstName: String
    public var userInformation: UserInformationStruct
}

struct UserProfileResponseKvp: Decodable {
    public var Key, Value: String
}

struct UserProfileResponse: Decodable {
    public var d: [UserProfileResponseKvp]
    public var Message: String?
    public var didFail: Bool { get { return Message != nil && !Message!.isEmpty }}
}

enum Direction {
    case back
    case nearest
    case forward
}

struct PeriodSlot: Decodable {
    public var SubjectAbbrev, SubjectDesc, FromTime, ToTime, Teacher, Heading, Room, Period, Year, Class, CalendarDate, TeacherEmail: String
    public var CurrentPeriod_ByServerTime: Bool
    
    public var date: Date {
        get {
            return self.CalendarDate.asPcsDate()!
        }
    }
    
    func getTime(_ of: TimeType) -> Date {
        var time: String
        switch of {
        case .fromTime:
            time = self.FromTime
        case .toTime:
            time = self.ToTime
        }
        let split = time.split(separator: ".")
        let hour = String(split[0])
        let minutes = String(split[1])
        return Calendar.current.date(bySettingHour: Int(hour)!, minute: Int(minutes)!, second: 0, of: self.CalendarDate.date(inFormat: Constants.pcsDateFormat)!)!
    }
}
