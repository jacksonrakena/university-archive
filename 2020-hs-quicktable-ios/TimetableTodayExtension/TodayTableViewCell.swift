//
//  TodayTableViewCell.swift
//  TimetableTodayExtension
//
//  Created by Jackson Rakena on 15/06/20.
//  Copyright © 2020 Jackson Rakena. All rights reserved.
//

import UIKit

class TodayTableViewCell: UITableViewCell {

    @IBOutlet weak var periodName: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
