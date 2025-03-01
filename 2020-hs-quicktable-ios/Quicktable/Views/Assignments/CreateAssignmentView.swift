//
//  CreateAssignmentView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI
import CoreData


struct CreateAssignmentView: View {
    @EnvironmentObject var api: ApiManager
    @Environment(\.managedObjectContext) var moc: NSManagedObjectContext
    @State var name: String = ""
    @State var due: Date = Date()
    @State var selectedSubjectString: Int = 0
    @State var notes: String = ""
    @State var showingClassSelect: Bool = false
    @FetchRequest(entity: Subject.entity(), sortDescriptors: []) var subjects: FetchedResults<Subject>
    
    var body: some View {
        return NavigationView {
            VStack {
                Form {
                    TextField("Name", text: $name)
                    DatePicker(selection: $due, displayedComponents: .date) {
                        Text("Due date")
                    }
                    Picker(selection: $selectedSubjectString, label: Text("Subject")) {
                        Group {
                            ForEach(0 ..< subjects.count) {
                                Text(self.subjects[$0].longName!)
                            }
                        }
                    }
                    
                    TextField("Notes", text: $notes)
                    Button(action: {
                        if self.name.isEmpty {
                            return
                        }
                        let assignment = Assignment(context: self.moc)
                        assignment.id = UUID()
                        assignment.name = self.name
                        assignment.due = self.due
                        assignment.assigned = Date()
                        assignment.notes = self.notes
                        assignment.subject = self.subjects[self.selectedSubjectString]
                        
                        self.api.save()
                        self.api.showCreateView.toggle()
                    }) {
                        Text("Create")
                    }
                }.navigationBarTitle("Assignment")
            }
        }
    }
}
