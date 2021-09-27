# WebMoney

This project provides a JAX-RS interface for a personal finance web service

To set this up, your JavaEE server needs an authentication user in the "file" realm.
You also need to create a directory with read-write permission for the JavaEE process, and update the context-param "directory" in the "WEB-INF/web.xml" configuration file with its path, including the closing "/".

This application has been tested on Glassfish 6.