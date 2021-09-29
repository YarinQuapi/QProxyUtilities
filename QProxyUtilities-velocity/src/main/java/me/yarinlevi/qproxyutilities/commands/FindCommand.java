package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;

public class FindCommand implements SimpleCommand {
    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(MessagesUtils.getMessage("not_enough_args"));
        } else {
            String playerName = args[0];

            QProxyUtilitiesVelocity.getInstance().getServer().getAllPlayers().stream().filter(x -> x.getUsername().equalsIgnoreCase(playerName)).findAny()
                    .ifPresentOrElse(proxiedPlayer ->
                                    sender.sendMessage(MessagesUtils.getMessageWithCommand("/server " + proxiedPlayer.getCurrentServer().orElseThrow().getServerInfo().getName(), "find_command", proxiedPlayer.getUsername(), proxiedPlayer.getCurrentServer().orElseThrow().getServerInfo().getName())),
                            () -> sender.sendMessage(MessagesUtils.getMessage("player_not_online")));
        }
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("qpunishments.command.find");
    }
}
