## java-plugins-common (maven project)
- contains common classes for CI Plugins in Java
- made specifically for the Continuous Integration plugins but might be useful elsewhere also

### To include in your project
- easiest method is to import the jar file in the target directory

### For Development
- Java >= 8
- Maven >= 3

#### To bundle the jar
Don't forget to increment the version in the pom.xml file
<pre>
mvn package
</pre>
the jar file will be in the target directory

