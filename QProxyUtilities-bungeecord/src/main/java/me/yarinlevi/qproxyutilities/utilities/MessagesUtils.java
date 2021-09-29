package me.yarinlevi.qproxyutilities.utilities;
import me.yarinlevi.qproxyutilities.Configuration;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesBungeeCord;
import me.yarinlevi.qproxyutilities.YamlConfiguration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessagesUtils {
    static Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private static Configuration messagesData;

    public MessagesUtils() {
        try {
            messagesData = me.yarinlevi.qproxyutilities.YamlConfiguration.getProvider(me.yarinlevi.qproxyutilities.YamlConfiguration.class).load(new File(QProxyUtilitiesBungeeCord.getInstance().getDataFolder(), "messages.yml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String permission, String key, Object... args) {
        TextComponent message = getMessageLines(key, args);

        for (ProxiedPlayer player : QProxyUtilitiesBungeeCord.getInstance().getProxy().getPlayers().stream().filter(x->x.hasPermission(permission)).collect(Collectors.toList())) {
            player.sendMessage(message);
        }
    }

    public static void reload() {
        try {
            messagesData = me.yarinlevi.qproxyutilities.YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(QProxyUtilitiesBungeeCord.getInstance().getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextComponent getMessage(String key, Object... args) {
        return new TextComponent(String.format(messagesData.getString(key).replaceAll("&", "§"), args));
    }

    public static TextComponent getMessageLines(String key, Object... args) {
        StringBuilder message = new StringBuilder();

        for (String string : messagesData.getStringList(key)) {
            message.append(string.replaceAll("&", "§"));
        }

        return new TextComponent(message.toString().formatted(args));
    }

    public static TextComponent getMessageWithCommand(String command, String key, Object... args) {
        String msg = String.format(messagesData.getString(key).replaceAll("&", "§"), args);

        TextComponent textComponent = new TextComponent(msg);

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        return textComponent;
    }

    public static TextComponent getMessageWithClickable(String key, Object... args) {
        String msg = String.format(messagesData.getString(key).replaceAll("&", "§"), args);

        TextComponent textComponent = new TextComponent();

        for (String str : msg.split(" ")) {
            if (urlPattern.matcher(str).matches()) {
                TextComponent uri = new TextComponent(str);

                uri.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, str));
                textComponent.addExtra(uri);
                textComponent.addExtra(" ");
            } else {
                TextComponent noteArg = new TextComponent(str);

                textComponent.addExtra(noteArg);
                textComponent.addExtra(" ");
            }
        }

        return textComponent;
    }

    public static String getRawString(String key) {
        return messagesData.getString(key, key).replaceAll("&", "§");
    }
}
