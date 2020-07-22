package org.advancedban.provider;

import org.advancedban.extension.BanEntry;
import org.advancedban.utils.TargetOffline;

import java.util.List;

public interface Provider {

    String getName();

    TargetOffline getTargetOffline(String username);

    void setTargetOffline(TargetOffline targetOffline);

    void addBan(BanEntry entry);

    BanEntry getActiveById(Integer id);

    BanEntry getBanActiveByUsername(String username);

    BanEntry getBanActiveByUsername(String username, boolean permanent);

    BanEntry getMuteActiveByUsername(String username);

    BanEntry getMuteActiveByUsername(String username, boolean permanent);

    List<BanEntry> getAllDeleteByUsername(String username);

    List<BanEntry> getAllActiveByUsername(String username);

    void deleteEntry(BanEntry entry);

    void updateEntry(BanEntry entry);
}