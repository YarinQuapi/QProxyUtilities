package me.yarinlevi.qproxyutilities.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import net.kyori.adventure.text.Component;

import java.util.stream.Collectors;

public class PlayerChatListener {
    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerChat(PlayerChatEvent event) {
        StringBuffer message = new StringBuffer(event.getMessage());

        char ch = message.charAt(0);

        if (QProxyUtilitiesVelocity.getInstance().getConfig().contains("chats." + ch)) {
            String permission = QProxyUtilitiesVelocity.getInstance().getConfig().getString("chats." + ch + ".permission");
            if (event.getPlayer().hasPermission(permission)) {
                message.deleteCharAt(0);
                event.setResult(PlayerChatEvent.ChatResult.denied());

                String prefix = String.format(QProxyUtilitiesVelocity.getInstance().getConfig().getString("chats." + ch + ".prefix"), event.getPlayer().getUsername());

                for (Player player : QProxyUtilitiesVelocity.getInstance().getServer().getAllPlayers().stream().filter(x -> x.hasPermission(permission)).collect(Collectors.toList())) {
                    player.sendMessage(Component.text(prefix + message.toString().trim()));
                }
            }
        }
    }
}
