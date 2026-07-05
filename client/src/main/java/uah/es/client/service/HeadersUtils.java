package uah.es.client.service;

import java.util.concurrent.ThreadLocalRandom;

public class HeadersUtils {
    public static String generateTrafficType() {
        return ThreadLocalRandom.current().nextBoolean()
                ? "v1"
                : "v2";
    }
}
