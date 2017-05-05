# facturas

This is a small spring-boot-application that reads open data from the electricity company of Ambato, Ecuador and displays the data as json.

### Badges

[![travis-status](https://travis-ci.org/WhiteHatTux/facturas.svg?branch=master)](https://travis-ci.org/WhiteHatTux/facturas)
[![codecov](https://codecov.io/gh/WhiteHatTux/facturas/branch/master/graph/badge.svg)](https://codecov.io/gh/WhiteHatTux/facturas)

![powered by electricity](http://forthebadge.com/images/featured/featured-powered-by-electricity.svg)

![gluten free](http://forthebadge.com/images/featured/featured-gluten-free.svg)

## Quick start
It can be built easily with the included gradlewrapper by invoking:

    ./gradlew clean build

and then

    java -jar facturas-0.0.1-SNAPSHOT.jar

## Configuration

There are some basic configuration options available in the `src/main/resources/application.properties`, but it will work out of the box creating a h2 database in the users home directory.
