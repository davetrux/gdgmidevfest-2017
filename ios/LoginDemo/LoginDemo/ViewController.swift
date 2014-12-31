//
//  ViewController.swift
//  LoginDemo
//
//  Created by David Truxall on 10/13/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var loginType: UISegmentedControl!
    
    @IBOutlet weak var userName: UITextField!
    
    @IBOutlet weak var password: UITextField!
    
    @IBOutlet weak var results: UITableView!
    
    var personList: Array<Person>?
    
    let cellIdentifier = "cellIdentifier"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Register the UITableViewCell class with the tableView
        self.results?.registerClass(UITableViewCell.self, forCellReuseIdentifier: self.cellIdentifier)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func attemptLogin(sender: AnyObject) {
        var auth: Service.LoginType!
        
        switch self.loginType.selectedSegmentIndex
        {
            case 0:
                auth = Service.LoginType.Basic
            case 1:
                auth = Service.LoginType.Ntlm
            case 2:
                auth = Service.LoginType.oAuth
            default:
                auth = Service.LoginType.Basic
        }
        
        let user = userName.text
        let pwd = password.text
        
        let service = Service()
        
        self.personList?.removeAll(keepCapacity: true)
        
        service.getPersons(user, password: pwd, auth: auth, self.loadPersons)

    }
    
    private func loadPersons(persons: Array<Person>?, error: NSError?){
        self.personList = persons
        
        dispatch_async(dispatch_get_main_queue(), {self.results.reloadData}())
    }
    
    // UITableViewDataSource methods
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return (personList?.count ?? 0)
        
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        var cell = tableView.dequeueReusableCellWithIdentifier(self.cellIdentifier) as UITableViewCell
        
        if personList?.count > 0 {
            cell.textLabel?.text =  "\(personList![indexPath.row].firstName) \(personList![indexPath.row].lastName)"
        }
        
        return cell
    }
    
    // UITableViewDelegate methods
    
    func tableView(tableView: UITableView!, didSelectRowAtIndexPath indexPath: NSIndexPath!) {
        
        
    }
    
}

