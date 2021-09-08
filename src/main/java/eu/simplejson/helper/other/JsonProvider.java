
package eu.simplejson.helper.other;

import eu.simplejson.JsonEntity;

public interface JsonProvider {

    /**
     * The name of this provider
     */
    String getName();

    /**
     * Formats a {@link JsonEntity} to a {@link String}
     *
     * @param value the entity
     * @return string
     */
    String toString(JsonEntity value);

    /**
     * Parses a text into a {@link JsonEntity}
     *
     * @param text the text to parse
     * @return parsed entity
     */
    JsonEntity parse(String text);

}
