//
//  Person.swift
//  LoginDemo
//
//  Created by David Truxall on 12/15/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import Foundation

class Person {

    var firstName:String
    var lastName:String
    var gender:String
    
    init(first:String,last:String,gender:String) {

        self.firstName = first
        self.lastName = last
        self.gender = gender
    }
}