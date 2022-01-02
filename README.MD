
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
jsonObject.addProperty("adult", true); //Adds a boolean property
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
Json json = new JsonBuilder()
        .serializeNulls(true) //Allows to serialize nulled fields
        .innerClassSerialization(2) //The amount of serializing if the class has its same class as field
        .checkSerializersForSubClasses() //Checks for serializers in subclasses
        .writeArraysSingleLined() //writes primitive values in one line
        .format(JsonFormat.FORMATTED) //Sets the format of (de-)serialized entities
        .build(); //Builds this json

JsonEntity entity = json.toJson(UUID.randomUUID()); //Serializes the given 
                                                    // object into a JsonEntity
UUID uuid = json.fromJson(entity, UUID.class); //Deserializes the entity
                                                //To the object you want

//argument example

JsonEntity entity = json.toJson(Arrays.asList("Hello", "World", "Whats", "up?"));

List<?> list = json.fromJson(entity, List.class, String.class);
                      //the json    //object-type //the list type

//-> This helps to construct lists and serialize the values inside a list the right way

```

## Example (de-)serializer

```java
public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
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

Json json = new JsonBuilder()
        .addSerializer(UUID.class, new UUIDSerializer())
        .build();

```

## Reading and saving JsonObjects

```java
File file = new File("config.json");
JsonObject jsonObject = new JsonObject(file);

jsonObject.addProperty("enabled", true);
jsonObject.addProperty("lastChanged", new Date().getTime());

jsonObject.save(file);

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

## Using the built-in Config API

SimpleJson provides its house own Config-API where you can simply
store and load values from a file into a config and save it all again
JsonConfigs consist of JsonSections which represent a JsonObject within
more JsonObjects
Unlike the JsonObject it provides way more possibilities like searching for a
JsonObject through all sub-objects or not having to provide a Json-Instance
because JsonConfigs are created through a Json-Instance, so there is no way 
that the Json-Instance is null at any time.

Example:
```java
Json json = new JsonBuilder().format(JsonFormat.FORMATTED).build();
//this can be any json instance you want

JsonConfig config = json.loadConfig(new File("config.json")); //loads the config
        
//filling in all values you want to
config.set("name", "Hans");
config.set("age", 23);
config.set("job", "no_job");

//saving the config to the provide file
config.save();
```

Retrieving a value from other objects:

```java


Json json = new JsonBuilder().format(JsonFormat.FORMATTED).build();
JsonConfig config = json.loadConfig(new File("config.json")); //loads the config

String key = (String) config.get("sec1.sec2.key");
System.out.println("Returned: " + key);
```

The example above would lead to the following:

```json
{
    "comment": "The key for this is simply comment",
    "sec1": {
        "comment": "The key for this is sec1.comment",
        "sec2": {
            "comment": "The key for this is sec1.sec2.key",
            "key": "value"
        }
    }
}
```