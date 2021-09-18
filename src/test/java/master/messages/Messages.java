package master.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;

@Getter @Setter @ToString
public class Messages  {

    /**
     * The prefix for all messages
     */
    private String prefix;

    /**
     * If the proxy is in maintenance
     */
    private String proxyMaintenanceMessage;

    /**
     * If a group is in maintenance
     */
    private String groupMaintenanceMessage;

    /**
     * If the network is full
     */
    private String networkIsFull;

    /**
     * If a server is full
     */
    private String serviceIsFull;

    /**
     * If no fallback was found
     */
    private String noFallbackServer;

    /**
     * If the player got kicked and no fallback was found
     */
    private String kickedAndNoFallbackServer;

    /**
     * If somebody is not joining via proxy
     */
    private String onlyProxyJoin;

    /**
     * When network is shut down
     */
    private String networkShutdown;

    /**
     * When you're trying to connect to a server
     * that has a higher protocol than your minecraft version
     */
    private String wrongMinecraftVersion;

    public Messages() {
        this.prefix = "§bPoloCloud §7» ";

        this.groupMaintenanceMessage = "§7This §bgroup §7is in maintenance§8...";
        this.proxyMaintenanceMessage = "§7This §bnetwork §7is in maintenance§8...";

        this.networkIsFull = "%prefix% §7The network is §cfull§8...";
        this.serviceIsFull = "%prefix% §7The service is §cfull§8...";

        this.noFallbackServer = "%prefix% §cCould not find a suitable fallback to connect you to!";
        this.kickedAndNoFallbackServer = "%prefix% §cThe server you were on went down, but no fallback server was found!";
        this.onlyProxyJoin = "%prefix% §cYou can only join on a Proxy§8.";
        this.networkShutdown = "%prefix% §cThe network shut down!";
        this.wrongMinecraftVersion = "%prefix% §7For Server §b%server% §7the version §3%required_version% §7is required §8(§7You§8: §a%your_version%§8)";
    }

}
