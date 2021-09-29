package me.yarinlevi.qproxyutilities;

import lombok.Getter;
import me.yarinlevi.qproxyutilities.commands.ReportCommand;
import me.yarinlevi.qproxyutilities.commands.ReportListCommand;
import me.yarinlevi.qproxyutilities.commands.ViewReportCommand;
import me.yarinlevi.qproxyutilities.listeners.PlayerChatListener;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class QProxyUtilitiesBungeeCord extends Plugin {

    @Getter private static QProxyUtilitiesBungeeCord instance;
    @Getter private Configuration config;
    @Getter private MySQLHandler mysql;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file1 = new File(getDataFolder(), "messages.yml");
        File file2 = new File(getDataFolder(), "config.yml");

        registerFile(file1, "messages.yml");
        registerFile(file2, "config.yml");

        new MessagesUtils();

        try {
            this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            if (this.getConfig().getBoolean("reports.enabled")) {
                this.mysql = new MySQLHandler(this.config);
            } else {
                this.getLogger().info("Reports are disabled! mysql connection process aborted!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginManager pm = getProxy().getPluginManager();

        pm.registerListener(this, new PlayerChatListener());

        if (this.config.getBoolean("reports.enabled")) {
            if (this.mysql.isEnabled()) {
                pm.registerCommand(this, new ReportCommand("report", "qproxyutilities.report"));
                pm.registerCommand(this, new ReportListCommand("reportlist", "qproxyutilities.reports.admin"));
                pm.registerCommand(this, new ViewReportCommand("viewreport", "qproxyutilities.reports.admin"));
            } else {
                this.getLogger().severe("No connection to MySQL database! reports system disabled! check your configuration.");
            }
        }
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
