
# SimpleJson (Json-Library)

## Features of SimpleJson:
- There is a (beta) (de-)serializer for custom Objects
- Useful Methods
- Different JsonFormats (RAW, FORMATTED, SIMPLE)
- Extra FileFormat (SIMPLE) which breaks every rule of json format (easy to use and understand)
- Add custom (de-)serializers for your objects to deserialzie

##Features in work
- Improving speed of (de-)serializer 

##Disclaimer:
- To understand how the JSON-File-Format works you should check out the docs (https://www.json.org/json-en.html)

# Usage:

## Creating JsonObject

```java
JsonObject jsonObject = new JsonObject();
jsonObject.addProperty("name", "Hans"); //Adds a String property
jsonObject.addProperty("age", "56"); //Adds an int property
jsonObject.addProperty(adult, true); //Adds a boolean property
jsonObject.addProperty("key", jsonEntity); //adds a json entity
```

## Create JsonArray

```java
JsonArray jsonArray = new JsonArray();
jsonArray.add("Hello World!"); //Adds a String to this array
jsonArray.add(347); //Adds an int to this array
jsonArray.add(jsonEntity); //Adds a json entity to this array

JsonEntity entity = jsonArray.get(0); //Gets the entity at index 0
jsonArray.remove(0); //Removes the object at index 0
jsonArray.size(); //The size of the array

```

## Example usage of Json

```java
Json json = JsonBuilder
        .newBuilder()
        .serializeNulls() //Allows to serialize nulled fields
        .serializeSameFieldInstance(2) //The amount of serializing if the class has its same class as field
        .checkSerializersForSubClasses() //Checks for serializers in subclasses
        .setFormat(JsonFormat.FORMATTED) //Sets the format of (de-)serialized entities
        .build(); //Builds this json

JsonEntity entity = json.toJson(UUID.randomUUID()); //Serializes the given 
                                                    // object into a JsonEntity

UUID uuid = json.fromJson(entity, UUID.class); //Deserializes the entity
                                                     //To the object you want
```

## Example (de-)serializer

```java
public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element, Field field, Json json) {
        return UUID.fromString(element.asString());
    }

    @Override
    public JsonEntity serialize(UUID obj, Json json, Field field) {
        return new JsonString(obj.toString());
    }
}
```

## Registering (de-)serializer

```java

Json json = JsonBuilder.newBuilder().build();

json.registerSerializer(UUID.class, new UUIDSerializer());

```

## Reading and saving JsonObjects

```java
JsonObject jsonObject = new JsonObject(new File("config.json"));

jsonObject.addProperty("enabled", true);
jsonObject.addProperty("lastChanged", new Date().getTime());

jsonObject.save(new File("config.json"));

```

## Working with annotations

SimpleJson provides its house-own Annotations to simplify the usage of objects
that are going to be (de-)serialized.

```java

public class Person {

    @SerializedField(name = "person_name")
    //Changes the name of the field in the json entity
    private final String name;

    @SerializedField(ignore = true) 
    //Field will be skipped in (de-)serializer
    private final int age;
    
    @SerializedField(wrapperClasses = @WrapperClass(interfaceClass = YourInterface.class, wrapperClass = YourWrapperClass.class))
    //Sets a wrapper class if the interface needs to be instantiated
    private final YourInterface yourInterface;

    public Person(String name, int age, YourInterface yourInterface) {
        this.name = name;
        this.age = age;
        this.yourInterface = yourInterface;
    }
}
```
There is also an Annotation for a whole Object to declare fields
that will be ignored depending on their class-type or the strategy to exclude
certain fields or the same-field-serialization-amount

```java
@SerializedObject(
        excludeClasses = {UUID.class, Config.class},
        serializeSameFieldInstance = 1,
        strategy = YourCustomStrategyClass.class
)
public class Config {
    
    private final UUID configId;
    
    private final Config parent;

    public Config(UUID configId, Config parent) {
        this.configId = configId;
        this.parent = parent;
    }
}
```
