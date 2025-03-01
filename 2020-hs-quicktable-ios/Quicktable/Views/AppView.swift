//
//  ContentView.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 28/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI
import Foundation
import Combine
import CoreData

struct AppView: View {
    @EnvironmentObject var api: ApiManager
    @Environment(\.managedObjectContext) var moc: NSManagedObjectContext
    @State var showSetup: Bool = !(UserDefaults.init(suiteName: "group.quicktable")!.bool(forKey: "hasCompletedSetup"))
    @State private var keyboardHeight: CGFloat = 0
    @State var tempId: String = ""
    @State var isShowingScanner = false
    @State var showModalStyle = true
    @State var verificationRequestFailed = false
    @State var type = "student"
    var body: some View {
        return TabView(selection: $api.selectedTab) {
            ScheduleTabView()
                .tabItem {
                    Image(systemName: "list.dash")
                    Text("Classes")
                }.tag("schedule").environment(\.managedObjectContext, self.moc).environmentObject(api)
            /*ScrollableTimetableView().tabItem {
                Image(systemName: "list.dash")
                Text("Classes")
            }.tag("schedule").environment(\.managedObjectContext, self.moc).environmentObject(api)*/
            
            AssignmentsTabView()
                .tabItem {
                    Image(systemName: "square.and.pencil")
                    Text("Assignments")
            }.tag("assignments").environment(\.managedObjectContext, self.moc).environmentObject(api)
          
            /*DailyNoticesTabView()
                .tabItem {
                    Image(systemName: "sun.max")
                    Text("Daily Notices")
            }.tag("dailynotices").environment(\.managedObjectContext, self.moc).environmentObject(api)*/
            
            /*EventsTabView()
                .tabItem {
                        Image(systemName: "calendar")
                        Text("News")
                }.tag("events").environment(\.managedObjectContext, self.moc).environmentObject(api)
            */
                /*HouseCompetitionTabView()
                .tabItem {
                    Image(systemName: "guitars")
                    Text("Caradus Shield")
                }.tag("housecomp").environment(\.managedObjectContext, self.moc).environmentObject(api)*/
            
            /*ProfileTabView()
                .tabItem {
                    Image(systemName: "person")
                    Text("Profile")
            }.tag("profile").environment(\.managedObjectContext, self.moc).environmentObject(api)*/
        
        }.sheet(isPresented: $showSetup) {
            VStack {
                HStack(alignment: .center, spacing: 1) {
                    Text("Welcome to ").bold().font(.title)
                    Text("Quicktable.").bold().font(.title).foregroundColor(Color.orange)
                }.padding(.bottom)
                Text("We're your all-in-one solution for College life, with your timetable, assignments, Daily Notices, and upcoming school events all rolled into one.").foregroundColor(.gray).fixedSize(horizontal: false, vertical: true)
                if (self.verificationRequestFailed) {
                    Text("Try removing or adding the zeroes to the front of your ID. If you're using PCSchool, are you typing in the 'Barcode' number?").foregroundColor(.red).padding(.top).fixedSize(horizontal: false, vertical: true)
                } else {
                    Text("To begin, we're going to need the 5 or 6 digits on the lower right side of your school-issued student ID. Include zeroes, but remove them if an error arises.").padding(.top).fixedSize(horizontal: false, vertical: true)
                }
                HStack {
                    Spacer()
                    TextField("", text: self.$tempId)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding(.horizontal)
                    .keyboardType(.numberPad)
                    .frame(width: 200)
                    Spacer()
                }
                Picker(selection: self.$type, label: Text("Student or teacher?")) {
                    Text("Student").tag("student")
                    Text("Teacher").tag("staff")
                }.pickerStyle(SegmentedPickerStyle())
                Button(action: {
                    if Int(self.tempId) != nil {
                        self.api.id = self.tempId
                        self.api.type = self.type
                        self.api.updateTimetable { data in
                            if data.count != 0 {
                                UserDefaults.init(suiteName: "group.quicktable")!.set(true, forKey: "hasCompletedSetup")
                                self.showSetup.toggle()
                            } else {
                                self.verificationRequestFailed = true
                            }
                        }
                    }
                }) {
                    Text("Get started")
                        .padding(10)
                        .foregroundColor(Color.white)
                        .background(Color.orange)
                        .cornerRadius(10)
                }
                Text("It's the 'Barcode' number on PCSchool's Student Information page.").padding(.top, 10)
            }.padding().keyboardAdaptive().presentation(isModal: self.$showModalStyle) {
                
            }
        }.onTapGesture(count: 2) {
            if (self.api.selectedTab == "schedule") {
                self.api.resetDate()
            }
        }
    }
}
