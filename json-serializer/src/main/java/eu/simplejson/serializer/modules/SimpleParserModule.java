package eu.simplejson.serializer.modules;

import eu.simplejson.api.modules.ParserModule;
import eu.simplejson.elements.JsonEntity;
import eu.simplejson.enums.JsonFormat;
import eu.simplejson.parser.JsonParser;
import eu.simplejson.parser.WritingBuffer;
import eu.simplejson.parser.easy.SimpleJsonWriter;
import eu.simplejson.parser.json.NormalJsonWriter;

import java.io.*;

public class SimpleParserModule implements ParserModule {

    @Override
    public String toString(JsonEntity entity, JsonFormat format) {
        StringWriter writer = new StringWriter();

        try {
            WritingBuffer buffer = new WritingBuffer(writer, 128);

            if (format != JsonFormat.SIMPLE) {
                NormalJsonWriter jsonWriter = new NormalJsonWriter(format == JsonFormat.FORMATTED);
                jsonWriter.saveRecursive(entity, buffer, 0);
            } else {
                SimpleJsonWriter jsonWriter = new SimpleJsonWriter();
                jsonWriter.saveRecursive(entity, buffer, 0, "", true);
            }

            buffer.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return writer.toString();
    }

    @Override
    public JsonEntity parse(String input) {
        return new JsonParser().parse(input);
    }

    @Override
    public JsonEntity parse(File file) {
        try {
            return this.parse(new FileReader(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public JsonEntity parse(Reader reader) {
        return new JsonParser().parse(reader);
    }
}
