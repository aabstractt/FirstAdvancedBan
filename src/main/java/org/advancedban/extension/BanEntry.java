package org.advancedban.extension;

public class BanEntry extends Entry {

    public BanEntry(String name, String createdAt, String finishAt, String author, String reason) {
        super(name, createdAt, finishAt, author, reason);
    }

    @Override
    public String getTableName() {
        return "ban";
    }
}
