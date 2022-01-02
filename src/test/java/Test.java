import eu.simplejson.JsonEntity;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.config.JsonConfig;
import eu.simplejson.helper.json.Json;
import eu.simplejson.helper.json.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

    public static void main(String[] args) {

        Json json = new JsonBuilder()
                .format(JsonFormat.FORMATTED)
                .innerClassSerialization(2)
                .serializeNulls(true)
                .writeArraysSingleLined(true)
                .checkSerializersForSubClasses()
                .build();

        Family family = new Family(
                new Person(
                        "Miller",
                        "Valerie",
                        Gender.FEMALE,
                        36,
                        "08.08",
                        new ArrayList<>(),
                        new ConcurrentHashMap<>()
                ),
                new Person(
                        "Hustler",
                        "Mirco",
                        Gender.MALE,
                        35,
                        "30.07",
                        new ArrayList<>(),
                        new ConcurrentHashMap<>()
                ),
                Arrays.asList(
                        new Person(
                                "Miller",
                                "Julian",
                                Gender.MALE,
                                17,
                                "03.10",
                                Arrays.asList(
                                        new FriendShip(System.currentTimeMillis(), new Person("Smith", "Mick", Gender.MALE, 17, "24.10", new ArrayList<>(), new ConcurrentHashMap<>()), RelationState.AMAZING)
                                ),
                                new ConcurrentHashMap<>()
                        )
                ),
                "Mixed"
        );

        long start = System.currentTimeMillis();
        JsonEntity entity = json.toJson(family);
        System.out.println(entity);
        System.out.println("Serializing took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        Family deserializedFamily = json.fromJson(entity, Family.class);
        System.out.println(deserializedFamily.toString());
        System.out.println("Deserializing took " + (System.currentTimeMillis() - start) + "ms");
    }


    @Getter @AllArgsConstructor @ToString
    public static class Family {

        private final Person mother;
        private final Person father;

        private final List<Person> children;

        private String name;
    }


    @Getter @AllArgsConstructor @ToString
    public static class Person {

        private String lastName;
        private String name;
        private Gender gender;

        private int age;
        private String birthday;

        private List<FriendShip> friendShips;

        private final Map<String, Object> extras;
    }

    @Getter @AllArgsConstructor @ToString
    public static class FriendShip {

        private long date;
        private Person friend;
        private RelationState state;
    }

    public static enum RelationState {

        BAD, OK, WELL, GOOD, AMAZING

    }

    public static enum Gender {

        MALE, FEMALE, OTHER
    }
}
