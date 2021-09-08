import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor @ToString @NoArgsConstructor
public class Player {

    private String name;
    private UUID uniqueId;
    private boolean operator;
    private List<String> permissions;
    private long playTime;
    private double health;
}
