
package eu.simplejson.helper.parsers.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.elements.object.JsonEntry;
import eu.simplejson.enums.JsonType;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.Writer;

@AllArgsConstructor
public class NormalJsonWriter {

    /**
     * If the wrote json should be formatted
     */
    public boolean format;

    /**
     * Writes a new line into this writer if formatted is enabled
     * Otherwise no new line will be added
     * @param tw the writer
     * @param level the level
     * @throws IOException if something goes wrong
     */
    public void newLine(Writer tw, int level) throws IOException {
        if (format) {
            tw.write(System.getProperty("line.separator"));
            for (int i = 0; i < level; i++) {
                tw.write("  ");
            }
        }
    }

    /**
     * Saves a {@link JsonEntity} to a {@link Writer} with a given level
     *
     * @param value the entity to save
     * @param tw the writer
     * @param level the current level
     * @throws IOException if something goes wrong
     */
    public void saveRecursive(JsonEntity value, Writer tw, int level) throws IOException {
        boolean following = false;

        switch (value.jsonType()) {
            case OBJECT:

                JsonObject obj = value.asJsonObject();
                if (obj.size() > 0) {
                    this.newLine(tw, level);
                }
                tw.write('{');

                for (JsonEntry jsonEntry : obj) {
                    if (following) {
                        tw.write(",");
                    }
                    this.newLine(tw, level+1);
                    tw.write('\"');
                    tw.write(escapeString(jsonEntry.getName()));
                    tw.write("\":");
                    JsonEntity v = jsonEntry.getValue();
                    if (v == null) {
                        v = JsonLiteral.NULL;
                    }
                    JsonType vType = v.jsonType();
                    if (format && vType != JsonType.ARRAY && vType != JsonType.OBJECT) {
                        tw.write(" ");
                    }
                    this.saveRecursive(v, tw, level+1);
                    following = true;
                }

                if (following) {
                    this.newLine(tw, level);
                }

                tw.write('}');
                break;
            case ARRAY:
                JsonArray arr = value.asJsonArray();
                int n = arr.size();
                if (n > 0) {
                    this.newLine(tw, level);
                }
                tw.write('[');
                for (int i = 0; i < n; i++) {
                    if (following) {
                        tw.write(",");
                    }
                    JsonEntity v = arr.get(i);
                    JsonType vType = v.jsonType();
                    if (vType!= JsonType.ARRAY && vType != JsonType.OBJECT) {
                        this.newLine(tw, level+1);
                    }
                    this.saveRecursive(v, tw, level+1);
                    following = true;
                }
                if (following) {
                    this.newLine(tw, level);
                }
                tw.write(']');
                break;
            case STRING:
                tw.write('"');
                tw.write(escapeString(value.asString()));
                tw.write('"');
                break;
            case BOOLEAN:
                tw.write(value.isTrue() ? "true" : "false");
                break;
            default:
                tw.write(value.toString());
                break;
        }
    }

    public static String escapeString(String src) {
        if (src == null) {
            return null;
        }

        for (int i = 0; i < src.length(); i++) {
            if (getEscapedChar(src.charAt(i)) != null) {
                StringBuilder sb = new StringBuilder();
                if (i > 0) {
                    sb.append(src, 0, i);
                }
                return doEscapeString(sb, src, i);
            }
        }
        return src;
    }

    private static String doEscapeString(StringBuilder sb, String src, int cur) {
        int start = cur;
        for (int i = cur; i < src.length(); i++) {
            String escaped = getEscapedChar(src.charAt(i));
            if (escaped != null) {
                sb.append(src, start, i);
                sb.append(escaped);
                start = i + 1;
            }
        }
        sb.append(src, start, src.length());
        return sb.toString();
    }

    private static String getEscapedChar(char c) {
        switch (c) {
            case '\"': return "\\\"";
            case '\t': return "\\t";
            case '\n': return "\\n";
            case '\r': return "\\r";
            case '\f': return "\\f";
            case '\b': return "\\b";
            case '\\': return "\\\\";
            default: return null;
        }
    }
}
