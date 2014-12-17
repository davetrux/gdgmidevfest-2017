//
//  BasicService.swift
//  LoginDemo
//
//  Created by David Truxall on 12/15/14.
//  Copyright (c) 2014 Hewlett-Packard Company. All rights reserved.
//

import Foundation

typealias JSONDictionary = Dictionary<String, AnyObject>
typealias JSONArray = Array<AnyObject>

class BasicService: NSObject, NSURLConnectionDataDelegate {
    
    var settings:Settings!
    
    let responseData = NSMutableData()
    var statusCode:Int = -1
    
    typealias APICallback = ((AnyObject?, NSError?) -> ())
    var callback: APICallback! = nil
    
    override
    init(){
        self.settings = Settings()
    }
    

    func connection(connection: NSURLConnection!, didReceiveResponse response: NSURLResponse!) {
        let httpResponse = response as NSHTTPURLResponse
        statusCode = httpResponse.statusCode
        switch (httpResponse.statusCode) {
        case 201, 200, 401:
            self.responseData.length = 0
        default:
            println("ignore")
        }
    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!) {
        self.responseData.appendData(data)
    }

    func connectionDidFinishLoading(connection: NSURLConnection!) {
        var error: NSError?
        var json : AnyObject! = NSJSONSerialization.JSONObjectWithData(self.responseData, options: NSJSONReadingOptions.MutableLeaves, error: &error)
        
        if error != nil {
            callback(nil, error)
            return
        }
        
        switch(statusCode) {
        case (200):
            callback(self.handleData(json), nil)
        case (400):
            self.callback(nil, self.handleBadRequest(json))
        case (500):
            callback(nil, self.handleServerError(json))
        case (401):
            callback(nil, self.handleAuthError(json))
        default:
            // Unknown Error??
            callback(nil, nil)
        }
    }
    
    func handleData(json: AnyObject) -> NSArray {
        return NSArray()
    }

    
    func handleServerError(json: AnyObject) -> NSError {
        if let resultObj = json as? JSONDictionary {
            
            if let messageObj: AnyObject = resultObj["error"] {
                if let message = messageObj as? String {
                    return NSError(domain:"server", code:500, userInfo:["error": message])
                }
            }
        }
        return NSError(domain:"server", code:500, userInfo:["error": "Bad Request"])
    }
    
    func handleBadRequest(json: AnyObject) -> NSError {
        if let resultObj = json as? JSONDictionary {
            
            if let messageObj: AnyObject = resultObj["error"] {
                if let message = messageObj as? String {
                    return NSError(domain:"format", code:400, userInfo:["error": message])
                }
            }
        }
        return NSError(domain:"format", code:400, userInfo:["error": "Bad Request"])
    }

    

    func handleAuthError(json: AnyObject) -> NSError {
        if let resultObj = json as? JSONDictionary {

            if let messageObj: AnyObject = resultObj["error"] {
                if let message = messageObj as? String {
                    return NSError(domain:"auth", code:401, userInfo:["error": message])
                }
            }
        }
        return NSError(domain:"auth", code:401, userInfo:["error": "Authentication error"])
    }

    private func hTTPGetRequest(callback: APICallback, url: String) {
        self.callback = callback
        
        var nsURL = NSURL(string: url)
        
        let request = NSURLRequest(URL: nsURL!)
        
        let conn = NSURLConnection(request: request, delegate:self)
        if (conn == nil) {
            callback(nil, nil)
        }
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

            
            callback(response)
        }
        task.resume()
    }
    
    
    private func createAuthHeader(userName: String, password:String) -> String {
        let authString = userName + ":" + password
        let authData = authString.dataUsingEncoding(NSUTF8StringEncoding)
        let base64EncodedCredential = authData!.base64EncodedStringWithOptions(nil)
        return "Basic \(base64EncodedCredential)"
    }
}