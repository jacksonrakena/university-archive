//
//  TodayViewController.swift
//  TimetableTodayExtension
//
//  Created by Jackson Rakena on 15/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import UIKit
import NotificationCenter
import SwiftUI
import CoreData
import Combine
import Foundation

class TodayViewController: UITableViewController, NCWidgetProviding {
    var data: [PeriodSlot] = []
    var id: Int
    var type: String
    var ud: UserDefaults? = UserDefaults.init(suiteName: "group.quicktable")
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
       return data.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TodayItem", for: indexPath) as! TodayTableViewCell
        let item = data[indexPath.row]
        cell.periodName.text = "\(item.SubjectDesc) (Room \(item.Room))"
        return cell
    }
    
    required init?(coder: NSCoder) {
        print("TVC Init")
        
        guard let ids = ud?.string(forKey: "id"), let id0 = Int(ids) else {
            return nil
        }
        self.id = id0
        guard let type = ud?.string(forKey: "type") else {
            return nil
        }
        self.type = type
        super.init(coder: coder)
    }
    
    override func viewDidAppear(_ animated: Bool)
    {
        super.viewDidAppear(animated)
        extensionContext?.widgetLargestAvailableDisplayMode = .expanded
        self.preferredContentSize.height = 280
    }
    
    func widgetMarginInsets(forProposedMarginInsets defaultMarginInsets: UIEdgeInsets) -> (UIEdgeInsets)
    {
        return UIEdgeInsets.zero
    }
    
    @available(iOSApplicationExtension 10.0, *)
    func widgetActiveDisplayModeDidChange(_ activeDisplayMode: NCWidgetDisplayMode, withMaximumSize maxSize: CGSize)
    {
        if activeDisplayMode == .expanded {
            preferredContentSize = CGSize(width: maxSize.width, height: 280)
        }
        else if activeDisplayMode == .compact {
            preferredContentSize = maxSize
        }
    }
    
    func widgetPerformUpdate(completionHandler: (@escaping (NCUpdateResult) -> Void)) {
        ApiManager.updateTimetableNoCacheStore(id: self.id, date: Date(), type: self.type, onComplete: { (p) in
            DispatchQueue.main.async {
                print(p.count)
                self.data = p.filter {
                    !$0.SubjectDesc.isEmpty
                }
                print(self.data.count)
                self.tableView.reloadData()
                completionHandler(NCUpdateResult.newData)
            }
            
        }, onError: { err in
            completionHandler(NCUpdateResult.failed)
        })
    }
}
