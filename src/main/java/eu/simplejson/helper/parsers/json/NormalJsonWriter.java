
package eu.simplejson.helper.parsers.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.elements.object.JsonEntry;
import eu.simplejson.helper.parsers.easy.SimpleJsonParser;
import eu.simplejson.enums.JsonType;

import java.io.IOException;
import java.io.Writer;


public class NormalJsonWriter {

    public boolean format;

    public NormalJsonWriter(boolean format) {
        this.format = format;
    }

    public void nl(Writer tw, int level) throws IOException {
        if (format) {
            tw.write(System.getProperty("line.separator"));
            for (int i=0; i<level; i++) tw.write("  ");
        }
    }

    public void save(JsonEntity value, Writer tw, int level) throws IOException {
        boolean following=false;
        switch (value.getType()) {
            case OBJECT:
                JsonObject obj=value.asJsonObject();
                if (obj.size()>0) nl(tw, level);
                tw.write('{');
                for (JsonEntry pair : obj) {
                    if (following) tw.write(",");
                    nl(tw, level+1);
                    tw.write('\"');
                    tw.write(escapeString(pair.getName()));
                    tw.write("\":");
                    //save(, tw, level+1, " ", false);
                    JsonEntity v = pair.getValue();
                    if (v == null) {
                        continue;
                    }
                    JsonType vType = v.getType();
                    if (format && vType!= JsonType.ARRAY && vType!= JsonType.OBJECT) tw.write(" ");
                    if (v==null) tw.write("null");
                    else save(v, tw, level+1);
                    following=true;
                }
                if (following) nl(tw, level);
                tw.write('}');
                break;
            case ARRAY:
                JsonArray arr=value.asJsonArray();
                int n=arr.size();
                if (n>0) nl(tw, level);
                tw.write('[');
                for (int i=0; i<n; i++) {
                    if (following) tw.write(",");
                    JsonEntity v=arr.get(i);
                    JsonType vType=v.getType();
                    if (vType!= JsonType.ARRAY && vType!= JsonType.OBJECT) nl(tw, level+1);
                    save(v, tw, level+1);
                    following=true;
                }
                if (following) nl(tw, level);
                tw.write(']');
                break;
            case BOOLEAN:
                tw.write(value.isTrue()?"true":"false");
                break;
            case STRING:
                tw.write('"');
                tw.write(escapeString(value.asString()));
                tw.write('"');
                break;
            default:
                tw.write(value.toString());
                break;
        }
    }

    public static String escapeName(String name) {
        boolean needsEscape=name.length()==0;
        for(char ch : name.toCharArray()) {
            if (SimpleJsonParser.isWhiteSpace(ch) || ch=='{' || ch=='}' || ch=='[' || ch==']' || ch==',' || ch==':') {
                needsEscape=true;
                break;
            }
        }
        if (needsEscape) return "\""+ NormalJsonWriter.escapeString(name)+"\"";
        else return name;
    }

    public static String escapeString(String src) {
        if (src==null) return null;

        for (int i=0; i<src.length(); i++) {
            if (getEscapedChar(src.charAt(i))!=null) {
                StringBuilder sb=new StringBuilder();
                if (i>0) sb.append(src, 0, i);
                return doEscapeString(sb, src, i);
            }
        }
        return src;
    }

    private static String doEscapeString(StringBuilder sb, String src, int cur) {
        int start=cur;
        for (int i=cur; i<src.length(); i++) {
            String escaped=getEscapedChar(src.charAt(i));
            if (escaped!=null) {
                sb.append(src, start, i);
                sb.append(escaped);
                start=i+1;
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
