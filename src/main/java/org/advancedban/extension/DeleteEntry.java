package org.advancedban.extension;

public class DeleteEntry extends Entry {

    public DeleteEntry(String name, String createdAt, String finishAt, String author, String reason) {
        super(name, createdAt, finishAt, author, reason);
    }

    @Override
    public String getTableName() {
        return "delete";
    }
}
