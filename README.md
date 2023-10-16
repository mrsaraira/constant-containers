# constant-containers
[![Java CI with Gradle](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml/badge.svg)](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml)

Flexible generic data structure for storing immutable constant values, which can replace enums for more complex usages.

The idea is to have enum-like containers to store constants but with a more flexible data structure and operations on the containers and the stored constants. 
For example, you might want to have one-to-many data relations between the constants, or one-many-one or you could have a simple (one-to-one) enumeration and a single powerful util to operate the containers and constants.

Please check `DemoTest.java` for examples for now until I find time to update the README

### Getting Started:

Final documentation and README need to be updated then it will be published on Maven Central. 
Right now you can clone this library or add it to your dependencies.

1. Add constants-containers library dependency to your project. The latest version is **1.0.0**.

Maven:
```xml
<repository>
    <id>sonatype-releases</id>
    <name>Sonatype releases</name>
    <url>https://s01.oss.sonatype.org/content/repositories/releases</url>
</repository>

<dependency>
    <groupId>io.github.mrsaraira</groupId>
    <artifactId>constant-containers</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle:
```
repositories {
    maven { url "https://s01.oss.sonatype.org/content/repositories/releases" }
}
dependencies {
    implementation 'io.github.mrsaraira:temporary-resources:1.1.0'
}
```

TBD ...
