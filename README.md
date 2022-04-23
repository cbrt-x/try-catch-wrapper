# Download via Jitpack
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
	<groupId>com.github.Jadefalke2</groupId>
	<artifactId>Try-Catch-Wrapper</artifactId>
	<version>v0.0.1-alpha</version>
</dependency>
```
# Usage
### Run method
```java
Try.attempt(() -> executeMethod())
	.onCatch(e -> e.printStacktrace())
	.run();
```
### Obtain value from method call
```java
var files = Try.attempt(() -> Files.list(path))
    .onCatch(IOException.class, e -> e.printStacktrace())
    .get();

files.ifPresent(this::doSomething);
```
### Use `finally`
```java
Try.attempt(this::startProcess)
    .onFinally(this::endProcess);
    .run();
```
