//
//  TimetableView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct TimetableView : View {
    @EnvironmentObject var api: ApiManager
    let format = "h:mm a"
    @State var showPopover = false
    
    var body: some View {
        return VStack {
            if api.timetableLoading {
                ActivityIndicator(isAnimating: .constant(true), style: .medium)
                Text("Loading timetable for \(self.api.selectedDate.string(inFormat: "EEEE, MMMM d"))...")
            } else if (api.error != nil) {
                Text(api.error ?? "An unknown error occurred.")
            } else {
                VStack(alignment: .center) {
                    ForEach(api.data, id: \.SubjectAbbrev) { period in
                        PeriodLink(period: period).environmentObject(self.api)
                    }
                }.padding(.bottom, 10)
                HStack {
                    Button(action: {
                        self.api.moveDateBackward()
                    }) {
                        Image(systemName: "chevron.left")
                            .padding(10)
                            .foregroundColor(Color.white)
                            .background(Color.orange)
                            .cornerRadius(10)
                    }
                    Text(self.api.selectedDate.string(inFormat: "EEEE, MMMM d, yyyy"))
                        .foregroundColor(.orange).padding(.horizontal, 20)
                    /*    .onTapGesture {
                            self.showPopover = true
                    }
                        .popover(isPresented: $showPopover, arrowEdge: .bottom) {
                        VStack {
                            Text("View timetable for date:").bold().font(.callout)
                            DatePicker("", selection: self.$api.selectedDate, displayedComponents: .date)
                                .datePickerStyle(DefaultDatePickerStyle()).padding(.top)
                            HStack {
                                Button(action: { self.api.resetDate() }) {
                                    Text("Today").padding(10).foregroundColor(Color.blue)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 10)
                                                .stroke(Color.orange, lineWidth: 2)
                                    )
                                }
                                Button(action: {
                                    self.api.setDate(to: self.api.selectedDate)
                                    self.showPopover = false
                                }) {
                                    Text("Save").padding(10).foregroundColor(Color.white).background(Color.orange).cornerRadius(10)
                                }
                            }
                        }
                    }*/
                    Button(action: {
                        self.api.moveDateForward()
                    }) {
                        Image(systemName: "chevron.right")
                            .padding(10)
                            .foregroundColor(Color.white)
                            .background(Color.orange)
                            .cornerRadius(10)
                    }
                }.padding(.bottom, 20)
            }
        }
    }
}
