package me.yarinlevi.qproxyutilities.listeners;

import me.yarinlevi.qproxyutilities.QProxyUtilitiesBungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.stream.Collectors;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        StringBuffer message = new StringBuffer(event.getMessage());

        char ch = message.charAt(0);

        if (event.getSender() instanceof ProxiedPlayer player) {
            if (QProxyUtilitiesBungeeCord.getInstance().getConfig().contains("chats." + ch)) {
                String permission = QProxyUtilitiesBungeeCord.getInstance().getConfig().getString("chats." + ch + ".permission");
                if (player.hasPermission(permission)) {
                    message.deleteCharAt(0);
                    event.setCancelled(true);

                    String prefix = String.format(QProxyUtilitiesBungeeCord.getInstance().getConfig().getString("chats." + ch + ".prefix"), player.getName());

                    for (ProxiedPlayer foundPlayer : QProxyUtilitiesBungeeCord.getInstance().getProxy().getPlayers().stream().filter(x -> x.hasPermission(permission)).collect(Collectors.toList())) {
                        foundPlayer.sendMessage(new TextComponent(prefix + message.toString().trim()));
                    }
                }
            }
        }
    }
}