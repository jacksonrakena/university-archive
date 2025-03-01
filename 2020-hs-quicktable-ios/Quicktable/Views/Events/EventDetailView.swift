//
//  EventDetailView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 5/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI

struct EventDetailView: View {
    var event: SchoolEvent
    var body: some View {
        VStack {
            if event.imageUrl != nil {
                AsyncImage(url: URL(string: self.event.imageUrl!)!, placeholder: Text("Loading image...")).frame(width: 200, height: 200, alignment: .center)
            }
            HStack {
                Text(event.name).font(.title)
            }
                Text("Author: \(event.author) (\(event.authorTitle))").foregroundColor(.gray)
                Text("Location: \(event.location)")
                Text("Applies to: \(event.appliesTo)")
            if (event.isAllDay) {
                Text(event.startTime.string(inFormat: "MMMM d, YYYY"))
            } else {
                if (Calendar.current.isDate(event.startTime, inSameDayAs: event.endTime)) {
                    Text("\(event.startTime.string(inFormat: "MMMM d")) \(event.startTime.string(inFormat: "h:mm a")) - \(event.endTime.string(inFormat: "h:mm a"))")
                } else {
                    Text("\(event.startTime.string(inFormat: "MMMM d")) \(event.startTime.string(inFormat: "h:mm a")) - \(event.endTime.string(inFormat: "MMMM d")) \(event.endTime.string(inFormat: "h:mm a"))")
                }
            }
            Spacer().frame(minHeight: 10, maxHeight: 20)
            Text(event.description).fixedSize(horizontal: false, vertical: true).padding(.horizontal)
            if event.associatedLink != nil {
                Button(action: {
                    guard let url = URL(string: self.event.associatedLink!) else {
                        return
                    }
                    UIApplication.shared.open(url)
                }) {
                    Text(event.associatedLink!)
                }
            }
        }
    }
}

struct EventDetailView_Previews: PreviewProvider {
    static var previews: some View {
        EventDetailView(event: SchoolEvent(startTime: Date(), endTime: Date(), isAllDay: false, name: "Test event", description: "Preview event aaaaaaaaaaaaaaaaaaaaaa", author: "Preview provider", authorTitle: "Xcode", appliesTo: "All", location: "City", imageUrl: nil))
    }
}
