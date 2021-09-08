package eu.simplejson.elements.object;

import eu.simplejson.JsonEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


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
