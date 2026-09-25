package com.example.IpoPrediction.Service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppEnvironmentTest {

    @Test
    void usesLocalOrDevProfiles() {
        assertTrue(AppEnvironment.isLocalOrDev("dev"));
        assertTrue(AppEnvironment.isLocalOrDev("local"));
        assertTrue(AppEnvironment.isLocalOrDev("DEV"));
        assertTrue(AppEnvironment.isLocalOrDev("local,debug"));
        assertTrue(AppEnvironment.isLocalOrDev(null));
        assertTrue(AppEnvironment.isLocalOrDev(""));
    }

    @Test
    void treatsOtherEnvironmentsAsNonLocal() {
        assertFalse(AppEnvironment.isLocalOrDev("prod"));
        assertFalse(AppEnvironment.isLocalOrDev("staging"));
        assertFalse(AppEnvironment.isLocalOrDev("production"));
    }
}
