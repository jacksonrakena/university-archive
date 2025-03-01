//
//  SetColourView.swift
//  Quicktable
//
//  Created by Jackson Rakena on 12/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import SwiftUI
import CoreData

struct GradientPickerView : View {
    @State var startLocation: CGFloat // 2
    @State private var dragOffset: CGSize = .zero
    let linearGradientHeight: CGFloat = 200
    
    // 0 = top
    // 200 = bottom
    private func calculateCurrentPosition() -> CGFloat {
        let offset = startLocation + dragOffset.height // Using our starting point, see how far we've dragged +- from there
        let maxY = max(0, offset) // We want to always be greater than 0, even if their finger goes outside our view
        let minY = min(maxY, linearGradientHeight) // We want to always max at 200 even if the finger is outside the view.
        
        return minY
    }
    
    @State private var isDragging: Bool = false

    private var circleWidth: CGFloat {
       isDragging ? 35 : 15
    }
    
    let gradient: [Color]
    @Binding var position: CGFloat
    let colorCalculate: (CGFloat) -> Color
    
    var body: some View {
        ZStack(alignment: .top) {
            LinearGradient(gradient: Gradient(colors: gradient), startPoint: .top, endPoint: .bottom)
                .frame(width: 40, height: linearGradientHeight)
                .cornerRadius(5)
                .shadow(radius: 8)
                .overlay(RoundedRectangle(cornerRadius: 5).stroke(Color.white, lineWidth: 2.0))
            .gesture(
                DragGesture().onChanged({ (value) in
                    self.dragOffset = value.translation
                    self.startLocation = value.startLocation.y
                    self.position = self.calculateCurrentPosition() / self.linearGradientHeight
                    self.isDragging = true
                }).onEnded({ (_) in
                    self.isDragging = false
                })
            )
        
        Circle()
            .foregroundColor(colorCalculate(self.position))
        .frame(width: self.circleWidth, height: self.circleWidth, alignment: .center)
        .shadow(radius: 5)
        .overlay(
            RoundedRectangle(cornerRadius: self.circleWidth / 2.0).stroke(Color.white, lineWidth: 2.0)
        )
        .offset(x: self.isDragging ? self.circleWidth : 0.0, y: self.calculateCurrentPosition() - self.circleWidth / 2)
        .animation(Animation.spring().speed(2))
        }
    }
}

struct SetColourView: View {
    @Environment(\.managedObjectContext) var moc: NSManagedObjectContext
    @State var hue: CGFloat = 0
    @State var brightness: CGFloat = 0
    @State var saturation: CGFloat = 0
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @EnvironmentObject var api: ApiManager
    
    @FetchRequest(entity: Subject.entity(), sortDescriptors: []) var subjects: FetchedResults<Subject>
    var subject: Subject
    
    var slot: PeriodSlot
    
    var colorGrad: [Color] = {
        // 1
        let hueValues = Array(0...359)
        // 2
        return hueValues.map {
            Color(UIColor(hue: CGFloat($0) / 359.0 ,
                          saturation: 1.0,
                          brightness: 1.0,
                          alpha: 1.0))
        }
    }()
    
    var body: some View {
        VStack {
            PeriodLink(period: slot, overrideColor: Color(hue: Double(hue), saturation: Double(saturation), brightness: Double(brightness)), isClickable: false).environmentObject(self.api).frame(height: 200)
            
            HStack(alignment: .center, spacing: 50) {
                GradientPickerView(startLocation: hue*200, gradient: colorGrad, position: $hue, colorCalculate: { Color(hue: Double($0), saturation: 1.0, brightness: 1.0)
                })
                GradientPickerView(startLocation: brightness*200, gradient: [Color.black, Color(hue: Double(hue), saturation: Double(saturation), brightness: 1.0)], position: $brightness, colorCalculate: { Color(hue: Double(self.hue), saturation: Double(self.saturation), brightness: Double($0))
                })
                GradientPickerView(startLocation: saturation*200, gradient: [Color.white, Color(hue: Double(hue), saturation: 1.0, brightness: Double(brightness))], position: $saturation, colorCalculate: {  Color(hue: Double(self.hue), saturation: Double($0), brightness: Double(self.brightness))
                })
            }
            Button(action: {
                let col0 = UIColor(hue: self.hue, saturation: self.saturation, brightness: self.brightness, alpha: 1.0)
                self.subject.colorHex = col0.toHexString()
                do {
                    try self.moc.save()
                }catch{}
                self.mode.wrappedValue.dismiss()
            }) {
                Text("Save")
            }.padding()
        }.navigationBarTitle("Colour")
    }
}
