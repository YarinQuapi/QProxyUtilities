package me.yarinlevi.qproxyutilities.commands;

import me.yarinlevi.qproxyutilities.QProxyUtilitiesBungeeCord;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class ReportCommand extends Command {
    public ReportCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer player) {
            if (args.length != 0) {
                ProxiedPlayer reportedPlayer = QProxyUtilitiesBungeeCord.getInstance().getProxy().getPlayers().stream().filter(x -> x.getName().toLowerCase().startsWith(args[0].toLowerCase())).findFirst()
                        .orElse(player);

                if (!reportedPlayer.getUniqueId().equals(player.getUniqueId())) {
                    String name = reportedPlayer.getName();
                    UUID uuid = reportedPlayer.getUniqueId();
                    String reportedServer = reportedPlayer.getServer().getInfo().getName();
                    String reportingServer = player.getServer().getInfo().getName();

                    String formattedDate = new SimpleDateFormat(MessagesUtils.getRawString("date_format")).format(new Date(System.currentTimeMillis()));

                    String sql = String.format("INSERT INTO `reports`(`reported_uuid`,`reporter_uuid`,`reported_name`,`reporter_name`,`reported_server`,`reporter_server`, `date_added`) VALUES(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");"
                                                ,uuid, player.getUniqueId(), name, player.getName(), reportedServer, reportingServer, System.currentTimeMillis());

                    QProxyUtilitiesBungeeCord.getInstance().getMysql().insert(sql);

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
                    MessagesUtils.broadcast("qproxyutilities.reports.admin", "player_reported", name, uuid, reportedServer, player.getName(), player.getUniqueId(), reportingServer, formattedDate);

                } else {
                    sender.sendMessage(MessagesUtils.getMessage("player_not_found"));
                }
            }
        } else {
            sender.sendMessage(MessagesUtils.getMessage("not_player"));
        }
    }
}
