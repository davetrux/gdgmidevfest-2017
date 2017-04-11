GDG Michigan DevFest 2017
================

Projects for my talk Android Authentication in the Web World

## android
Contains an example Android app that performs many of the authentication types discussed.

## services
Three different implementations of the same web services to handle the different authentication types

### hmac-java
A Java implementation of HMAC using JAX-RS/Jersey. This is a simplified implementation for demonstration, it is not secure.

### ntlm-dotnet
An ASP.NET 4 project containing an MVC forms-based login and the REST API via WCF.

### oauth-spring
A Java implementation of the services using the Spring Security framework. The framework sits in front of the services. Changing the configuration files changes the services from HTTP Basic, to Digest to OAuth.

This is not a secure implementation of OAuth due to the way logins are handled.