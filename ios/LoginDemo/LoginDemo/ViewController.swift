//
//  ViewController.swift
//  LoginDemo
//
//  Created by David Truxall on 10/13/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var loginType: UISegmentedControl!
    
    @IBOutlet weak var userName: UITextField!
    
    @IBOutlet weak var password: UITextField!
    
    @IBOutlet weak var results: UITableView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func attemptLogin(sender: AnyObject) {
        
        let user = userName.text
        let pwd = password.text
        
        let service = Service()
        
        service.getPersons(user, password: pwd, self.loadPersons)

    }
    
    private func loadPersons(persons: NSArray?, error: NSError?){
        let i = 9
    }
    
}

