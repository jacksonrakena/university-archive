//
//  ScheduleTabView.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct ScheduleTabView: View {
    @EnvironmentObject var api: ApiManager
    
    func getTimetableViewTitle() -> String {
        if Calendar.current.isDateInToday(api.selectedDate) {
            return "Today"
        }
        else if Calendar.current.isDateInYesterday(api.selectedDate) {
            return "Yesterday"
        }
        else if Calendar.current.isDateInTomorrow(api.selectedDate) {
            return "Tomorrow"
        }
        else if Calendar.current.isDate(api.selectedDate, equalTo: Date(), toGranularity: .weekOfYear) {
            return api.selectedDate.string(inFormat: "EEEE")
        }
        else if Calendar.current.isDate(api.selectedDate.advanced(by: 604800), equalTo: Date(), toGranularity: .weekOfYear) {
            return "Last " + api.selectedDate.string(inFormat: "EEEE")
        }
        else if Calendar.current.isDate(api.selectedDate.advanced(by: -604800), equalTo: Date(), toGranularity: .weekOfYear) {
            return "Next " + api.selectedDate.string(inFormat: "EEEE")
        } else {
            return api.selectedDate.string(inFormat: "EEEE, MMMM d")
        }
    }
    
    var body: some View {
        return NavigationView {
            TimetableView().environmentObject(api).navigationBarTitle(getTimetableViewTitle())
        }
    }    
}
