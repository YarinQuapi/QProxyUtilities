package me.yarinlevi.qproxyutilities;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.yarinlevi.qproxyutilities.commands.FindCommand;
import me.yarinlevi.qproxyutilities.commands.ReportCommand;
import me.yarinlevi.qproxyutilities.commands.ReportListCommand;
import me.yarinlevi.qproxyutilities.commands.ViewReportCommand;
import me.yarinlevi.qproxyutilities.listeners.PlayerChatListener;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "qproxyutilities-velocity",
        name = "QProxyUtilities",
        version = "0.1",
        authors = { "Quapi" }
)
public class QProxyUtilitiesVelocity {

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private final Path dataDirectory;

    @Getter private static QProxyUtilitiesVelocity instance;
    @Getter private Configuration config;
    @Getter private MySQLHandler mysql;

    @Inject
    public QProxyUtilitiesVelocity(ProxyServer server, Logger logger, @DataDirectory Path path) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = path;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        if (!dataDirectory.toFile().exists())
            //noinspection ResultOfMethodCallIgnored
            dataDirectory.toFile().mkdir();

        File file1 = new File(dataDirectory.toFile(), "messages.yml");
        File file2 = new File(dataDirectory.toFile(), "config.yml");

        registerFile(file1, "messages.yml");
        registerFile(file2, "config.yml");

        new MessagesUtils();

        try {
            this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(dataDirectory.toFile() + "\\config.yml"));
            if (getConfig().getBoolean("reports.enabled")) {
                this.mysql = new MySQLHandler(this.config);
            } else {
                this.getLogger().info("Reports are disabled! mysql connection process aborted!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        server.getEventManager().register(this, new PlayerChatListener());

        CommandManager manager = server.getCommandManager();

        if (getConfig().getBoolean("reports.enabled")) {
            if (this.mysql.isEnabled()) {
                manager.register("report", new ReportCommand());
                manager.register("viewreport", new ViewReportCommand());
                manager.register("reportlist", new ReportListCommand());
            } else {
                this.getLogger().error("No connection to MySQL database! reports system disabled! check your configuration.");
            }
        }

        manager.register("find", new FindCommand(), "locate", "locateplayer");
    }

    private void registerFile(File file, String streamFileName) {
        if (!file.exists()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(streamFileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
