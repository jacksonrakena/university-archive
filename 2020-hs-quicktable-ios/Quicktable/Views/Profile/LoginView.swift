//
//  LoginView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 6/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import SwiftUI

struct LoginView: View {
    @State var username: String = ""
    @State var password: String = ""
    @State var loading: Bool = false
    @State var animateSpinner: Bool = true
    @EnvironmentObject var api: ApiManager
    var body: some View {
        if loading {
            return AnyView(VStack {
                if !animateSpinner {
                    Text("Error").font(.callout).bold()
                }
                Text(api.requestState)
                if (animateSpinner) {
                    ActivityIndicator(isAnimating: $animateSpinner, style: .large)
                }
            })
        }
        return AnyView(VStack {
            Image("Logo").frame(width: 200, height: 200, alignment: .center).scaleEffect(0.15).padding()
            Text("Spider").font(.title).bold()
            Text("Connect your Spider account to gain access to authorized features.")
            HStack {
                TextField("Username", text: $username).textContentType(.username).autocapitalization(.none).textFieldStyle(RoundedBorderTextFieldStyle()).frame(width: CGFloat(100))
                Text("@scotscollege.school.nz")
            }
            SecureField("Password", text: $password).textContentType(.password).padding([.horizontal, .vertical]).padding(.bottom).autocapitalization(.none).textFieldStyle(RoundedBorderTextFieldStyle())
            Button(action: {
                self.loading = true
                self.requestAuth()
            }) {
                Text("Login")
                .padding(10)
                .foregroundColor(Color.white)
                .background(Color.orange)
                .cornerRadius(10)
            }
        }.keyboardAdaptive())
    }
    
    func requestAuth() {
        self.api.username = username
        self.api.password = password
        DispatchQueue.main.async {
            self.api.updateUserProfile { error in
                DispatchQueue.main.async {
                    self.api.requestState = error
                    self.animateSpinner = false
                }
            }
        }
    }
}
