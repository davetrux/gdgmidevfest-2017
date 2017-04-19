FROM tomcat:8-jre8

ADD tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
ADD server.xml /usr/local/tomcat/conf/server.xml

ADD basic-spring/build/libs/*.war /usr/local/tomcat/webapps/
ADD google-spring/build/libs/*.war /usr/local/tomcat/webapps/
ADD hmac-java/build/libs/*.war /usr/local/tomcat/webapps/
ADD oauth-spring/build/libs/*.war /usr/local/tomcat/webapps/