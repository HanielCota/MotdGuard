package io.github.hanielcota.motdguard.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.github.hanielcota.motdguard.maintenance.MaintenanceManager;
import io.github.hanielcota.motdguard.motd.MotdProvider;
import io.github.hanielcota.motdguard.ratelimit.RateLimiter;
import java.net.InetSocketAddress;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

class PingListenerTest {

    private static final InetSocketAddress ADDRESS = new InetSocketAddress("127.0.0.1", 12345);
    private static final Component BLOCK_MESSAGE = Component.text("Blocked");

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
        when(rateLimiter.isBlocked(ADDRESS)).thenReturn(false);
        final var maintenanceManager = mock(MaintenanceManager.class);
        when(maintenanceManager.isEnabled()).thenReturn(false);
        final var motdProvider = mock(MotdProvider.class);
        when(motdProvider.buildMotd(original)).thenReturn(motd);
        final var event = eventReturning(original);

        new PingListener(motdProvider, rateLimiter, maintenanceManager).onProxyPing(event);

        verify(event).setPing(motd);
        verify(motdProvider).buildMotd(original);
        verify(motdProvider, never()).buildMaintenanceMotd(any());
        verify(motdProvider, never()).buildBlockedMotd(any(), any());
    }

    @Test
    void shouldSetBlockedPingWhenRateLimited() {
        final var original = ping("original");
        final var blocked = ping("blocked");
        final var rateLimiter = mock(RateLimiter.class);
        when(rateLimiter.isBlocked(ADDRESS)).thenReturn(true);
        when(rateLimiter.blockMessage()).thenReturn(BLOCK_MESSAGE);
        final var maintenanceManager = mock(MaintenanceManager.class);
        final var motdProvider = mock(MotdProvider.class);
        when(motdProvider.buildBlockedMotd(original, BLOCK_MESSAGE)).thenReturn(blocked);
        final var event = eventReturning(original);

        new PingListener(motdProvider, rateLimiter, maintenanceManager).onProxyPing(event);

        verify(event).setPing(blocked);
        verify(motdProvider).buildBlockedMotd(eq(original), eq(BLOCK_MESSAGE));
        verify(motdProvider, never()).buildMotd(any());
        // Rate limiting short-circuits: maintenance state must not even be consulted.
        verify(maintenanceManager, never()).isEnabled();
    }

    @Test
    void shouldSetMaintenanceMotdDuringMaintenance() {
        final var original = ping("original");
        final var maintenance = ping("maintenance");
        final var rateLimiter = mock(RateLimiter.class);
        when(rateLimiter.isBlocked(ADDRESS)).thenReturn(false);
        final var maintenanceManager = mock(MaintenanceManager.class);
        when(maintenanceManager.isEnabled()).thenReturn(true);
        final var motdProvider = mock(MotdProvider.class);
        when(motdProvider.buildMaintenanceMotd(original)).thenReturn(maintenance);
        final var event = eventReturning(original);

        new PingListener(motdProvider, rateLimiter, maintenanceManager).onProxyPing(event);

        verify(event).setPing(maintenance);
        verify(motdProvider).buildMaintenanceMotd(original);
        verify(motdProvider, never()).buildMotd(any());
        verify(motdProvider, never()).buildBlockedMotd(any(), any());
    }
}
