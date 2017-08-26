## java-plugins-common (gradle project)
- contains common classes for CI Plugins in Java
- made specifically for the Continuous Integration plugins but might be useful elsewhere also

### To include in your project
#### normal java project
easiest method is to import the jar file located within the "build/libs" directory
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

#### gradle project
- for a gradle project you can add it as a dependency in your build.gradle file
- the jar is publicly hosted with [JitPack.io](https://jitpack.io#crossbrowsertesting/ci-common)

Just add this to your build.gradle
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    compile group: 'com.github.crossbrowsertesting', name: 'ci-common', version:'1.0'
}
```
### For Development
- Java >= 8

#### To bundle the jar
<pre>
gradlew jar
</pre>
the jar file will be in the "build/libs" directory
#### To release the jar on GitHub
<pre>
gradlew githubRelease
</pre>
