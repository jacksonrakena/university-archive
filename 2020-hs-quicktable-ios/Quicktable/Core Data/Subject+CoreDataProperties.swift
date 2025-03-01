//
//  Subject+CoreDataProperties.swift
//  Quicktable
//
//  Created by Jackson Rakena on 12/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//
//

import Foundation
import CoreData


extension Subject {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Subject> {
        return NSFetchRequest<Subject>(entityName: "Subject")
    }

    @NSManaged public var classNumber: String?
    @NSManaged public var longName: String?
    @NSManaged public var shortName: String?
    @NSManaged public var teacherEmail: String?
    @NSManaged public var teacherName: String?
    @NSManaged public var yearGroup: String?
    @NSManaged public var colorHex: String?
    @NSManaged public var assignments: Set<Assignment>?

}

// MARK: Generated accessors for assignments
extension Subject {

    @objc(addAssignmentsObject:)
    @NSManaged public func addToAssignments(_ value: Assignment)

    @objc(removeAssignmentsObject:)
    @NSManaged public func removeFromAssignments(_ value: Assignment)

    @objc(addAssignments:)
    @NSManaged public func addToAssignments(_ values: NSSet)

    @objc(removeAssignments:)
    @NSManaged public func removeFromAssignments(_ values: NSSet)

}
