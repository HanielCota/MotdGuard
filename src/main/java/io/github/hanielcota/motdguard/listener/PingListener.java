package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;

/**
 * Event listener for proxy ping events.
 *
 * <p>This listener processes incoming server ping requests, first checking if the IP is rate
 * limited. If blocked, a hidden ping is returned. Otherwise, the dynamic MOTD is returned instead.
 */
@RequiredArgsConstructor
public final class PingListener {

  private final MotdProvider motdProvider;
  private final RateLimiter rateLimiter;

  @Subscribe
  public void onProxyPing(final ProxyPingEvent event) {
    if (!rateLimiter.isAllowed(event.getConnection().getRemoteAddress())) {
      event.setPing(rateLimiter.buildBlockedPing(event.getPing()));
      return;
    }
    event.setPing(motdProvider.buildMotd(event.getPing()));
  }
}
