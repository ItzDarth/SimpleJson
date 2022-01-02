
# SimpleJson (Json-Library)

## Features of SimpleJson:
- There is a (beta) (de-)serializer for custom Objects
- Useful Methods
- Own Config-API
- Annotations and Strategies for (de-)serialization
- Different JsonFormats (RAW, FORMATTED, SIMPLE)
- Extra FileFormat (SIMPLE) which breaks every rule of json format (easy to use and understand)
- Add custom (de-)serializers for your objects to deserialize

##Negative points of SimpleJson
- You can't use any JsonEntity without having created a Json Instance at least once
<br> => This only requires to build one instance at the start of your programm. Nothing else to do for you.
<br> => The rest manages SimpleJson itself. It's just sad that you can't use any Element without a valid existing Instance

# Usage:

To understand how the JSON-File-Format works you should check out the docs (https://www.json.org/json-en.html)

## Creating a new Json Instance

As explained above, it's unavoidable to not create a Json-Instance.
So see down below how to create a new instance.

```java
Json yourJsonInstance = new JsonBuilder()
        .serializeNulls(true) //Allows to serialize nulled fields
        .innerClassSerialization(2) //The amount of serializing if the class has its same class as field
        .checkSerializersForSubClasses() //Checks for serializers in subclasses
        .writeArraysSingleLined() //writes primitive values in one line
        .format(JsonFormat.FORMATTED) //Sets the format of (de-)serialized entities
        .build(); //Builds this instance

//To see what to do with the instance read on!
```

#Json-Element-Basics:

## 1) JsonEntity

A JsonEntity is the root of every existing json-based Object you will find in this library.
Every Entity has its own JsonType and the possibility to parse it to a formatted Json-String using the JsonEntity#toString() Method.

Example usage:
```java
JsonEntity stringEntity = JsonEntity.valueOf("Hello World!");
stringEntity.asString(); //returns the element as string

JsonEntity numberEntity = JsonEntity.valueOf(23.0D);
numberEntity.asInt(); //returns the element as integer
numberEntity.asDouble(); //returns the element as double
numberEntity.asByte(); //returns the element as byte
...

JsonEntity booleanEntity = JsonEntity.valueOf(true);
booleanEntity.asBoolean(); //returns the element as boolean

JsonEntity objectEntity = new JsonObject(); //read on for info on object
objectEntity.asJsonObject(); //returns the element as jsonObject
        
JsonEntity jsonEntity = JsonEntity.valueOf(10);

jsonEntity.isInt(); //returns (boolean) if the entity is integer
jsonEntity.isString(); //returns (boolean) if the entity is string
jsonEntity.isBoolean(); //returns (boolean) if the entity is boolean
jsonEntity.isNull(); //returns (boolean) if the entity is null
jsonEntity.isPrimitive(); //returns (boolean) if the entity is a primitive value
...

JsonType entityType = jsonEntity.jsonType(); //the type of the entity
=> (STRING, NUMBER, OBJECT, ARRAY, BOOLEAN, NULL, UNDEFINED)
```

## 2) JsonObject

A JsonObject is a simple and effective way to store values under a specific key.
You can kind of compare a JsonObject to a HashMap.
You can get and set values under or from a specific key.
It's also possible to remove Values from the Object or get the position of certain values and more.

Example usage:
```java
JsonObject jsonObject = new JsonObject();
jsonObject.addProperty("name", "Hans"); //Adds a String property
jsonObject.addProperty("age", "56"); //Adds an int property
jsonObject.addProperty("adult", true); //Adds a boolean property
jsonObject.addProperty("key", jsonEntity); //adds a json entity
        
JsonEntity entity = jsonObject.get("age"); //raw json entity
int age = entity.asInt(); //gets entity as integer
        
//or single-lined
int age = jsonObject.get("age").asInt(); //directly took the age 
```

## 3) JsonArray

A JsonArray is a simple and effective way to store multiple JsonEntities after another.
You can kind of compare a JsonArray to a Java List.
You can add as many JsonEntities as you want and get them later on by their
position or remove values.

Example usage:
```java
JsonArray jsonArray = new JsonArray();
jsonArray.add("Hello World!"); //Adds a String to this array
jsonArray.add(347); //Adds an int to this array
jsonArray.add(jsonEntity); //Adds a json entity to this array

JsonEntity entity = jsonArray.get(0); //Gets the entity at index 0
jsonArray.remove(0); //Removes the object at index 0
jsonArray.size(); //The size of the array

```
## Using the (De-)Serializer

As already mentioned you can (de-)serialize custom objects of your choice
and register custom Serializers to make the operation even faster and save time.

```java
Json json = yourJsonInstance; //need to build instance (see explanation)

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

## Own example Serializer

This is what a serializer for the Class UUID would look like:
```java
public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element, Field field, Json json, Class<?>... arguments) {
        return UUID.fromString(
                element.asString() //getting the current object as String
        );
        //creating a new UUID instance from the read String
    }

    @Override
    public JsonEntity serialize(UUID obj, Json json, Field field) {
        return JsonEntity.valueOf(obj.toString()); //returning a new JsonEntity by parsing 
                                                    //the UUID to a normal String
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