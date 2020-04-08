# model-mapper
Easy to use annotation based model mapper with advanced capabilities.

## Highlights 2.1.0
- Now supports Spring boot managed ValueMapper and MultiValueMapper beans! (check tests for examples)

## Highlights 2.0.0
- Supports field and setter method injection. (Method setter injection is the default)
- Supports lombok setters when using the method injection strategy (default).
- Create your own value and multi value mapper classes for advanced usage.
- Map and combine multiple values into one.
- Map values to existing instances.
- Map and instantiate classes. 

This library is available on mvn central.

Gradle
```groovy
implementation 'com.github.eye2web:model-mapper:2.1.0'
```

Maven
```xml
<dependency>
  <groupId>com.github.eye2web</groupId>
  <artifactId>model-mapper</artifactId>
  <version>2.0.0</version>
</dependency>
```

Fields with the same name and type will be mapped by default.
For advanced use, the following field annotations can be used.

Examples can be found in src/main/test.

```java
@MapValue(fieldName = "firstName")
private String firstName;
```

Mapping multiple fields into one. The multi value mapper is custom and implements the 'MultiValueMapper' interface. 
```java
@MapValues(fieldNames = {"firstName", "lastName"},
            multiValueMapper = ConcatMultiValueMapper.class)
private String fullName;
```

```java
@MapValue(fieldName = "firstName",
            valueMapper = FirstNameValueMapper.class,
            properties = {FieldProperties.IGNORE_NULL_VALUES})
    private String firstName;
```