package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ViewReportCommand implements SimpleCommand {
    Pattern regex = Pattern.compile("[0-9]+");

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(MessagesUtils.getMessage("not_enough_args"));
        } else {
            if (regex.matcher(args[0]).matches()) {
                int id = Integer.parseInt(args[0]);

                ResultSet rs = QProxyUtilitiesVelocity.getInstance().getMysql().get("SELECT * FROM `reports` WHERE `id`=" + id + ";");

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

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("qproxyutilities.reports.receive");
    }
}
