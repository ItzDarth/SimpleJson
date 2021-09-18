package master;

import lombok.Data;
import master.messages.Messages;
import master.properties.Properties;

import java.io.IOException;

@Data
public class MasterConfig{

    /**
     * The config properties
     */
    private Properties properties;

    /**
     * The messages
     */
    private Messages messages;

    public MasterConfig() {
        this.messages = new Messages();
        this.properties = new Properties();
    }


}
