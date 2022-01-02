package eu.simplejson.api.modules;

import eu.simplejson.elements.JsonEntity;
import eu.simplejson.enums.JsonFormat;

import java.io.File;
import java.io.Reader;

public interface ParserModule {

    String toString(JsonEntity entity, JsonFormat format);

    JsonEntity parse(String input);

    JsonEntity parse(File file);

    JsonEntity parse(Reader reader);
}
