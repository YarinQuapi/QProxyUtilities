package me.yarinlevi.qproxyutilities.commands;

import me.yarinlevi.qproxyutilities.QProxyUtilitiesBungeeCord;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;


public class ReportListCommand extends Command {
    Pattern regex = Pattern.compile("[0-9]+");

    public ReportListCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

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

        ResultSet rs = QProxyUtilitiesBungeeCord.getInstance().getMysql().get(sql);

        try {
            if (rs != null) {
                TextComponent component = new TextComponent();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String reported_name = rs.getString("reported_name");
                    String reporter_name = rs.getString("reporter_name");

                    component.addExtra(MessagesUtils.getMessageWithCommand("/viewreport " + id, "report_list_view", id, reported_name, reporter_name));
                    component.addExtra("\n");
                }

                sender.sendMessage(component);

            } else {
                sender.sendMessage(MessagesUtils.getMessage("no_reports_found"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
