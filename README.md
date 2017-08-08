## java-plugins-common (maven project)
- contains common classes for CI Plugins in Java
- made specifically for the Continuous Integration plugins but might be useful elsewhere also

### To include in your project
#### normal java project
easiest method is to import the jar file located within the target directory
#### maven project
- for a maven project you can add it as a dependency in your pom.xml file
- the jar is publicly hosted with [JitPack.io](https://jitpack.io#crossbrowsertesting/ci-common)

Just add this to your pom.xml
```xml	
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.github.crossbrowsertesting</groupId>
    <artifactId>ci-common</artifactId>
    <version>0.25-SNAPSHOT</version>
  </dependency>
</dependencies>
```
The version will need to match the release tag

### For Development
- Java >= 8
- Maven >= 3

#### To bundle the jar
Don't forget to increment the version in the pom.xml file
<pre>
mvn package
</pre>
the jar file will be in the target directory

