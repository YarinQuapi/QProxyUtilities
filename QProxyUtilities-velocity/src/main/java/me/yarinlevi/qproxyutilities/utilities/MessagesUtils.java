package me.yarinlevi.qproxyutilities.utilities;

import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qyamlconfig.Configuration;
import me.yarinlevi.qyamlconfig.YamlConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MessagesUtils {
    private static final Map<String, String> messages = new HashMap<>();
    static Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    public MessagesUtils() {
        Configuration messagesData;

        try {

            messagesData = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(QProxyUtilitiesVelocity.getInstance().getDataDirectory().toFile(), "messages.yml"));
            messagesData.getKeys().forEach(key -> messages.put(key, messagesData.getString(key)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        messages.clear();

        Configuration messagesData;

        try {
            messagesData = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(QProxyUtilitiesVelocity.getInstance().getDataDirectory().toFile(), "messages.yml"));
            messagesData.getKeys().forEach(key -> messages.put(key, messagesData.getString(key)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Component getMessage(String key, Object... args) {
        return Component.text(String.format(messages.get(key).replaceAll("&", "§"), args));
    }

    public static Component getMessageWithClickable(String key, Object... args) {
        String msg = String.format(messages.get(key).replaceAll("&", "§"), args);

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
        return messages.getOrDefault(key, key).replaceAll("&", "§");
    }
}
