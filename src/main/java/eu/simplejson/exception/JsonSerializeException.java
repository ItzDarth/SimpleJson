
package eu.simplejson.exception;


import lombok.Getter;

@Getter
public class JsonSerializeException extends RuntimeException {

    public JsonSerializeException(String message) {
        super(message);
    }
}
