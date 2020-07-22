package org.advancedban.commands;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import org.advancedban.AdvancedBan;
import org.advancedban.extension.BanEntry;
import org.advancedban.utils.TargetOffline;
import org.advancedban.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TempbanCommand extends Command {

    public TempbanCommand() {
        super("tempban", "Ban a player temporary.");

        this.setAliases(new String[]{"tban"});

        Server.getInstance().getCommandMap().register(getName(), this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        TargetOffline targetOffline;

        Long time;

        if(args.length < 2) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + label + " <player> <format> <reason>");
        } else if(!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(this.getPermissionMessage());
        } else if((targetOffline = Utils.getTargetOffline(args[0])) == null) {
            sender.sendMessage(TextFormat.RED + String.format("%s not found", args[0]));
        } else if((time = Utils.calculateTime(args[1])) == null) {
            sender.sendMessage(TextFormat.RED + "Please enter a valid time format.");
        } else {
            BanEntry ban = AdvancedBan.getInstance().getProvider().getBanActiveByUsername(targetOffline.getName());

            if(ban != null) {
                ban.delete();
            }

            (new BanEntry(2, targetOffline.getName(), (new SimpleDateFormat("dd.MM.yy HH:mm")).format(new Date()), time, sender.getName(), args.length > 2 ? args[2] : "Unknown", targetOffline.getLastAddress())).submit();
        }

        return false;
    }
}