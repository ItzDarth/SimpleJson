package eu.simplejson.exceptions;

public class JsonSerializerNotFoundException extends RuntimeException {

    public JsonSerializerNotFoundException(String message) {
        super(message);
    }
}
