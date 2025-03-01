//
//  ProfileTabView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 4/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI

struct ProfileTabView: View {
    @EnvironmentObject var api: ApiManager
    var body: some View {
        if self.api.userProfile != nil {
            return AnyView(VStack {
                AsyncImage(url: URL(string: "https://spider.scotscollege.school.nz/Spider2011/Handlers/ImageHandler.ashx?imageHeight=200&arg=\(self.api.userProfile!.userInformation.ImageNameEncrypted)")!, placeholder: Text("Loading image...")).frame(width: 160, height: 193, alignment: .center).clipShape(Circle()).overlay(
                    Circle().stroke(Color.white, lineWidth: 4))
                .shadow(radius: 10)
                Text("Hi, \(api.userProfile!.userInformation.KnownAs)!").font(.title).bold()
                Text("Member code: \(api.userProfile!.memberCode)")
                Text("Tutor: \(api.userProfile!.userInformation.HomeTeacher)")
                Text("Dean: \(api.userProfile!.userInformation.Dean)")
                Text("Birth date: \(api.userProfile!.userInformation.birthDate.string(inFormat: "MMMM d, yyyy"))")
                Text("Exam number: \(api.userProfile!.userInformation.ExamNo)")
                Text("Student ID: \(api.userProfile!.userInformation.StudentID.description)")
            })
        } else {
            return AnyView(LoginView().environmentObject(self.api))
        }
    }
}
