package io.github.hanielcota.motdguard.ratelimit;

import java.net.InetSocketAddress;

public interface RateLimitChecker {

    boolean isAllowed(InetSocketAddress address);

    void refresh();
}