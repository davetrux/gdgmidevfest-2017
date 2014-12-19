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
        let i = 8
    }
    
    
    func getPersons(userName:String, password:String, callback:APICallback) {
        println("get persons")
        self.httpRequest(settings.basicUrl, userName: userName, password: password, callback: callback)
        //self.httpGetRequest(callback, url: self.settings.basicUrl)
        
    }
    
    func httpRequest(url:String, userName:String, password:String, callback:APICallback) {
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