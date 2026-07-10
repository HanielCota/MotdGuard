package io.github.hanielcota.motdguard.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import java.net.InetSocketAddress;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class PingListenerTest {

    private static final InetSocketAddress ADDRESS = new InetSocketAddress("127.0.0.1", 12345);

    private static ServerPing ping(final String description) {
        return ServerPing.builder()
                .description(Component.text(description))
                .version(new ServerPing.Version(760, "1.19.2"))
                .build();
    }

    private ProxyPingEvent eventReturning(final ServerPing original) {
        final var connection = mock(InboundConnection.class);
        when(connection.getRemoteAddress()).thenReturn(ADDRESS);

        final var event = mock(ProxyPingEvent.class);
        when(event.getPing()).thenReturn(original);
        when(event.getConnection()).thenReturn(connection);
        return event;
    }

    @Test
    void shouldSetMotdWhenPingIsAllowed() {
        final var original = ping("original");
        final var motd = ping("motd");
        final var rateLimiter = mock(RateLimiter.class);
        when(rateLimiter.tryBlockPing(ADDRESS, original)).thenReturn(null);
        final var motdProvider = mock(MotdProvider.class);
        when(motdProvider.buildMotd(original)).thenReturn(motd);
        final var event = eventReturning(original);

        new PingListener(motdProvider, rateLimiter).onProxyPing(event);

        verify(event).setPing(motd);
        verify(motdProvider).buildMotd(original);
    }

    @Test
    void shouldSetBlockedPingWhenRateLimited() {
        final var original = ping("original");
        final var blocked = ping("blocked");
        final var rateLimiter = mock(RateLimiter.class);
        when(rateLimiter.tryBlockPing(ADDRESS, original)).thenReturn(blocked);
        final var motdProvider = mock(MotdProvider.class);
        final var event = eventReturning(original);

        new PingListener(motdProvider, rateLimiter).onProxyPing(event);

        verify(event).setPing(blocked);
        verify(motdProvider, never()).buildMotd(any());
    }
}
