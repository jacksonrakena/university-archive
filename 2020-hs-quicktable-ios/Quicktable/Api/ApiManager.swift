//
//  ApiManager.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import CoreData

class ApiManager: ObservableObject {
    init(context: NSManagedObjectContext) {
        self.dataContext = context
        let cDate = Date()
        if !Calendar.current.isDateInWeekend(cDate) {
            if cDate > Calendar.current.date(bySettingHour: 15, minute: 30, second: 00, of: cDate)! {
                self.selectedDate = Date().advanced(by: 86400).getWeekday(.forward)
            }
            else {
                self.selectedDate = Date().getWeekday(.forward)
            }
        } else {
            self.selectedDate = Date().getWeekday(.forward)
        }
        self.updateAll()
    }
    
    func updateAll() {
        self.updateEvents()
        self.updateTimetable()
    }
    
    // MARK: Core data
    let dataContext: NSManagedObjectContext
    
    // MARK: Global
    @Published var selectedTab: String = "schedule"
    static let baseUrl: String = "https://spider.scotscollege.school.nz/Spider2011/"
    @Published var verificationRequestFailed: Bool = false
    
    // MARK: Schedule
    @UDValue(key: "id", defaultValue: "") var id: String { willSet { self.invalidateCache() }}
    @Published var selectedDate: Date { didSet { self.updateTimetable() } }
    var isToday: Bool { get { return Calendar.current.isDateInToday(self.selectedDate) } }
    @Published var data: [PeriodSlot] = []
    var dataCache: [String: [PeriodSlot]] = [:]
    @Published var error: String? = nil
    @Published var timetableLoading = false
    @UDValue(key: "type", defaultValue: "student") var type: String
    
    // MARK: Assignments
    @Published var showCreateView: Bool = false
    
    // MARK: Profile
    @Published var userIsSignedIn: Bool = false
    @Published var userProfile: UserProfileConstructed? = nil
    @Published var requestState: String = "Authorizing with Spider..."
    @UDValue(key: "username", defaultValue: "") var username: String
    @UDValue(key: "password", defaultValue: "") var password: String
    
    // MARK: Events
    let eventUrl: URL = URL(string: "https://abyssaldev.com/api/v1/quicktable/events")!
    @Published var events: [SchoolEvent]? = nil
    @Published var eventError: String? = nil
    
    func updateEvents(errorHandler: ErrorHandler<String> = nil) {
        print("Updating events...")
        ApiManager.makeWebRequest(to: eventUrl, method: "GET", with: [String:Any](), using: Constants.abyssalDecoder) { (incomingEvents: [SchoolEvent]?, response, error) in
            if error != nil {
                print(error!)
                self.eventError = "There was an error fetching College News."
            }
            DispatchQueue.main.async {
                if incomingEvents != nil {
                    print("Saving \(incomingEvents!.count) events")
                    self.events = incomingEvents
                }
                else {
                    self.eventError = "There was an error fetching College News."
                }
            }
        }
    }
    
    func updateUserProfile(errorHandler: ErrorHandler<String> = nil) {
        if username.isEmpty || password.isEmpty {
            errorHandler?("Username or password empty.")
            return
        }
        guard let url = URL(string: ApiManager.baseUrl + "Handlers/Login.asmx/GetWebLogin") else { return }
        
        let data: [String:Any] = [
            "UserName": username,
            "Password": password,
            "SecurityKey": "a"
        ]
        
        ApiManager.makeWebRequest(to: url, method: "POST", with: data, using: Constants.spiderDecoder) { (model: UserProfileResponse?, response, error) in
            guard let model = model else {
                errorHandler?("Unknown username or password.")
                return
            }
            guard let response = response as? HTTPURLResponse else {
                errorHandler?("Not an HTTP response.")
                return
            }
            if (model.didFail) {
                errorHandler?("Failed to auth with message: " + model.Message!)
                return
            }
            
            guard let memberCode = model.d.first(where: { item in
                item.Key == "MEMBER_CODE"
            })?.Value else {
                    errorHandler?("Failed to find member code.")
                    return
            }
            
            guard let firstName = model.d.first(where: { item in
                item.Key == "USER_FRIENDLY_NAME"
                })?.Value else {
                    errorHandler?("Failed to find user's name.")
                    return
            }
            
            guard let studentId = model.d.first(where: { item in
                item.Key == "MEMBER_ID"
            })?.Value else {
                errorHandler?("Failed to find user's ID.")
                return
            }
            
            if (studentId != self.id) {
                // discrepancy betwen login and provided user ID
            }
            
            print("Auth success.")
            guard let regex = try? NSRegularExpression(pattern: "(.ASPXAUTH=)((.*?);)", options: .caseInsensitive) else {
                errorHandler?("Failed to create regex.")
                return
            }
            guard let cookiesHeader = response.value(forHTTPHeaderField: "Set-Cookie") else {
                errorHandler?("Failed to find cookie header.")
                return
            }
            
            if let match = regex.firstMatch(in: cookiesHeader, options: [], range: NSRange(location: 0, length: cookiesHeader.utf16.count)) {
                if let tokenRange = Range(match.range(at: 3), in: cookiesHeader) {
                    let aspxAuthToken = cookiesHeader[tokenRange]
                    if aspxAuthToken.isEmpty {
                        errorHandler?("Token is empty.")
                        return
                    }
                    DispatchQueue.main.async {
                        self.requestState = "Logged in, loading \(firstName)..."
                    }
                    
                    guard let url = URL(string: ApiManager.baseUrl + "Handlers/Student.asmx/GetStudent?MemberHash=\(studentId)&ShowDeparted=true") else { return}
                    
                    ApiManager.makeWebRequest(to: url, method: "GET", with: [String:Any](), using: Constants.spiderDecoder) { (model: UserInformationResponse?, response, error) in
                        guard let uinfo = model?.d.first else {
                            errorHandler?("User information response is empty.")
                            return
                        }
                        
                        DispatchQueue.main.async {
                            self.userProfile = UserProfileConstructed(aspxAuthKey: String(aspxAuthToken), memberCode: memberCode, firstName: firstName, userInformation: uinfo)
                        }
                    }
                } else {
                    errorHandler?("Failed to establish range.")
                }
            } else {
                errorHandler?("Failed to find match.")
            }
        }
    }
    
    func moveDateForward() {
        selectedDate = selectedDate.advanced(by: 86400).getWeekday(.forward)
    }
    
    func moveDateBackward() {
        selectedDate = selectedDate.advanced(by: -86400).getWeekday(.back)
    }
    
    func resetDate() {
        let cDate = Date()
        if !Calendar.current.isDateInWeekend(cDate) {
            if cDate > Calendar.current.date(bySettingHour: 15, minute: 30, second: 00, of: cDate)! {
                self.selectedDate = Date().advanced(by: 86400).getWeekday(.forward)
            }
            else {
                self.selectedDate = Date().getWeekday(.forward)
            }
        } else {
            self.selectedDate = Date().getWeekday(.forward)
        }
    }
    
    func setDate(to: Date) {
        selectedDate = to.getWeekday(.nearest)
    }
    
    func save() {
        do {
            try dataContext.save()
        } catch {
        }
    }
    
    func invalidateCache() {
        print("Invalidating cache...")
        self.dataCache = [:]
        updateTimetable()
    }
    
    func tryGetFromCache(of date: Date) -> [PeriodSlot]? {
        if (Calendar.current.isDateInWeekend(date)) {
            print("Weekend, skipping.")
            return []
        }
        if let d = dataCache[date.string(inFormat: "dd/MM/YYYY")] {
            if (d.count == 0) {
                return nil
            }
            return d
        }
        return nil
    }
    
    static func makeWebRequest<T: Decodable>(to url: URL, method: String, with jsonData: JsonObject, using decoder: JSONDecoder, callback: @escaping (T?, URLResponse?, Error?) -> Void) {
        let body = try! JSONSerialization.data(withJSONObject: jsonData)
        
        var request = URLRequest(url: url)
        request.httpMethod = method
        if method != "GET" {
            request.httpBody = body
        }
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTask(with: request) { (data, response, error) in
            if error != nil {
                callback(nil, response, error)
                return
            }
            guard let data = data else { return }
            do {
                let json = try decoder.decode(T.self, from: data)
                callback(json, response, nil)
            } catch {
                callback(nil, response, error)
            }
        }.resume()
    }
    
    typealias JsonObject = [String:Any]
    
    static func buildJsonObject(id: Int, type: String, date: Date) -> JsonObject {
        let stringDate = date.string(inFormat: "dd/MM/YYYY")
        
        var obj: JsonObject = [
            "LoadFutureDate": false,
            "Date": stringDate,
            "StudentID": 0,
            "TeacherID": 0
        ]
        
        switch (type) {
        case "student":
            obj["StudentID"] = id
            obj["TeacherID"] = 0
            break
        case "staff":
            obj["TeacherID"] = id
            obj["StudentID"] = 0
            break
        default:
            obj["TeacherID"] = 0
            obj["StudentID"] = id
        }
        
        return obj
    }
    
    static func updateTimetableNoCacheStore(id: Int, date: Date, type: String, onComplete: (([PeriodSlot]) -> Void)? = nil, onError: ((String) -> Void)? = nil) {
        guard let url = URL(string: self.baseUrl + "Handlers/Timetable.asmx/GetTimeTable_ByDayW") else {
            onError?("Couldn't make base URL")
            return
        }
        
        let body: JsonObject = buildJsonObject(id: id, type: type, date: date)
        
        makeWebRequest(to: url, method: "POST", with: body, using: Constants.spiderDecoder) { (json: DataMessage?, response, error: Error?) in
            if error != nil {
                print(error!)
                onError?(error!.localizedDescription)
                return
            }
            guard let json: DataMessage = json else {
                print("JSON response is nil")
                onError?("JSON nil")
                return
            }
            if onComplete != nil {
                onError?("Complete")
                onComplete!(json.d)
            }
        }
    }
    
    func updateTimetable(onComplete: (([PeriodSlot]) -> Void)? = nil) {
        guard let person = Int(self.id),
            let url = URL(string: ApiManager.baseUrl + "Handlers/Timetable.asmx/GetTimeTable_ByDayW") else { return
        }
        
        timetableLoading = true
        
        // cache
        if let d = tryGetFromCache(of: selectedDate) {
            print("Retrieved from cache.")
            self.data = d
            timetableLoading = false
            return
        }
        
        print("Sending request...")
        
        let body: JsonObject = ApiManager.buildJsonObject(id: person, type: type, date: selectedDate)
        
        ApiManager.makeWebRequest(to: url, method: "POST", with: body, using: Constants.spiderDecoder) { (json: DataMessage?, response, error: Error?) in
            if error != nil {
                print(error!)
                self.error = "There was an issue fetching timetable information."
                self.timetableLoading = false
                return
            }
            guard let json: DataMessage = json else {
                print("JSON response is nil")
                self.timetableLoading = false
                self.error = "Couldn't find you. Try re-installing the app, and trying your student ID with/without zeroes."
                onComplete?([PeriodSlot]())
                return
            }
            if json.d.count == 0 {
                self.timetableLoading = false
                self.verificationRequestFailed = true
                self.error = "We had an unexpected error fetching your timetable. Try restarting the app."
                onComplete?([PeriodSlot]())
                return
            }
            var periods = [PeriodSlot]()
            json.d.forEach { item in
                do {
                    if (item.Period.isEmpty) {return}
                    var subject: Subject
                    let subjects: [Subject] = try self.dataContext.fetch(predicate: NSPredicate(format: "shortName = %@", item.SubjectAbbrev))
                    if subjects.count == 0 {
                        subject = Subject(context: self.dataContext)
                        subject.shortName = item.SubjectAbbrev
                        subject.longName = item.SubjectDesc
                        subject.classNumber = item.Class
                        subject.teacherEmail = item.TeacherEmail
                        subject.teacherName = item.Teacher
                        subject.yearGroup = item.Year
                    } else {
                        subject = subjects.first!
                    }
                    periods.append(item)
                } catch {
                    print("Error processing subject " + item.SubjectAbbrev)
                    print(error)
                }
            }
            if onComplete != nil {
                onComplete!(json.d)
            }
            try! self.dataContext.save()
            self.dataCache[body["Date"] as! String] = periods
            DispatchQueue.main.async {
                self.timetableLoading = false
                self.data = periods
            }
        }
    }
}
