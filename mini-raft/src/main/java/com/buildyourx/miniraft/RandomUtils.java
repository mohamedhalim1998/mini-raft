package com.buildyourx.miniraft;

import java.security.SecureRandom;

public class RandomUtils {
    private RandomUtils() {

    }
    public static final SecureRandom random = new SecureRandom();

    public static Long heartbeatInterval() {
        return random.nextLong(150) + 150;
    }

}
