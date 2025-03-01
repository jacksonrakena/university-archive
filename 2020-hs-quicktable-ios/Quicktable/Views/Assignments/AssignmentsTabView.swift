//
//  AssignmentsTabView.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI
import CoreData

struct AssignmentsTabView: View {
    @EnvironmentObject var api: ApiManager
    @FetchRequest(entity: Assignment.entity(), sortDescriptors: []) var assignments: FetchedResults<Assignment>
    @Environment(\.managedObjectContext) var moc: NSManagedObjectContext
    
    var body: some View {
        return NavigationView {
            VStack(alignment: .center) {
                List {
                    ForEach(assignments, id: \.id) { item in
                        AssignmentLink(item: item)
                    }.onDelete { (index) in
                        for i in index {
                            let assignment = self.assignments[i]
                            self.moc.delete(assignment)
                        }
                        do {
                            try self.moc.save()
                        } catch {}
                    }
                }
            }
            .navigationBarTitle("Assignments")
            .navigationBarItems(leading: EditButton(), trailing: Button(action: {
                self.api.showCreateView.toggle()
            }) {
                Image(systemName: "plus").imageScale(.large).padding().edgesIgnoringSafeArea(.all)
            })
        }.sheet(isPresented: $api.showCreateView) {
            CreateAssignmentView().environmentObject(self.api).environment(\.managedObjectContext, self.moc)
        }
    }
}
