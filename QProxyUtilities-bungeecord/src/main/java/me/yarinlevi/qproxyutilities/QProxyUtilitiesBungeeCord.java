package me.yarinlevi.qproxyutilities;

import lombok.Getter;
import me.yarinlevi.qproxyutilities.listeners.PlayerChatListener;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.md_5.bungee.api.plugin.Plugin;

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

        getProxy().getPluginManager().registerListener(this, new PlayerChatListener());

        this.config = new Configuration(getDataFolder() + "\\config.yml");
        this.mysql = new MySQLHandler(this.config);
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
