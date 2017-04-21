GDG Michigan DevFest 2017
================

Projects for my talk _Android Authentication in the Web World_

[Additional resources and slides](https://davidtruxall.com/resources-android-authentication/)

## Android
Contains an example Android app that performs the authentication types discussed in my talk.

## Services
Three different implementations of the same web services to handle the different authentication types. None of them use an actual user store, they all accept hard-coded credentials and are only useful for exercising the Android client. Do not consider the service code production-ready, it's only for demos.

There is a Dockerfile which hosts all the services in Tomcat. Simply build all the services with Gradle from the root, then execute the docker.sh script to create the container.

### hmac-java
A Java implementation of HMAC using Spring. This is a simplified implementation for demonstration, it is not secure. It lacks the use of nonces.

### ntlm-dotnet
An ASP.NET 4 project containing an MVC REST API.

### basic-spring
A Java implementation of the services using the Spring Security framework. The framework sits in front of the services. This configuration performs both HTTP Basic and HTTP Digest authentication.

### oath-spring
A Java implementation of the services using the Spring Security framework. The framework sits in front of the services. This configuration performs a simplified and insecure version of oAuth2 as everything happens in the query string. Useful only for demoing the Android app and oAuth process. Not for actual use.
