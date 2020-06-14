package org.advancedban.utils;

import cn.nukkit.Player;
import org.advancedban.AdvancedBan;
import org.advancedban.provider.MysqlProvider;

import java.sql.SQLException;
import java.util.HashMap;

public class Utils {

    @SuppressWarnings("unchecked")
    public static MysqlProvider loadProvider() throws SQLException {
        String typeProvider = AdvancedBan.getInstance().getConfig().getString("provider").toLowerCase();

        if(!typeProvider.equals("mysql") && !typeProvider.equals("yaml")) {
            AdvancedBan.getInstance().getLogger().error("Invalid provider");

            AdvancedBan.getInstance().setEnabled(false);

            return null;
        }

        return new MysqlProvider((HashMap<String, Object>) AdvancedBan.getInstance().getConfig().get("mysql"));
    }

    public static TargetOffline getOrCreateTarget(Player player) throws SQLException {
        return getOrCreateTarget(player.getName(), player.getAddress());
    }

    public static TargetOffline getOrCreateTarget(String name) throws SQLException {
        return getOrCreateTarget(name, null);
    }

    public static TargetOffline getOrCreateTarget(String name, String address) throws SQLException {
        TargetOffline targetOffline = AdvancedBan.getInstance().getProvider().getTargetOffline(name);

        if(targetOffline == null) AdvancedBan.getInstance().getProvider().setTargetOffline((targetOffline = new TargetOffline(name, address, address)));

        return targetOffline;
    }
}
