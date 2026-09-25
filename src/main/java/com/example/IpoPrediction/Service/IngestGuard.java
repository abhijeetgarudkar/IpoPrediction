package com.example.IpoPrediction.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IngestGuard {

    private final boolean localOrDev;
    private final Set<String> allowedHosts;

    public IngestGuard(
            @Value("${app.environment:dev}") String environment,
            @Value("${ingest.allowed-hosts:}") String allowedHosts) {
        this.localOrDev = AppEnvironment.isLocalOrDev(environment);
        this.allowedHosts = parseHosts(allowedHosts);
    }

    public URI validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Ingest URL is required");
        }

        URI uri;
        try {
            uri = URI.create(url.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ingest URL is invalid");
        }

        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        if (localOrDev) {
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw new IllegalArgumentException("Ingest URL must use http or https");
            }
        } else if (!scheme.equals("https")) {
            throw new IllegalArgumentException("Ingest URL must use https");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Ingest URL host is required");
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);
        if ("localhost".equals(normalizedHost) || normalizedHost.endsWith(".local")) {
            throw new IllegalArgumentException("Ingest URL host is not allowed");
        }

        if (!allowedHosts.isEmpty() && !allowedHosts.contains(normalizedHost)) {
            throw new IllegalArgumentException("Ingest URL host is not allowlisted");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (isBlockedAddress(address)) {
                    throw new IllegalArgumentException("Ingest URL resolves to a private or local address");
                }
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Ingest URL host could not be resolved");
        }

        return uri;
    }

    private static boolean isBlockedAddress(InetAddress address) {
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress();
    }

    private static Set<String> parseHosts(String allowedHosts) {
        if (allowedHosts == null || allowedHosts.isBlank()) {
            return Set.of();
        }

        List<String> hosts = Arrays.stream(allowedHosts.split(","))
                .map(String::trim)
                .filter(host -> !host.isEmpty())
                .map(host -> host.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        return Set.copyOf(hosts);
    }
}
