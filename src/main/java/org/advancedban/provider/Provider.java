package org.advancedban.provider;

import org.advancedban.extension.BanEntry;
import org.advancedban.extension.DeleteEntry;
import org.advancedban.extension.Entry;
import org.advancedban.extension.MuteEntry;
import org.advancedban.utils.TargetOffline;

import java.sql.SQLException;

public interface Provider {

    String getName();

    TargetOffline getTargetOffline(String username) throws SQLException;

    void setTargetOffline(TargetOffline targetOffline);

    void addEntry(Entry entry);

    BanEntry getBanActiveByUsername(String name);

    MuteEntry getMuteActiveByUsername(String name);

    BanEntry[] getAllBanByUsername(String name);

    BanEntry[] getAllBanActive();

    MuteEntry[] getAllMuteByUsername(String name);

    MuteEntry[] getAllMuteActive();

    DeleteEntry[] getAllDeleteByUsername(String name);

    Entry[] getAllActiveByUsername(String name);

    Entry[] getAllActive();

    void deleteEntry(Entry entry);

    void updateEntry(Entry entry);
}