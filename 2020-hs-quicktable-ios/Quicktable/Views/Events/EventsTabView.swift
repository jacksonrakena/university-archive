//
//  EventsTabView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 5/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI

struct EventsTabView: View {
    @EnvironmentObject var api: ApiManager
    init() {
        //self.api.updateEvents()
    }
    var body: some View {
        NavigationView {
            if (self.api.eventError != nil) {
                Text(self.api.eventError!)
            }
            if (self.api.events == nil || self.api.events!.count == 0) {
                Text("Loading College News...")
                
            } else {
                List(self.api.events?.sorted(by: { $0.startTime.compare($1.startTime) == .orderedDescending }) ?? [], id: \.name) { event in
                    NavigationLink(destination: EventDetailView(event: event)) {
                        VStack(alignment: .leading) {
                            Text(event.startTime.string(inFormat: "EEEE, MMMM d, yyyy")).foregroundColor(.gray)
                            Text(event.name)
                        }
                    }
                }.navigationBarTitle("College News")
            }
        }
    }
}
