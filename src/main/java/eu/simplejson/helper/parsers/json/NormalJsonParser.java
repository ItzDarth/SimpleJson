
package eu.simplejson.helper.parsers.json;

import eu.simplejson.JsonEntity;
import eu.simplejson.elements.JsonArray;
import eu.simplejson.elements.object.JsonObject;
import eu.simplejson.exception.JsonParseException;
import eu.simplejson.elements.JsonLiteral;
import eu.simplejson.elements.JsonNumber;
import eu.simplejson.elements.JsonString;
import eu.simplejson.helper.JsonUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


public class NormalJsonParser {


    /**
     * The reader object
     */
    private final Reader reader;

    /**
     * The input as char[]
     */
    private final char[] buffer;

    /**
     * The offset of the buffer
     */
    private int bufferOffset;

    /**
     * The current index of the buffer
     */
    private int index;

    /**
     * The filling count
     */
    private int fill;

    /**
     * The current line
     */
    private int line;

    /**
     * The current line offset
     */
    private int lineOffset;

    /**
     * The current count
     */
    private int current;

    /**
     * The capturing string builder
     */
    private StringBuilder captureBuffer;

    /**
     * Where the capturing starts
     */
    private int captureStart;

    /**
     * Constructs this parser with a {@link String} input
     *
     * @param string the input to parse
     */
    public NormalJsonParser(String string) {
        this(new StringReader(string), Math.max(JsonUtils.MIN_BUFFER_SIZE, Math.min(JsonUtils.DEFAULT_BUFFER_SIZE, string.length())));
    }

    /**
     * Constructs this parser with a {@link Reader} instance
     *
     * @param reader the reader
     */
    public NormalJsonParser(Reader reader) {
        this(reader, JsonUtils.DEFAULT_BUFFER_SIZE);
    }

    /**
     * Constructs this parser with a {@link Reader} instance
     * and already a provided size of the buffer
     *
     * @param reader the reader
     * @param size the buffer size
     */
    public NormalJsonParser(Reader reader, int size) {
        this.reader = reader;
        this.buffer = new char[size];
        this.line = 1;
        this.captureStart = -1;
    }

    /**
     * Parses the given input into {@link JsonEntity}
     *
     * @return entity
     * @throws IOException if something goes wrong
     */
    public JsonEntity parse() throws IOException {

        //Reading and skipping spaces
        this.read();
        this.skipWhiteSpace();

        //Setting value
        JsonEntity parsedEntity = readValue();

        //Skipping spaces again
        this.skipWhiteSpace();

        //Throwing error if end of file
        if (!isEndOfText()) {
            throw error("Unexpected character");
        }
        return parsedEntity;
    }

    /**
     * Reads a {@link JsonEntity} from the current char
     *
     * @return entity or throws error
     */
    private JsonEntity readValue() throws IOException {
        switch (current) {

            case 'n':
                //Is null instance
                return readNull();

            case 't':
                //Is boolean instance (true)
                return readTrue();

            case 'f':
                //Is boolean instance (false)
                return readFalse();

            case '"':
                //Is empty String instance
                return readString();

            case '[':
                //Is array instance
                return readArray();

            case '{':
                //Is object instance
                return readObject();

            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                //Is number instance
                return readNumber();
            default:
                //Nothing found throwing error
                throw expected("value");
        }
    }

    /**
     * Reads a {@link JsonArray} from this parser
     *
     * @return array or throw error
     */
    private JsonArray readArray() throws IOException {

        //Reading and skipping spaces
        this.read();
        this.skipWhiteSpace();

        //Creating new array
        JsonArray array = new JsonArray();

        //Ending array if "]" reached
        if (this.readIf(']')) {
            return array;
        } do {

            //Adding new value by reading it
            this.skipWhiteSpace();
            array.add(this.readValue());
            this.skipWhiteSpace();

        } while (this.readIf(','));

        //Didn't end with a closing char throwing error
        if (!this.readIf(']')) {
            throw this.expected("',' or ']'");
        }
        return array;
    }

    /**
     * Reads {@link JsonObject} from this parser
     *
     * @return object or throw error
     */
    private JsonObject readObject() throws IOException {
        //Reading and skipping spaces
        this.read();
        this.skipWhiteSpace();

        //Creating new object
        JsonObject object = new JsonObject();


        //Ending array if "}" reached
        if (this.readIf('}')) {
            return object;
        } do {
            this.skipWhiteSpace();
            String name = this.readName();
            this.skipWhiteSpace();

            //Wrong formatting throwing error
            if (!this.readIf(':')) {
                throw this.expected("':'");
            }
            //Read name and now reading value
            this.skipWhiteSpace();
            object.add(name, this.readValue());
            this.skipWhiteSpace();

        } while (readIf(',')); //Reading while a new value is following


        //Didn't end with a closing char throwing error
        if (!this.readIf('}')) {
            throw this.expected("',' or '}'");
        }
        return object;
    }

    /**
     * Reads current name of current entity that is getting read
     *
     * @return name or throw error
     */
    private String readName() throws IOException {
        if (current != '"') {
            throw this.expected("name");
        }
        return readStringInternal();
    }

    /**
     * Reads a null instance
     *
     * @return entity or throw exception
     */
    private JsonEntity readNull() throws IOException {
        read();
        readRequiredChar('u');
        readRequiredChar('l');
        readRequiredChar('l');
        return JsonLiteral.NULL;
    }

    private JsonEntity readTrue() throws IOException {
        read();
        readRequiredChar('r');
        readRequiredChar('u');
        readRequiredChar('e');
        return JsonLiteral.TRUE;
    }

    private JsonEntity readFalse() throws IOException {
        read();
        readRequiredChar('a');
        readRequiredChar('l');
        readRequiredChar('s');
        readRequiredChar('e');
        return JsonLiteral.FALSE;
    }

    private void readRequiredChar(char ch) throws IOException {
        if (!readIf(ch)) {
            throw expected("'"+ch+"'");
        }
    }

    private JsonEntity readString() throws IOException {
        return new JsonString(readStringInternal());
    }

    private String readStringInternal() throws IOException {
        read();
        startCapture();
        while (current!='"') {
            if (current=='\\') {
                pauseCapture();
                readEscape();
                startCapture();
            } else if (current<0x20) {
                throw expected("valid string character");
            } else {
                read();
            }
        }
        String string=endCapture();
        read();
        return string;
    }

    private void readEscape() throws IOException {
        read();
        switch(current) {
            case '"':
            case '/':
            case '\\':
                captureBuffer.append((char)current);
                break;
            case 'b':
                captureBuffer.append('\b');
                break;
            case 'f':
                captureBuffer.append('\f');
                break;
            case 'n':
                captureBuffer.append('\n');
                break;
            case 'r':
                captureBuffer.append('\r');
                break;
            case 't':
                captureBuffer.append('\t');
                break;
            case 'u':
                char[] hexChars=new char[4];
                for(int i=0; i<4; i++) {
                    read();
                    if (!isHexDigit()) {
                        throw expected("hexadecimal digit");
                    }
                    hexChars[i]=(char)current;
                }
                captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected("valid escape sequence");
        }
        read();
    }

    private JsonEntity readNumber() throws IOException {
        startCapture();
        readIf('-');
        int firstDigit=current;
        if (!readDigit()) {
            throw expected("digit");
        }
        if (firstDigit!='0') {
            while (readDigit()) {
            }
        }
        readFraction();
        readExponent();
        return new JsonNumber(Double.parseDouble(endCapture()));
    }

    private boolean readFraction() throws IOException {
        if (!readIf('.')) {
            return false;
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readExponent() throws IOException {
        if (!readIf('e') && !readIf('E')) {
            return false;
        }
        if (!readIf('+')) {
            readIf('-');
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readIf(char ch) throws IOException {
        if (current!=ch) {
            return false;
        }
        read();
        return true;
    }

    private boolean readDigit() throws IOException {
        if (!isDigit()) {
            return false;
        }
        read();
        return true;
    }

    private void skipWhiteSpace() throws IOException {
        while (isWhiteSpace()) {
            read();
        }
    }

    private void read() throws IOException {
        if (index==fill) {
            if (captureStart!=-1) {
                captureBuffer.append(buffer, captureStart, fill-captureStart);
                captureStart=0;
            }
            bufferOffset += fill;
            fill=reader.read(buffer, 0, buffer.length);
            index=0;
            if (fill==-1) {
                current=-1;
                return;
            }
        }
        if (current=='\n') {
            line++;
            lineOffset=bufferOffset+index;
        }
        current=buffer[index++];
    }

    private void startCapture() {
        if (captureBuffer==null) {
            captureBuffer=new StringBuilder();
        }
        captureStart=index-1;
    }

    private void pauseCapture() {
        int end=current==-1 ? index : index-1;
        captureBuffer.append(buffer, captureStart, end-captureStart);
        captureStart=-1;
    }

    private String endCapture() {
        int end=current==-1 ? index : index-1;
        String captured;
        if (captureBuffer.length()>0) {
            captureBuffer.append(buffer, captureStart, end-captureStart);
            captured=captureBuffer.toString();
            captureBuffer.setLength(0);
        } else {
            captured=new String(buffer, captureStart, end-captureStart);
        }
        captureStart=-1;
        return captured;
    }

    private JsonParseException expected(String expected) {
        if (isEndOfText()) {
            return error("Unexpected end of input");
        }
        return error("Expected " +expected);
    }

    private JsonParseException error(String message) {
        int absIndex=bufferOffset+index;
        int column=absIndex-lineOffset;
        int offset=isEndOfText() ? absIndex : absIndex-1;
        return new JsonParseException(message, offset, line, column-1);
    }

    private boolean isWhiteSpace() {
        return current==' ' || current=='\t' || current=='\n' || current=='\r';
    }

    private boolean isDigit() {
        return current>='0' && current<='9';
    }

    private boolean isHexDigit() {
        return current>='0' && current<='9'
                || current>='a' && current<='f'
                || current>='A' && current<='F';
    }

    private boolean isEndOfText() {
        return current==-1;
    }

}
