# Constant-Containers
[![Java CI with Gradle](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml/badge.svg)](https://github.com/mrsaraira/constant-containers/actions/workflows/gradle.yml)

Flexible generic data structure for storing immutable constant values can enhance enums for more complex usages.

The constant-containers library offers a robust mechanism for defining and working with immutable, constant values in a type-safe way. This library enhances the capabilities of enums and enum-like structures allowing for more complex relationships and operations.

### Functionality:
- Enum and Enum-like Containers: Supports enum and enum-like structures to hold constants, improving upon traditional enums by allowing more complex data structures and relationships.
- Immutable Constants: Encourages the use of immutable constant values, ensuring thread safety and consistency.
- Type-safe Operations: Provides type-safe operations on the constants and containers, reducing the risk of runtime errors.
- Flexible Relationships: Allows defining one-to-one, one-to-many, and many-to-many relationships among constants.
- Utility Methods: Offers utility methods for matching, retrieving, and operating on constants and their relationships, such as anyValue, anyRelationValue, getEnumByValue, and match.
- Support for Anonymous and Inner Classes: Enables defining constants within anonymous and inner classes, though with some limitations.

### Getting Started:

#### Add the dependency

Add constants-containers library dependency to your project. The latest version is **2.0.0**.

Maven:
```xml
<dependency>
    <groupId>io.github.mrsaraira</groupId>
    <artifactId>constant-containers</artifactId>
    <version>2.0.0</version>
</dependency>
```

Gradle:
```
dependencies {
    implementation 'io.github.mrsaraira:constant-containers:2.0.0'
}
```
### Example Using Enums

#### Step 1: Define the Containers

```java
import io.github.mrsaraira.constants.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role implements EnumRelationConstantContainer<String, String, Role> {
    ADMIN(Constants.of("Admin", "CREATE", "READ", "UPDATE", "DELETE")),
    EDITOR(Constants.of("Editor", "CREATE", "READ", "UPDATE")),
    VIEWER(Constants.of("Viewer", "READ"));

    private final RelationConstant<String, String> constant;
}
```
This enum-like structure uses the library to define a Role with related permissions. Each Role is a constant that holds a relationship with multiple permissions.

#### Step 3: Using the Containers
You can now use the defined Role container to perform various operations, such as checking permissions, finding a role by its name, or listing all permissions of a role.
```java
boolean hasDeletePermission = Constants.anyRelationValue("DELETE", Role.ADMIN);
System.out.println("Does ADMIN have DELETE permission? " + hasDeletePermission);

// Get a Role by its name
Optional<Role> optionalRole = Constants.getEnumByValue("Editor", Role.class);
optionalRole.ifPresent(role -> System.out.println("Found role: " + role.getConstant().getKey().getValue()));

// List all permissions of the VIEWER role
Constant<String>[] viewerPermissions = Role.VIEWER.getConstant().getRelations();
System.out.println("VIEWER permissions: " + Arrays.toString(viewerPermissions));
List<String> relationValues = Constants.getRelationValues(Role.VIEWER.getConstant());
System.out.println("VIEWER permissions values: " + relationValues);
```
***Output:***
```
Does ADMIN have DELETE permission? true
Found role: Editor
VIEWER permissions: [ConstantImpl(value=READ)]
VIEWER permissions: [READ]
```

This example demonstrates how constant-containers can be used to model complex relationships in a type-safe and immutable way, extending the capabilities of traditional enums in Java.

#### List Of The Operations On Enum
![image](https://github.com/mrsaraira/constant-containers/assets/29603102/6250fec8-c55d-48ba-b99c-ed84a4f8452d)
#### List Of The Utility Operations
![image](https://github.com/mrsaraira/constant-containers/assets/29603102/c5acd74a-d83f-4753-ae38-ebdb70f3641f)

Check DemoTest test class for more examples.

### Limitations:
- Exception Handling: Some operations, like attempting to retrieve an instance of an enum class via Constants.getInstance(), will throw an IllegalStateException (you should use getEnumByValue instead), indicating certain limitations in dynamic instantiation or reflection capabilities.
- No Support for Anonymous Classes in getInstance: The utility method getInstance does not support anonymous classes, which could limit its usage in certain dynamic scenarios.
- Static Requirement for Constants: There's a requirement for constants to be static when used within certain container implementations, which may not always be desirable or intuitive.
- Runtime Exceptions for Duplicate Keys: Containers with duplicated keys may lead to runtime exceptions, requiring careful structuring of constants to avoid such issues.
