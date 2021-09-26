package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class Report implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (sender instanceof Player player) {
            if (args.length != 0) {
                Player reportedPlayer = QProxyUtilitiesVelocity.getInstance().getServer().getAllPlayers().stream().filter(x -> x.getUsername().toLowerCase().startsWith(args[0].toLowerCase())).findFirst()
                        .orElse(player);

                if (!reportedPlayer.getUniqueId().equals(player.getUniqueId())) {
                    String name = reportedPlayer.getUsername();
                    UUID uuid = reportedPlayer.getUniqueId();
                    String reportedServer = reportedPlayer.getCurrentServer().get().toString();
                    String reportingServer = player.getCurrentServer().get().toString();

                    String formattedDate = new SimpleDateFormat(MessagesUtils.getRawString("date_format")).format(new Date(System.currentTimeMillis()));

                    String sql = String.format("INSERT INTO `reports`(`reported_uuid`,`reporter_uuid`,`reported_name`,`reporter_name`,`reported_server`,`reporter_server`, `date_added`) VALUES(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");"
                    ,                         uuid, player.getUniqueId(), name, player.getUsername(), reportedServer, reportingServer, System.currentTimeMillis());
                } else {
                    sender.sendMessage(MessagesUtils.getMessage("player_not_found"));
                }
            }
        } else {
            sender.sendMessage(MessagesUtils.getMessage("not_player"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("qproxyutilities.report");
    }
}
