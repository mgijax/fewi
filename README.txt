Prototype for MGI Spring/Hibernate WI

This is the MGI prototype skeleton for integrating Spring 3.0.3 and Hibernate 3.3.2. It contains the necessary configuration files and libraries to begin experimenting with the aforementioned technologies. There are a few existing pages that can serve as examples, but these are in an incomplete state and should only be used as examples.
Configuration:

The web application configuration is done for you. You should only need to modify:

    * ./build.properties - set jboss.dir to be the jboss configuration directory you will be using.
    * ./mysql-ds.xml - edit according to the mysql database you wish to connect to. This can be deployed to the deploy.dir using the deploy-ds ant task.
    * ./www/WEB-INF/properties/jdbc.properties - edit if you change the jndi-name in the datasource deployment file, or if you wish to change any of the other settings in this file.
    * execute the deploy-ds ant task to deploy the jdbc driver and datasource files.

Link to the existing Marker Query Form.
JBoss 5.1 configuration:

I have used the default deployment configuration for my prototype. You will need to do a couple of things to prepare JBoss.

    * If you didn't do so above, execute the deploy-ds ant task to deploy the jdbc driver and datasource files.
    * remove JBoss/common/lib/hibernate-validator.jar. Replace it with ./lib/hibernate-validator-4.1.0.Beta1.jar


