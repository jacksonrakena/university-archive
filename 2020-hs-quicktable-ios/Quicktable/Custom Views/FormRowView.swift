//
//  FormRowView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 16/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI

struct FormRowView<T: View>: View {
    var title: T
    var value: T
    var body: some View {
        HStack {
            title
            Spacer()
            value
        }
    }
}

struct TextRowView : View {
    var title: String
    var value: String
    var body: some View {
        FormRowView(title: Text(title), value: Text(value).foregroundColor(.gray))
    }
}
