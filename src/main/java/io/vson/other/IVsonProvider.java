
package io.vson.other;

import io.vson.VsonValue;

public interface IVsonProvider {

    String getName();

    String getDescription();

    VsonValue parse(String text);

    String stringify(VsonValue value);
}
