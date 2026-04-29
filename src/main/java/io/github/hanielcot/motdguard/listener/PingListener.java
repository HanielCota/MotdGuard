package io.github.hanielcot.motdguard.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import io.github.hanielcot.motdguard.service.MotdService;
import io.github.hanielcot.motdguard.service.RateLimitService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PingListener {

  private final MotdService motdService;
  private final RateLimitService rateLimitService;

  @Subscribe
  public void onProxyPing(final ProxyPingEvent event) {
    if (!rateLimitService.isAllowed(event.getConnection().getRemoteAddress())) {
      event.setPing(rateLimitService.buildBlockedPing(event.getPing()));
      return;
    }

    event.setPing(motdService.buildMotd(event.getPing()));
  }
}
