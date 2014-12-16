//
//  BasicService.swift
//  LoginDemo
//
//  Created by David Truxall on 12/15/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import Foundation

class BasicService {
    
    var settings:Settings!
    
    init(){
        self.settings = Settings()
    }
    
    func getPersons(userName:String, password:String, callback:(NSDictionary)->()) {
        println("get persons")
        request(settings.basicUrl, userName: userName, password: password, callback)
    }
    
    func request(url:String, userName:String, password:String, callback:(NSDictionary)->()) {
        var nsURL = NSURL(string: url)
        let authHeader = createAuthHeader(userName, password: password)
        
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        config.HTTPAdditionalHeaders = ["Authorization" : authHeader]
        let session = NSURLSession(configuration: config)
        
        println(callback)
        let task = session.dataTaskWithURL(nsURL!) {
            (data,response,error) in
            var error:NSError?
            var response = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers, error: &error) as NSDictionary
            
            let persons = self.convertJson(response)
            
            callback(persons)
        }
        task.resume()
    }
    
    private func convertJson( json: NSDictionary) -> NSDictionary {
        
        let result = NSDictionary()
        
        for item in json {
            
        }
        
        return result
    }
    
    private func createAuthHeader(userName: String, password:String) -> String {
        let authString = userName + ":" + password
        let authData = authString.dataUsingEncoding(NSUTF8StringEncoding)
        let base64EncodedCredential = authData!.base64EncodedStringWithOptions(nil)
        return "Basic \(base64EncodedCredential)"
    }
}