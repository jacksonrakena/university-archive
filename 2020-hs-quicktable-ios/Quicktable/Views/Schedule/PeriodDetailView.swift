//
//  PeriodDetailView.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI
import CoreData

struct PeriodDetailView: View {
    @EnvironmentObject var api: ApiManager
    @FetchRequest(entity: Subject.entity(), sortDescriptors: []) var subjects: FetchedResults<Subject>
    var subject: Subject? { get {
        return subjects.first { s0 in
            return s0.shortName != nil && s0.shortName == of.SubjectAbbrev
        }
    }}
    
    func getRandUICol(period: PeriodSlot) -> UIColor {
        let search = subjects.first { subject in
            return subject.shortName == period.SubjectAbbrev
        }
        if search != nil && search!.colorHex != nil {
            guard let uic = UIColor(hex: search!.colorHex!) else {
                return period.SubjectDesc.randomUIColor()
            }
            return uic
        }
        return period.SubjectDesc.randomUIColor()
    }
    
    func getColorForPeriod(period: PeriodSlot) -> Color {
        return Color(getRandUICol(period: period))
    }
    
    let of: PeriodSlot
    
    var body: some View {
        let assignments: Set<Assignment> = subject?.assignments ?? []
        
        return VStack {
            HStack(alignment: .center) {
                VStack(alignment: .leading) {
                    Text("\(of.SubjectDesc)").bold()
                    Text("\(of.SubjectAbbrev) - \(of.getTime(.fromTime).string(inFormat: "h:mm a"))").foregroundColor(.gray)
                }
                Spacer()
                VStack(alignment: .leading) {
                    Text("Room \(of.Room)").bold()
                    Text("Class \(of.Year) \(of.Class)").foregroundColor(.gray)
                }
            }
            VStack(alignment: .leading) {
                Text("Teacher").bold()
                Text(of.Teacher).foregroundColor(.gray)
                Text("Assignments").bold()
                List {
                    if assignments.count != 0 {
                        ForEach(assignments.map {item in return item}, id: \.id) { item in
                            AssignmentLink(item: item)
                        }
                    } else {
                        ForEach(["All finished!"], id: \.self) { item in
                            Text(item)
                        }
                    }
                }
            }
        }.padding()
        
        /*return Form {
            Section(header: Text("Time & Place")) {
                TextRowView(title: "Subject", value: "\(of.SubjectAbbrev) \(of.SubjectDesc)")
                TextRowView(title: "Time", value: "\(of.Heading) \(of.getTime(.fromTime).string(inFormat: "h:mm a")) - \(of.getTime(.toTime).string(inFormat: "h:mm a"))")
                TextRowView(title: "Room", value: of.Room)
                TextRowView(title: "Class", value: "\(of.Year) \(of.Class)")
            }
            Section(header: Text("Teacher")) {
                TextRowView(title: "Teacher", value: of.Teacher)
                Button(action: {
                    if let link = URL(string: "mailto:" + self.of.TeacherEmail) {
                        UIApplication.shared.open(link)
                    }
                }) {
                    Text(of.TeacherEmail).foregroundColor(.blue)
                }
            }
            Section(header: Text("Assignments")) {
                    if assignments.count != 0 {
                        ForEach(assignments.map {item in return item}, id: \.id) { item in
                            AssignmentLink(item: item)
                        }
                    } else {
                        ForEach(["All finished!"], id: \.self) { item in
                            Text(item)
                        }
                    }
            }
            Section(header: Text("Settings")) {
                NavigationLink(destination: SetColourView(hue: getRandUICol(period: of).hueComponent, brightness: getRandUICol(period: of).brightnessComponent, saturation: getRandUICol(period: of).saturationComponent, subject: subject!, slot: of).environmentObject(api)) {
                    HStack {
                        Text("Change colour")
                        Spacer()
                        RoundedRectangle(cornerRadius: 1).fill(getColorForPeriod(period: of)).frame(width: 20, height: 20, alignment: .center)
                    }
                }
            }*/
    }
}
