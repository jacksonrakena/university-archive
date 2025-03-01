//
//  Assignment+CoreDataProperties.swift
//  Quicktable
//
//  Created by Jackson Rakena on 6/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//
//

import Foundation
import CoreData


extension Assignment {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Assignment> {
        return NSFetchRequest<Assignment>(entityName: "Assignment")
    }

    @NSManaged public var assigned: Date?
    @NSManaged public var due: Date?
    @NSManaged public var id: UUID?
    @NSManaged public var name: String?
    @NSManaged public var notes: String?
    @NSManaged public var subject: Subject?

}
