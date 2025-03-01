//
//  Utils.swift
//  SchoolLink
//
//  Created by Jackson Rakena on 30/05/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import Foundation
import Combine
import SwiftUI
import UIKit
import CoreData
import EventKit

enum Direction {
    case back
    case nearest
    case forward
}

class Constants {
    static var abyssalDecoder: JSONDecoder { get {
        let decoder = JSONDecoder()
        let format = DateFormatter()
        format.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        decoder.dateDecodingStrategy = .formatted(format)
        return decoder
    }}
    static var spiderDecoder: JSONDecoder { get {
            let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .formatted(pcsDateFormatter)
        return decoder
        }
    }
    
    static var pcsDateFormatter: DateFormatter { get {
        let df = DateFormatter()
        df.dateFormat = pcsDateFormat
        return df
        }}
    
    static let pcsDateFormat = "dd/MM/YYYY"
}

extension Date {
    func string(inFormat in0: String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = in0
        return dateFormatter.string(from: self)
    }
    
    func getWeekday(_ direction: Direction) -> Date {
        var tempDate = self
        if Calendar.current.isDateInWeekend(tempDate) {
            if direction == .nearest {
                let forward = self.getWeekday(.forward)
                let back = self.getWeekday(.back)
                if (self.distance(to: forward) > self.distance(to: back)) {
                    return back
                }
                return forward
            }
            
            while Calendar.current.isDateInWeekend(tempDate) {
                switch direction {
                case .back:
                    tempDate = tempDate.advanced(by: -86400)
                case .forward:
                    tempDate = tempDate.advanced(by: 86400)
                default:
                    tempDate = tempDate.advanced(by: 86400)
                }
            }
        } else {
            return self
        }
        return tempDate
    }
}

extension String {
    func date(inFormat in0: String) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        dateFormatter.dateFormat = in0
        return dateFormatter.date(from: self)
    }
    
    func asPcsDate() -> Date? {
        let components = self.components(separatedBy: "/")
        return Calendar.current.date(from: DateComponents(calendar: Calendar.current, timeZone: TimeZone.current, year: Int(components[2]), month: Int(components[1]), day: Int(components[0])))
    }
    
    func randomUIColor() -> UIColor {
        var total: Int = 0
        for u in self.unicodeScalars {
            total += Int(UInt32(u))
        }
        srand48(total * 200)
        let r = CGFloat(drand48())
        srand48(total)
        let g = CGFloat(drand48())
        srand48(total / 200)
        let b = CGFloat(drand48())
        return UIColor(red: r, green: g, blue: b, alpha: 1)
    }
    
    func randomColor() -> Color {
        return Color(self.randomUIColor()).opacity(1)
    }
}

extension Publishers {
    // 1.
    static var keyboardHeight: AnyPublisher<CGFloat, Never> {
        // 2.
        let willShow = NotificationCenter.default.publisher(for: UIApplication.keyboardWillShowNotification)
            .map { $0.keyboardHeight }
        
        let willHide = NotificationCenter.default.publisher(for: UIApplication.keyboardWillHideNotification)
            .map { _ in CGFloat(0) }
        
        // 3.
        return MergeMany(willShow, willHide)
            .eraseToAnyPublisher()
    }
}
extension Notification {
    var keyboardHeight: CGFloat {
        return (userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect)?.height ?? 0
    }
}

struct KeyboardAdaptive: ViewModifier {
    @State private var keyboardHeight: CGFloat = 0

    func body(content: Content) -> some View {
        content
            .padding(.bottom, keyboardHeight)
            .onReceive(Publishers.keyboardHeight) { self.keyboardHeight = $0 }
            .animation(.easeOut(duration: 0.20))
    }
}

extension View {
    func keyboardAdaptive() -> some View {
        ModifiedContent(content: self, modifier: KeyboardAdaptive())
    }
}

struct ModalView<T: View>: UIViewControllerRepresentable {
    let view: T
    @Binding var isModal: Bool
    let onDismissalAttempt: (()->())?

    func makeUIViewController(context: Context) -> UIHostingController<T> {
        UIHostingController(rootView: view)
    }

    func updateUIViewController(_ uiViewController: UIHostingController<T>, context: Context) {
        uiViewController.parent?.presentationController?.delegate = context.coordinator
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UIAdaptivePresentationControllerDelegate {
        let modalView: ModalView

        init(_ modalView: ModalView) {
            self.modalView = modalView
        }

        func presentationControllerShouldDismiss(_ presentationController: UIPresentationController) -> Bool {
            !modalView.isModal
        }

        func presentationControllerDidAttemptToDismiss(_ presentationController: UIPresentationController) {
            modalView.onDismissalAttempt?()
        }
    }
}

extension View {
    func presentation(isModal: Binding<Bool>, onDismissalAttempt: (()->())? = nil) -> some View {
        ModalView(view: self, isModal: isModal, onDismissalAttempt: onDismissalAttempt)
    }
}

extension NSManagedObjectContext {
    func fetch<T: NSManagedObject>(predicate: NSPredicate? = nil) throws -> [T] {
        let request: NSFetchRequest<T> = NSFetchRequest<T>()
        request.entity = T.entity()
        request.predicate = predicate
        return try self.fetch(request)
    }
}

class ImageLoader: ObservableObject {
    @Published var image: UIImage?
    private let url: URL

    init(url: URL) {
        self.url = url
    }
    
    private var cancellable: AnyCancellable?
    
    deinit {
        cancellable?.cancel()
    }

    func load() {
        cancellable = URLSession.shared.dataTaskPublisher(for: url)
            .map { UIImage(data: $0.data) }
            .replaceError(with: nil)
            .receive(on: DispatchQueue.main)
            .assign(to: \.image, on: self)
    }
    
    func cancel() {
        cancellable?.cancel()
    }
}

struct AsyncImage<Placeholder: View>: View {
    @ObservedObject private var loader: ImageLoader
    private let placeholder: Placeholder?
    
    init(url: URL, placeholder: Placeholder? = nil) {
        loader = ImageLoader(url: url)
        self.placeholder = placeholder
    }

    var body: some View {
        image
            .onAppear(perform: loader.load)
            .onDisappear(perform: loader.cancel)
    }
    
    private var image: some View {
        Group {
            if loader.image != nil {
                Image(uiImage: loader.image!)
                    .resizable()
            } else {
                placeholder
            }
        }
    }
}

extension UIColor {

    // MARK: - Initialization

    convenience init?(hex: String) {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }

        if ((cString.count) != 6) {
            return nil
        }

        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)

        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}

extension UIColor {
    func toHexString() -> String {
        var r:CGFloat = 0
        var g:CGFloat = 0
        var b:CGFloat = 0
        var a:CGFloat = 0

        getRed(&r, green: &g, blue: &b, alpha: &a)

        let rgb:Int = (Int)(r*255)<<16 | (Int)(g*255)<<8 | (Int)(b*255)<<0

        return String(format:"#%06x", rgb)
    }
}

extension UIColor {
    public final func toHSBComponents() -> (h: CGFloat, s: CGFloat, b: CGFloat) {
      var h: CGFloat = 0.0
      var s: CGFloat = 0.0
      var b: CGFloat = 0.0

      #if os(iOS) || os(tvOS) || os(watchOS)
        getHue(&h, saturation: &s, brightness: &b, alpha: nil)

        return (h: h, s: s, b: b)
      #elseif os(OSX)
        if isEqual(DynamicColor.black) {
          return (0.0, 0.0, 0.0)
        }
        else if isEqual(DynamicColor.white) {
          return (0.0, 0.0, 1.0)
        }

        getHue(&h, saturation: &s, brightness: &b, alpha: nil)

        return (h: h, s: s, b: b)
      #endif
    }
    
    public final var hueComponent: CGFloat {
      return toHSBComponents().h
    }

    /**
     The saturation component as CGFloat between 0.0 to 1.0.
     */
    public final var saturationComponent: CGFloat {
      return toHSBComponents().s
    }

    /**
     The brightness component as CGFloat between 0.0 to 1.0.
     */
    public final var brightnessComponent: CGFloat {
      return toHSBComponents().b
    }
}
