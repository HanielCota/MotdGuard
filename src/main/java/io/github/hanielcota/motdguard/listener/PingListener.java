package io.github.hanielcota.motdguard.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PingListener {

  @NonNull private final MotdProvider motdProvider;
  @NonNull private final RateLimiter rateLimiter;

  @Subscribe
  public void onProxyPing(final ProxyPingEvent event) {
    final var originalPing = event.getPing();
    final var address = event.getConnection().getRemoteAddress();

    final var blockedPing = rateLimiter.tryBlockPing(address, originalPing);

    if (blockedPing != null) {
      event.setPing(blockedPing);
      return;
    }

    event.setPing(motdProvider.buildMotd(originalPing));
  }
}
