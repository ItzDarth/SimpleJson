package objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class Player implements IPlayer {

    private final String name;
    private final UUID uniqueId;
    private final long time;

    private final Runnable joinHandler;
}
