package org.moonstudio.moontest.managers;

import java.util.*;

public class PlayerManager {
    private final Set<UUID> registeredOnJoinPlayers = Collections.synchronizedSet(new HashSet<>());
    public Set<UUID> getRegisteredOnJoinPlayers() {
        return registeredOnJoinPlayers;
    }
}
