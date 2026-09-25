package com.example.IpoPrediction.Service;

final class AppEnvironment {

    private AppEnvironment() {
    }

    static boolean isLocalOrDev(String environment) {
        if (environment == null || environment.isBlank()) {
            return true;
        }

        String[] tokens = environment.toLowerCase().split("[,\\s]+");
        for (String token : tokens) {
            if ("dev".equals(token) || "local".equals(token)) {
                return true;
            }
        }
        return false;
    }
}
