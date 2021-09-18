package master.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @ToString
public class Properties  {

    /**
     * The wrapper key to verify logins
     */
    private String wrapperKey;

    /**
     * If player connections should be displayed in console
     */
    private boolean logPlayerConnections;

    /**
     * ???
     */
    private int maxSimultaneouslyStartingTemplates;

    /**
     * If the proxy should be online mode
     * (No online = cracked users)
     */
    private boolean proxyOnlineMode;

    /**
     * Also known as 'proxyProtocol'
     * if ping requests should be forwarded
     * to the host for information access
     */
    private boolean proxyPingForwarding;

    /**
     * Syncs the amount of online players (e.g. 100)
     * To all proxies even if there are only 50 Players
     * on 2 proxies both will display 100 players
     * If its disabled they will display 50 each
     */
    private boolean syncProxyOnlinePlayers;

    /**
     * The port of the cloud server
     */
    private int port;

    /**
     * The default port for proxies
     */
    private int defaultProxyStartPort;


    /**
     * The default port for servers
     */
    private int defaultServerStartPort;

    public Properties() {

        this.wrapperKey = null;

        this.maxSimultaneouslyStartingTemplates = 2;
        this.port = 8869;
        this.defaultProxyStartPort = 25565;
        this.defaultServerStartPort = 3000;

        this.logPlayerConnections = false;
        this.proxyOnlineMode = true;
        this.proxyPingForwarding = false;
        this.syncProxyOnlinePlayers = true;
    }

}
