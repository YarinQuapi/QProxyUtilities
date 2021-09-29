package me.yarinlevi.qproxyutilities.commands;

import me.yarinlevi.qproxyutilities.QProxyUtilitiesBungeeCord;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ViewReportCommand extends Command {
    Pattern regex = Pattern.compile("[0-9]+");

    public ViewReportCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(MessagesUtils.getMessage("not_enough_args"));
        } else {
            if (regex.matcher(args[0]).matches()) {
                int id = Integer.parseInt(args[0]);

                ResultSet rs = QProxyUtilitiesBungeeCord.getInstance().getMysql().get("SELECT * FROM `reports` WHERE `id`=" + id + ";");

                try {
                    if (rs != null && rs.next()) {
                        sender.sendMessage(MessagesUtils.getMessageLines("player_reported",
                                rs.getString("reported_name"), rs.getString("reported_uuid"), rs.getString("reported_server"),
                                rs.getString("reporter_name"), rs.getString("reported_uuid"), rs.getString("reporter_server"),
                                new SimpleDateFormat(MessagesUtils.getRawString("date_format")).format(rs.getLong("date_added"))));
                    } else {
                        sender.sendMessage(MessagesUtils.getMessage("report_not_found"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(MessagesUtils.getMessage("report_not_found"));
            }
        }
    }
}
