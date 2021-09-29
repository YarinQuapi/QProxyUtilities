package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.yarinlevi.qproxyutilities.QProxyUtilitiesVelocity;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.kyori.adventure.text.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;


public class ReportListCommand implements SimpleCommand {
    Pattern regex = Pattern.compile("[0-9]+");

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        int limit;

        if (args.length == 0) {
            limit = 5;
        } else {
            if (regex.matcher(args[0]).matches()) {
                limit = Integer.parseInt(args[0]);
            } else {
                limit = 5;
            }
        }

        String sql = "SELECT * FROM `reports` ORDER BY id DESC LIMIT " + limit + ";";

        ResultSet rs = QProxyUtilitiesVelocity.getInstance().getMysql().get(sql);

        try {
            if (rs != null) {
                Component component = Component.empty();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String reported_name = rs.getString("reported_name");
                    String reporter_name = rs.getString("reporter_name");

                    component = component.append(MessagesUtils.getMessageWithCommand("/viewreport " + id, "report_list_view", id, reported_name, reporter_name));
                    component = component.append(Component.text("\n"));
                }

                sender.sendMessage(component);

            } else {
                sender.sendMessage(MessagesUtils.getMessage("no_reports_found"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("qproxyutilities.reports.receive");
    }
}
