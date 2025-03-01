//
//  AssignmentLink.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct AssignmentLink: View {
    var item: Assignment
    let format = "EEEE, MMMM d"
    var body: some View {
        NavigationLink(destination: AssignmentDetailView(assignment: item)) {
            VStack(alignment: .leading) {
                Text("Due \(item.due!.string(inFormat: "EEEE, MMMM d"))").foregroundColor(.gray)
                Text(item.name!)
            }
        }
    }
}
