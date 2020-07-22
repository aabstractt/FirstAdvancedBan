package org.advancedban;

import cn.nukkit.plugin.PluginBase;
import org.advancedban.commands.TempbanCommand;
import org.advancedban.provider.Provider;
import org.advancedban.utils.Utils;

import java.io.File;
import java.sql.SQLException;

public class AdvancedBan extends PluginBase {

    private static AdvancedBan instance;

    private Provider provider;

    @Override
    public void onEnable() {
        instance = this;

        File file = getDataFolder();

        if(!file.exists() && file.mkdir()) getLogger().info("AdvancedBan Setup started...");

        getLogger().info("Starting AdvancedBan modules...");

        saveConfig();

        new TempbanCommand();

        try {
            provider = Utils.loadProvider();
        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        getLogger().info("Starting " + provider.getName() + " provider.");
    }

    public Provider getProvider() {
        return provider;
    }

    public static AdvancedBan getInstance() {
        return instance;
    }
}