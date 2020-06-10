package com.github.mjd507.util.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by mjd on 2020/5/23 15:18
 */
@Slf4j
public class HostUtil {

    public static String getHostName() {
        InetAddress address = getInetAddress();
        if (address == null) return "";
        return address.getHostName();
    }

    public static String getHostIp() {
        InetAddress address = getInetAddress();
        if (address == null) return "";
        return address.getHostAddress();
    }

    private static InetAddress getInetAddress() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost failed, UnknownHostException: ", e);
            e.printStackTrace();
        }
        return address;
    }

}
