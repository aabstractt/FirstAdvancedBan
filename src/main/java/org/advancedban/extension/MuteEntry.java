package org.advancedban.extension;

public class MuteEntry extends Entry {

    public MuteEntry(String name, String createdAt, String finishAt, String author, String reason) {
        super(name, createdAt, finishAt, author, reason);
    }

    @Override
    public String getTableName() {
        return "mute";
    }
}
