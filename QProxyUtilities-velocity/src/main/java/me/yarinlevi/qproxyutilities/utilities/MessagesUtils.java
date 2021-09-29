package me.yarinlevi.qproxyutilities.utilities;

import com.velocitypowered.api.proxy.Player;
import me.yarinlevi.qproxyutilities.Configuration;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.YamlConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessagesUtils {
    static Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private static Configuration messagesData;

    public MessagesUtils() {
        try {
            messagesData = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(QProxyUtilitiesVelocity.getInstance().getDataDirectory().toFile(), "messages.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String permission, String key, Object... args) {
        Component message = getMessageLines(key, args);

        for (Player player : QProxyUtilitiesVelocity.getInstance().getServer().getAllPlayers().stream().filter(x->x.hasPermission(permission)).collect(Collectors.toList())) {
            player.sendMessage(message);
        }
    }

    public static void reload() {
        try {
            messagesData = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(QProxyUtilitiesVelocity.getInstance().getDataDirectory().toFile(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Component getMessage(String key, Object... args) {
        return Component.text(String.format(messagesData.getString(key).replaceAll("&", "§"), args));
    }

    public static Component getMessageLines(String key, Object... args) {
        StringBuilder message = new StringBuilder();

        for (String string : messagesData.getStringList(key)) {
            message.append(string.replaceAll("&", "§"));
        }

        return Component.text(message.toString().formatted(args));
    }

    public static Component getMessageWithCommand(String command, String key, Object... args) {
        String msg = String.format(messagesData.getString(key).replaceAll("&", "§"), args);

        Component textComponent = Component.text(msg);

        textComponent = textComponent.clickEvent(ClickEvent.runCommand(command));

        return textComponent;
    }

    public static Component getMessageWithURL(String key, Object... args) {
        String msg = String.format(messagesData.getString(key).replaceAll("&", "§"), args);

        Component textComponent = Component.empty();

        for (String str : msg.split(" ")) {
            if (urlPattern.matcher(str).matches()) {
                Component uri = Component.text(str);

                uri = uri.clickEvent(ClickEvent.openUrl(str));
                textComponent = textComponent.append(uri).append(Component.text(" "));
            } else {
                Component noteArg = Component.text(str);

                textComponent = textComponent.append(noteArg).append(Component.text(" "));
            }
        }

        return textComponent;
    }

    public static String getRawString(String key) {
        return messagesData.getString(key, key).replaceAll("&", "§");
    }
}
