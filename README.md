# facturas

This is a small spring-boot-application that reads open data from the electricity company of Ambato, Ecuador and displays the data as json.


## Quick start
It can be built easily with the included gradlewrapper by invoking:

    ./gradlew clean build

and then

    java -jar facturas-0.0.1-SNAPSHOT.jar

## Configuration

There is some basic configuration options available in the `src/main/resources/application.properties`, but it will work out of the box creating a h2 database in the users home directory.
