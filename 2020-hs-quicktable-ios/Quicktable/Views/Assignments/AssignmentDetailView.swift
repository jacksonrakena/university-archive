//
//  AssignmentDetailView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct AssignmentDetailView: View {
    var assignment: Assignment
    var body: some View {
        let calendar = Calendar.current

        // Replace the hour (time) of both dates with 00:00
        let date1 = calendar.startOfDay(for: Date())
        let date2 = calendar.startOfDay(for: assignment.due!)

        let components = calendar.dateComponents([.day], from: date1, to: date2)
        return Form {
            Section(header: Text("Information")) {
                TextRowView(title: "Assigned", value: assignment.assigned!.string(inFormat: "EEEE, MMMM d"))
                TextRowView(title: "Due", value: "\(assignment.due!.string(inFormat: "EEEE, MMMM d")) (in \(components.day!) day\(components.day! != 1 ? "s" : ""))")
                TextRowView(title: "Subject", value: assignment.subject!.longName!)
            }
            Section(header: Text("Notes")) {
                Text((assignment.notes == nil || assignment.notes!.isEmpty) ? "None" : assignment.notes!)
            }
        }.navigationBarTitle(Text(assignment.name!), displayMode: .inline)
    }
}
