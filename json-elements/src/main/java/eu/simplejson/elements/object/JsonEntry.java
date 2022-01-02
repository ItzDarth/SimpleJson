package eu.simplejson.elements.object;

import eu.simplejson.elements.JsonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter @AllArgsConstructor
public class JsonEntry {

    /**
     * The name of this entry (the key)
     */
    private final String name;

    /**
     * The value of this entry (the value)
     */
    private final JsonEntity value;

}
