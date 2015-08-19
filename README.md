# MethodRemapper
MethodRemapper is simple Java library and Gradle plugin to rename methods in classes during the build. All needed is a mapping configuration. The mappings will be applied to the owning class of the method, as well as all classes that inherit from these classes.

# Installation
Add the Gradle plugin to your build script:

```gradle
plugins {
    id 'blue.lapis.methodremapper' version '0.1.3'
}
```

# Tasks
|Name|Description|
|----|-----------|
|`remap`|Remaps the input JAR using the supplied mapping configuration.|

# Configuration
```gradle
remap {
    // The task to remap the output JAR from (by default: `jar` task)
    inputTask = tasks.jar
    // OR: The input JAR to remap
    inputJar = project.file('myinput.jar')

    // OPTIONAL: The output for the remapped JAR (will replace inputJar by default)
    outputJar = project.file('myoutput.jar')

    // The path to the configuration file (`remap.txt` in resource folder by default)
    config = project.file('myconfig.txt')
}
```

# Mappings
The mappings configuration is a simple text file consisting out of 3 parts for a mapping, each separated by a single space on each line:

- The full qualified class name of the owning class in internal format, e.g. `java/lang/Object`
- The method name and [descriptor](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3) of the source method
- The new method name

**Example:** `java/lang/Object toString()Ljava/lang/Object; asString`  
**Full example:** https://github.com/LapisBlue/Pore/blob/master/src/main/resources/remap.txt

# Java API
The [Gradle plugin](https://github.com/LapisBlue/MethodRemapper/blob/master/gradle-plugin/src/main/groovy/blue/lapis/methodremapper/gradle/RemapTask.groovy#L51-L90) is a good example how to use the remapper in other applications.

**Javadocs:** https://jd.lapis.blue/methodremapper/

## Dependency
MethodRemapper is available as Maven dependency on [jCenter](https://bintray.com/bintray/jcenter).

### Gradle (recommended)
```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'blue.lapis.methodremapper:remapper:0.2'
}
```

### Maven
```xml
<repositories>
    <repository>
        <name>jcenter</name>
        <url>http://jcenter.bintray.com</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>blue.lapis.methodremapper</groupId>
        <artifactId>remapper</artifactId>
        <version>0.2</version>
    </dependency>
</dependencies>
```
