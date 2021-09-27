package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class ReportCommand implements SimpleCommand {
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
                    String reportedServer = reportedPlayer.getCurrentServer().get().getServerInfo().getName();
                    String reportingServer = player.getCurrentServer().get().getServerInfo().getName();

                    String formattedDate = new SimpleDateFormat(MessagesUtils.getRawString("date_format")).format(new Date(System.currentTimeMillis()));

                    String sql = String.format("INSERT INTO `reports`(`reported_uuid`,`reporter_uuid`,`reported_name`,`reporter_name`,`reported_server`,`reporter_server`, `date_added`) VALUES(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");"
                                                ,uuid, player.getUniqueId(), name, player.getUsername(), reportedServer, reportingServer, System.currentTimeMillis());

                    QProxyUtilitiesVelocity.getInstance().getMysql().insert(sql);

                    /**
                     * &7--------- &bReport &7---------\n
                     * &7Reported Player: &c%1$s\n
                     * &7Reported UUID: &c%2$s\n
                     * &7Current Server: &c%3$s\n
                     * \n
                     * &7Reporter: &a%4$s\n
                     * &7Reporter UUID: &a%5$s\n
                     * &7Current Server: &a%6$s\n
                     * \n
                     * &7Time of report: &e%7$s\n
                     * &7Report ID: &e%8$s
                     */
                    MessagesUtils.broadcast("qproxyutilities.reports.receive", "player_reported", name, uuid, reportedServer, player.getUsername(), player.getUniqueId(), reportingServer, formattedDate);

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
