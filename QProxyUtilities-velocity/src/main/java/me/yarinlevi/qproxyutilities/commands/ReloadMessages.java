package me.yarinlevi.qproxyutilities.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.yarinlevi.qproxyutilities.utilities.MessagesUtils;

public class ReloadMessages implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        MessagesUtils.reload();
    }
}
