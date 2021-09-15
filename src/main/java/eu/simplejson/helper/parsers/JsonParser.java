package eu.simplejson.helper.parsers;

import eu.simplejson.JsonEntity;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.helper.parsers.easy.SimpleJsonParser;
import eu.simplejson.helper.parsers.json.NormalJsonParser;
import lombok.SneakyThrows;

import java.io.Reader;
import java.io.StringReader;

public class JsonParser {

    /**
     * The format of the parser
     */
    private final JsonFormat format;

    /**
     * Constructs an empty parser
     * using as default {@link JsonFormat#RAW}
     */
    public JsonParser() {
        this(JsonFormat.RAW);
    }

    /**
     * Constructs an {@link JsonParser} with a given {@link JsonFormat}
     *
     * @param format the format
     */
    public JsonParser(JsonFormat format) {
        this.format = format;
    }

    /**
     * Parses a {@link Reader} instance to a {@link JsonEntity}
     * based on the chosen {@link JsonFormat}
     *
     * @param reader the reader object
     * @return entity or null if exception
     */
    public JsonEntity parse(Reader reader) {
        try {
            if (format == JsonFormat.SIMPLE) {
                return new SimpleJsonParser(reader).parse();
            } else {
                return new NormalJsonParser(reader).parse();
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parses a {@link String} into a {@link JsonEntity}
     * based on the chosen {@link JsonFormat}
     *
     * @param input the string input
     * @return entity or null if exception
     */
    public JsonEntity parse(String input) {
        return parse(new StringReader(input));
    }

}
