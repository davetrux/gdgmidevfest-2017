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
    
    typealias APICallback = ((Array<Person>?, NSError?) -> ())
    
    enum LoginType {
        case Basic
        case Ntlm
        case oAuth
        case Digest
    }
    
    var authType: LoginType!
    
    var callback: APICallback! = nil
    
    var settings:Settings!
    
    let responseData = NSMutableData()
    var statusCode:Int = -1
    
    var userName: String!
    var password: String!
    
    override
    init(){
        self.settings = Settings()
    }
    
    //NSURLSessionTaskDelegate for Basic, Digest
    func URLSession(session: NSURLSession, task: NSURLSessionTask, didReceiveChallenge challenge: NSURLAuthenticationChallenge,completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential!) -> Void){
        var cred = NSURLCredential(user: self.userName, password: self.password, persistence:  NSURLCredentialPersistence.ForSession)
        
        completionHandler(NSURLSessionAuthChallengeDisposition.UseCredential, cred);
    }
    
    //NSURLSessionDelegate for NTLM
    func URLSession(session: NSURLSession, didReceiveChallenge challenge: NSURLAuthenticationChallenge, completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential!) -> Void) {
 
        var cred = NSURLCredential(user: self.userName, password: self.password, persistence:  NSURLCredentialPersistence.ForSession)
        
            completionHandler(NSURLSessionAuthChallengeDisposition.UseCredential, cred);
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
            
            //varNSString *strData = [[NSString alloc]initWithData:returnData encoding:NSUTF8StringEncoding];
            var strData = NSString(data: self.responseData, encoding: NSUTF8StringEncoding)
            NSLog("%@",strData!);
            
           
            
            switch(statusCode)
            {
            case (200):
                var err: NSError?
                
                var json = NSJSONSerialization.JSONObjectWithData(self.responseData, options: .MutableContainers, error: &err) as NSArray
                
                if err != nil && (statusCode == 200 || statusCode == 500) {
                    callback(nil, err)
                    return
                }
                callback(self.handleData(json), nil)
            case (400):
                self.callback(nil, self.handleBadRequest())
            //case (500):
                //callback(nil, self.handleServerError(json))
            case (401):
                callback(nil, self.handleAuthError())
            default:
                // Unknown Error??
                callback(nil, nil)
            }
        }
    }
    
    
    private func handleData(json: NSArray) -> Array<Person> {
        var result = [Person]()
        
        for dict in json {
            let item: NSDictionary = dict as NSDictionary

            let person = Person(first: item.valueForKey("firstName") as String, last: item.valueForKey("lastName") as String, gender: item.valueForKey("gender") as String)
            result.append(person)
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
    
    
    func getPersons(userName:String, password:String, auth:LoginType, callback:APICallback) {
        println("get persons")
        
        self.authType = auth
        
        self.callback = callback
        
        var url: String
        
        switch(auth)
        {
            case LoginType.Basic:
                url = settings.basicUrl
                self.httpNtlmRequest(url, userName: userName, password: password)
            case LoginType.Ntlm:
                url = settings.ntlmUrl
                self.httpNtlmRequest(url, userName: userName, password: password)
            case LoginType.oAuth:
                url = settings.oauthUrl
            
            case LoginType.Digest:
                url = settings.digestUrl
                self.httpNtlmRequest(url, userName: userName, password: password)
        }
        
        
        
    }
    
    private func httpNtlmRequest(url:String, userName:String, password:String) {
        var nsURL = NSURL(string: url)
        
        self.userName = userName
        self.password = password
        
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()

        let session = NSURLSession(configuration: config, delegate: self, delegateQueue: nil)
        let task = session.dataTaskWithURL(nsURL!)
        
        task.resume()
        
    }
    
    private func httpBasicRequest(url:String, userName:String, password:String) {
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