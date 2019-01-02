# micrometer-annotations
Provides annotations for easy monitoring of method invocations using micrometer for Spring Boot.

This project is not available through the maven central. If you want to use it, install it locally using `mvn clean install`

## Getting started

Maven:

```xml
<dependency>
    <groupId>no.bank.quiz</groupId>
    <artifactId>metrics-annotations</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

## Use

This library currently provides 3 annotations, `@Counted`, `@Timed` and `@TimeHistogram`. They are all method level annotations. 

```java
@Counted(value = "registered.users")
public void registerUser(User user) {
    userService.createUser(user);
}
```

The above will create and initialize a counter with the name "registered.users" that increments whenever the method is invoked, regardless of whether the method execution was successful.

Similarly, `@Timed` and `@TimedHistogram` will measure the execution time of annotated methods, without distinguishing between succesful and failed invocations.
