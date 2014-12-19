//
//  Service.swift
//  LoginDemo
//
//  Created by David Truxall on 12/19/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import Foundation

typealias JSONDictionary = Dictionary<String, AnyObject>
typealias JSONArray = Array<AnyObject>

class Service : NSObject, NSURLSessionDelegate, NSURLSessionDataDelegate, NSURLSessionTaskDelegate {
    
    typealias APICallback = ((NSArray?, NSError?) -> ())
    var callback: APICallback! = nil
    
    var settings:Settings!
    
    let responseData = NSMutableData()
    var statusCode:Int = -1
    
    override
    init(){
        self.settings = Settings()
    }
    
    //NSURLSessionDelegate
    func URLSession(session: NSURLSession, didReceiveChallenge challenge: NSURLAuthenticationChallenge, completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential!) -> Void) {
 
            completionHandler(NSURLSessionAuthChallengeDisposition.UseCredential, nil);
    }
    
    //NSURLSessionDataDelegate
    func URLSession(session: NSURLSession, dataTask: NSURLSessionDataTask, didReceiveData data: NSData){
        
        self.responseData.appendData(data)
        
    }
    
    
    func URLSession(session: NSURLSession, dataTask: NSURLSessionDataTask, didReceiveResponse response: NSURLResponse,
        completionHandler: (NSURLSessionResponseDisposition) -> Void) {
       
            let httpResponse = dataTask.response as NSHTTPURLResponse
            statusCode = httpResponse.statusCode
            switch (httpResponse.statusCode) {
            case 201, 200, 401:
                self.responseData.length = 0
            default:
                println("unexpected response code")
            }
            
            completionHandler(NSURLSessionResponseDisposition.Allow);
    }
    
    //
    func URLSession(session: NSURLSession, task: NSURLSessionTask, didCompleteWithError error: NSError?) {
        
        if(error != nil) {
            
            callback(nil, error)
        
        } else {
            
            var err: NSError?

            var json = NSJSONSerialization.JSONObjectWithData(self.responseData, options: .MutableContainers, error: &err) as NSArray

            if err != nil && (statusCode == 200 || statusCode == 500) {
                callback(nil, err)
                return
            }
            
            switch(statusCode) {
            case (200):
                callback(self.handleData(json), nil)
            case (400):
                self.callback(nil, self.handleBadRequest())
            case (500):
                callback(nil, self.handleServerError(json))
            case (401):
                callback(nil, self.handleAuthError())
            default:
                // Unknown Error??
                callback(nil, nil)
            }
        }
    }
    
    
    private func handleData(json: NSArray) -> NSArray {
        let result = NSMutableArray()
        
        for dict in json {
            let item: NSDictionary = dict as NSDictionary

            let person = Person(first: item.valueForKey("firstName") as String, last: item.valueForKey("lastName") as String, gender: item.valueForKey("gender") as String)
            result.addObject(person)
        }
        
        return result
    }
    
    
    private func handleServerError(json: AnyObject) -> NSError {
        if let resultObj = json as? JSONDictionary {
            
            if let messageObj: AnyObject = resultObj["error"] {
                if let message = messageObj as? String {
                    return NSError(domain:"server", code:500, userInfo:["error": message])
                }
            }
        }
        return NSError(domain:"server", code:500, userInfo:["error": "Bad Request"])
    }
    
    private func handleBadRequest() -> NSError {
        
        return NSError(domain:"format", code:400, userInfo:["error": "Bad Request"])
    }
    
    
    private func handleAuthError() -> NSError {
        
        return NSError(domain:"auth", code:401, userInfo:["error": "Authentication error"])
    }
    
    
    func getPersons(userName:String, password:String, callback:APICallback) {
        println("get persons")
        
        self.callback = callback
        
        self.httpRequest(settings.basicUrl, userName: userName, password: password)
        
    }
    
    private func httpRequest(url:String, userName:String, password:String) {
        var nsURL = NSURL(string: url)
        let authHeader = createAuthHeader(userName, password: password)
        
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        config.HTTPAdditionalHeaders = ["Authorization" : authHeader]
        
        
        let session = NSURLSession(configuration: config, delegate: self, delegateQueue: nil)
        let task = session.dataTaskWithURL(nsURL!)
        
        task.resume()
        
    }

    private func createAuthHeader(userName: String, password:String) -> String {
        let authString = userName + ":" + password
        let authData = authString.dataUsingEncoding(NSUTF8StringEncoding)
        let base64EncodedCredential = authData!.base64EncodedStringWithOptions(nil)
        return "Basic \(base64EncodedCredential)"
    }
}