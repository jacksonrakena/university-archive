//
//  HouseCompetitionTabView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 15/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI

struct HouseCompetitionTabView: View {
    var body: some View {
        NavigationView {
            VStack {
                Text("The current house in the lead is Smith with 196 points.")
                ZStack(alignment: .center) {
                    Circle().fill(Color(.blue))
                    Circle().fill(Color(.white)).scaleEffect(0.75)
                }
            }.navigationBarTitle("Caradus Shield")
        }
    }
}
