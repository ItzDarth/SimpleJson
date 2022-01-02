
package eu.simplejson.exception;


import lombok.Getter;

@Getter
public class JsonDeserializeException extends RuntimeException {

    public JsonDeserializeException(String message) {
        super(message);
    }
}
