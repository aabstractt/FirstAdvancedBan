package org.advancedban.provider;

import org.advancedban.utils.TargetOffline;

import java.sql.SQLException;

public interface Provider {

    String getName();

    TargetOffline getTargetOffline(String username) throws SQLException;

    void setTargetOffline(TargetOffline targetOffline);
}
