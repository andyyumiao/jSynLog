# jSynLog
A distributed log level control framework based on zookeeper, it can switch the log level on distributed servers

# step1
Init the maven pom file, you should add in AngularJs library if you need the web console
# step2
Put the classes into your java src and make sure there's nothing compile errors: ZkLogLevelService & ZkLogLevelServiceImpl
# step3
Set up the conf.properties
# step4
Set up spring environment
# step5
Just start up your applications. 
You can run the ZkLogLevelService instance on distributed servers

# Q&A
You can setup one web console: To read the data and change the application log configuration on zookeeper


