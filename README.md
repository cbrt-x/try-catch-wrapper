# Download via [Jitpack](https://jitpack.io/#Jadefalke2/Try-Catch-Wrapper/v1.0.0)
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.Jadefalke256</groupId>
    <artifactId>Try-Catch-Wrapper</artifactId>
    <version>v1.0.1</version>
</dependency>
```
# Usage
### Run method
```java
Try.attempt(this::executeMethod)
	.onCatch(Throwable::printStacktrace)
	.run();
```
### Obtain value from method call
```java
var files = Try.attempt(() -> Files.list(path))
    .onCatch(IOException.class, Throwable::printStacktrace)
    .obtain();

files.ifPresent(this::doSomething);
```
### Use `finally`
```java
Try.attempt(this::startProcess)
    .onFinally(this::endProcess);
    .run();
```

