package org.advancedban.utils;

public class TargetOffline {

    private String name;

    private String address;

    private String lastAddress;

    public TargetOffline(String name, String address, String lastAddress) {
        this.name = name;

        this.address = address;

        this.lastAddress = lastAddress;
    }

    /**
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * @return String
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return String
     */
    public String getLastAddress() {
        return lastAddress;
    }
}
