# constant-containers
[![Java CI with Gradle](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml/badge.svg)](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml)

Flexible generic data structure for storing immutable constant values, which can replace enums for more complex usages.

The idea is to have enum-like containers to store constants but with a more flexible data structure and operations on the containers and the stored constants. 
For example, you might want to have one-to-many data relations between the constants, or you could have a simple (one-to-one) enumeration and a single powerful util to operate the containers and constants.

Please check `DemoTest.java` for examples for now until I find time to update the README
