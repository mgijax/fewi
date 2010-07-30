<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head><title>Spring 3.0.3 / Hibernate 3.3.2 Prototype</title></head>
  <body>
    <h1>Prototype for MGI Spring/Hibernate WI</h1>
    <p>This is the MGI prototype skeleton for integrating Spring 3.0.3 and Hibernate 3.3.2.  It contains 
    the necessary configuration files and libraries to begin experimenting with the aforementioned 
    technologies.  There are a few existing pages that can serve as examples, but these are in an 
    incomplete state and should only be used as examples.</p>
    <h3>Configuration:</h3>
    <p>The web application configuration is done for you.  You should only need to modify:
	<ul>
			<li>./build.properties - set jboss.dir to be the jboss configuration directory you will be using.</li>
			<li>./mysql-ds.xml - edit according to the mysql database you wish to connect to. 
			This can be deployed to the deploy.dir using the deploy-ds ant task.</li>
			<li>./www/WEB-INF/properties/jdbc.properties - edit if you change the jndi-name 
			in the datasource deployment file, or if you wish to change any of the other settings 
			in this file.</li>
			<li>execute the deploy-ds ant task to deploy the jdbc driver and datasource files.</li>
			<li>copy the following files from your jboss/common/lib/ into ./lib: ejb3-persistance.jar, servlet-api.jar, slf4j-api-1.5.8.jar</li>
	</ul>
 
	 </p>
    <p><a href='/fewi/mgi/marker/'>Link</a> to the existing Marker Query Form.</p>
    
    <h3>JBoss 5.1 configuration:</h3>
    <p>I have used the default deployment configuration for my prototype.  You will 
    need to do a couple of things to prepare JBoss.
	<ul>
		<li>If you didn't do so above, execute the deploy-ds ant task to deploy the 
		jdbc driver and datasource files.</li>
		<li>remove JBoss/common/lib/hibernate-validator.jar.  Replace it with 
		./lib/hibernate-validator-4.1.0.Beta1.jar</li>
	</ul>
	</p>
  </body>
</html>