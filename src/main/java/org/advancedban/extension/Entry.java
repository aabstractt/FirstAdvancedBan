package org.advancedban.extension;

public abstract class Entry {

    private final String name;

    private final String createdAt;

    private final String finishAt;

    private final String author;

    private final String reason;

    public Entry(String name, String createdAt, String finishAt, String author, String reason) {
        this.name = name;

        this.createdAt = createdAt;

        this.finishAt = finishAt;

        this.author = author;

        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getFinishAt() {
        return finishAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getReason() {
        return reason;
    }

    public abstract String getTableName();
}
