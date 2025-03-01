//
//  PeriodLink.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct PeriodLink: View {
    let format = "h:mm a"
    @FetchRequest(entity: Subject.entity(), sortDescriptors: []) var subjects: FetchedResults<Subject>
    @EnvironmentObject var api: ApiManager
    func getColorForPeriod(period: PeriodSlot) -> Color {
        let search = subjects.first { subject in
            return subject.shortName == period.SubjectAbbrev
        }
        if search != nil && search!.colorHex != nil {
            guard let uic = UIColor(hex: search!.colorHex!) else {
                return period.SubjectDesc.randomColor()
            }
            return Color(uic)
        }
        return period.SubjectDesc.randomColor()
    }
    
    var period: PeriodSlot
    var overrideColor: Color?
    var isClickable: Bool = true
    var body: some View {
        NavigationLink(destination: PeriodDetailView(of: period).navigationBarTitle(period.SubjectDesc).environmentObject(self.api)) {
            VStack {
                Text(period.SubjectDesc).foregroundColor(.white).font(.headline)
                HStack {
                    Text("Room " + period.Room).foregroundColor(.white).bold().font(.footnote)
                    if (api.type == "student") {
                        Text(period.Teacher).foregroundColor(.white).font(.footnote)
                    } else {
                        Text("Class " + period.Class + " (Year " + period.Year + ")").foregroundColor(.white).font(.footnote)
                    }
                }
                if (!period.SubjectDesc.isEmpty) { Text("\(period.Heading) (\(period.getTime(.fromTime).string(inFormat: self.format)) - \(period.getTime(.toTime).string(inFormat: self.format)))").font(.footnote).foregroundColor(.white)}
            }
            .edgesIgnoringSafeArea(.all).frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .center).background(
                RoundedRectangle(cornerRadius: 15)
                    .fill(/*LinearGradient(
                         gradient: .init(colors: [Color(red: 240 / 255, green: 240 / 255, blue: 240 / 255), Utils.randomColor(seed: period.SubjectDesc)]),
                         startPoint: .init(x: 0, y: 1),
                         endPoint: .init(x: 1, y: 0)
                         ))*/ self.overrideColor ?? self.getColorForPeriod(period: period))).padding(.horizontal, 15).padding(.top, 5).shadow(radius: 10)
        }.disabled(!isClickable)
    }
}
