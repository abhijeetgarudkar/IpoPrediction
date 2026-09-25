package com.example.IpoPrediction.Service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IngestGuardTest {

    @Test
    void localDevAllowsHttp() {
        IngestGuard guard = new IngestGuard("dev", "");

        assertDoesNotThrow(() -> guard.validateUrl("http://example.com/ipo"));
        assertDoesNotThrow(() -> guard.validateUrl("https://example.com/ipo"));
    }

    @Test
    void productionRequiresHttps() {
        IngestGuard guard = new IngestGuard("prod", "");

        assertThrows(IllegalArgumentException.class, () -> guard.validateUrl("http://example.com/ipo"));
        assertDoesNotThrow(() -> guard.validateUrl("https://example.com/ipo"));
    }

    @Test
    void productionRejectsPrivateAndLocalHosts() {
        IngestGuard guard = new IngestGuard("prod", "");

        assertThrows(IllegalArgumentException.class, () -> guard.validateUrl("https://localhost/ipo"));
        assertThrows(IllegalArgumentException.class, () -> guard.validateUrl("https://127.0.0.1/ipo"));
    }

    @Test
    void allowlistRejectsOtherHosts() {
        IngestGuard guard = new IngestGuard("prod", "example.com");

        assertThrows(IllegalArgumentException.class, () -> guard.validateUrl("https://evil.com/ipo"));
        assertDoesNotThrow(() -> guard.validateUrl("https://example.com/ipo"));
    }
}
