package org.advancedban.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import org.advancedban.AdvancedBan;
import org.advancedban.provider.MysqlProvider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Utils {

    @SuppressWarnings("unchecked")
    public static MysqlProvider loadProvider() throws SQLException {
        String typeProvider = AdvancedBan.getInstance().getConfig().getString("provider").toLowerCase();

        if(!typeProvider.equals("mysql")) {
            AdvancedBan.getInstance().getLogger().error("Invalid provider");

            AdvancedBan.getInstance().setEnabled(false);

            return null;
        }

        return new MysqlProvider((HashMap<String, Object>) AdvancedBan.getInstance().getConfig().get("mysql"));
    }

    public static String calculateRemain(Long finishAt) {
        return calculateRemain(finishAt, System.currentTimeMillis());
    }

    public static String calculateRemain(Long finishAt, Long actualAt) {
        int finishAtSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(finishAt);

        int actualAtSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(actualAt);

        int diff = finishAtSeconds - actualAtSeconds;

        String timeRemaining;

        if(diff >= (60 * 60 * 24)) {
            timeRemaining = (diff / 86400) + " days, " + (diff % 86400) / 3600 + " hours, " + ((diff % 86400) % 3600) / 60 + " minutes";
        } else if(diff >= 3600) {
            int hours = diff / 3600;

            timeRemaining = hours + " hours, " + ((diff - (hours * 3600)) / 60) + " minutes";
        } else if(diff >= 60) {
            timeRemaining = (diff / 60) + " minutes";
        } else {
            timeRemaining = diff + " seconds";
        }

        return timeRemaining;
    }

    public static Long calculateTime(String arguments) {
        Object[] characters = arguments.replaceAll("[0-9]", "").split("");

        int currentInteger = Integer.parseInt(arguments.replaceAll("[a-z]", ""));

        TimeUnit tu = null;

        for(Object character : characters) {
            if(character instanceof String) {
                String k = character.toString().toLowerCase();

                switch(k) {
                    case "s":
                        tu = TimeUnit.SECONDS;
                        break;
                    case "m":
                        tu = TimeUnit.MINUTES;
                        break;
                    case "h":
                        tu = TimeUnit.HOURS;
                        break;
                    case "d":
                        tu = TimeUnit.DAYS;
                        break;
                }
            }
        }

        if(tu == null || currentInteger <= 0) {
            return null;
        }

        return System.currentTimeMillis() + tu.toMillis(currentInteger);
    }

    public static TargetOffline getTargetOffline(String username) {
        Player player = Server.getInstance().getPlayer(username);

        if(player != null) {
            username = player.getName();
        }

        return AdvancedBan.getInstance().getProvider().getTargetOffline(username);
    }
}