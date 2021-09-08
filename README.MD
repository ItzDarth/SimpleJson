
# SimpleJson (Json-Library)

## Features of SimpleJson:
- There is a (beta) (de-)serializer for custom Objects
- Useful Methods
- Different JsonFormats (RAW, FORMATTED, SIMPLE)
- Extra FileFormat (SIMPLE) which breaks every rule of json format (easy to use and understand)
- Add custom (de-)serializers for your objects to deserialzie

##Features in work
- Annotation to Exlude values from being serialized 
- Annotation to change values for values when being serialized
- Improving speed of (de-)serializer 

##Disclaimer:
- To understand how the JSON-File-Format works you should check out the docs (https://www.json.org/json-en.html)
- I would not recommend using the (de-)serializer because at the moment it just does what it should but its not very fast to be honest.
I tried comparing it to google Gson and the result was 67ms (gson) to 150ms (simpleJson)
So I would just use SimpleJson for the simple usage of JsonObjects


# Usage:

## Creating JsonObject

```java
JsonObject jsonObject = new JsonObject();
jsonObject.addProperty("name", "Hans"); //Adds a String property
jsonObject.addProperty("age", "56"); //Adds an int property
jsonObject.addSerialized("key", yourObject); //adds an object serialized
```

## Create JsonArray

```java
JsonArray jsonArray = new JsonArray();
jsonArray.add("Hello World!"); //Adds a String to this array
jsonArray.add(347); //Adds an int to this array
jsonArray.add(yourObject); //Adds a custom object to this array

JsonEntity entity = jsonArray.get(0); //Gets the entity at index 0
jsonArray.remove(0); //Removes the object at index 0
jsonArray.size(); //The size of the array

```

## Example usage of Json

```java
Json json = new Json()
        .serializeNulls() //Allows to serialize nulled fields
        .checkSerializersForSubClasses() //Checks for serializers in subclasses
        .setFormat(JsonFormat.FORMATTED); //Sets the format of (de-)serialized entities


JsonEntity entity = json.toString(UUID.randomUUID()); //Serializes the given 
                                                    // object into a JsonEntity

YourObject uuid = json.fromJson(entity, UUID.class); //Deserializes the entity
                                                     //To the object you want
```

## Example (de-)serializer

```java
public class UUIDSerializer extends JsonSerializer<UUID> {

    @Override
    public UUID deserialize(JsonEntity element) {
        return UUID.fromString(element.asString());
    }

    @Override
    public JsonEntity serialize(UUID obj,Json json) {
        return new JsonString(obj.toString());
    }
}
```

## Registering (de-)serializer

```java

Json json = new Json().serializeNulls().checkSerializersForSubClasses().setFormat(JsonFormat.FORMATTED); 

json.registerSerializer(UUID.class, new UUIDSerializer());

```

## Reading and saving JsonObjects

```java
JsonObject jsonObject = new JsonObject(new File("config.json"));

jsonObject.addProperty("enabled", true);
jsonObject.addProperty("lastChanged", new Date().getTime());

jsonObject.save(new File("config.json"), JsonFormat.FORMATTED);

```

