Metrics Client Incubator
===============================

<a href="https://raw.githubusercontent.com/ArpNetworking/metrics-incubator-extra/master/LICENSE">
    <img src="https://img.shields.io/hexpm/l/plug.svg"
         alt="License: Apache 2">
</a>
<a href="https://travis-ci.org/ArpNetworking/metrics-incubator-extra/">
    <img src="https://travis-ci.org/ArpNetworking/metrics-incubator-extra.png?branch=master"
         alt="Travis Build">
</a>
<a href="http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.arpnetworking.metrics.extras%22%20a%3A%22incubator-extra%22">
    <img src="https://img.shields.io/maven-central/v/com.arpnetworking.metrics.extras/incubator-extra.svg"
         alt="Maven Artifact">
</a>
<a href="http://www.javadoc.io/doc/com.arpnetworking.metrics.extras/incubator-extra">
    <img src="http://www.javadoc.io/badge/com.arpnetworking.metrics.extras/incubator-extra.svg"
         alt="Javadocs">
</a>

An incubator library for functionality that will eventually be in the core metrics client  or in independent formally 
defined extras.  The interfaces exposed by this library should be considered unstable may change from release to 
release.  The functionality contained within is experimental but the focus is on eventualy bringing this functionality 
into the core metrics libraries and freezing the interfaces.

Usage
-----

### Add Dependency

Determine the latest version of the library in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.arpnetworking.metrics.extras%22%20a%3A%22incubator-extra%22).

#### Maven

Add a dependency to your pom:

```xml
<dependency>
    <groupId>com.arpnetworking.metrics.extras</groupId>
    <artifactId>incubator-extra</artifactId>
    <version>VERSION</version>
</dependency>
```

The Maven Central repository is included by default.

#### Gradle

Add a dependency to your build.gradle:

    compile group: 'com.arpnetworking.metrics.extras', name: 'incubator-extra', version: 'VERSION'

Add the Maven Central Repository into your *build.gradle*:

```groovy
repositories {
    mavenCentral()
}
```

#### SBT

Add a dependency to your project/Build.scala:

```scala
val appDependencies = Seq(
    "com.arpnetworking.metrics.extras" % "incubator-extra" % "VERSION"
)
```

The Maven Central repository is included by default.

Building
--------

Prerequisites:
* [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (Or Invoke with JDKW)

Building:

    metrics-incubator-extra> ./mvnw verify

To use the local version you must first install it locally:

    metrics-incubator-extra> ./mvnw install

You can determine the version of the local build from the pom file.  Using the local version is intended only for testing or development.

You may also need to add the local repository to your build in order to pick-up the local version:

* Maven - Included by default.
* Gradle - Add *mavenLocal()* to *build.gradle* in the *repositories* block.
* SBT - Add *resolvers += Resolver.mavenLocal* into *project/plugins.sbt*.

License
-------

Published under Apache Software License 2.0, see LICENSE

&copy; Inscope Metrics Inc., 2017
