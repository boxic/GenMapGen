package net.boxic;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;

public class MinestomServer {

    public static InstanceContainer instance;

    public MinestomServer() {

        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Get instance manager
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Create new instance
        instance = instanceManager.createInstanceContainer(DimensionType.OVERWORLD);
        // Make floor
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(3, 4, Block.WHITE_CONCRETE);
            for (int x = 0; x < 16; x++) {
                unit.modifier().setBlock(unit.absoluteStart().blockX() + x, 3, unit.absoluteStart().blockZ() + 8, Block.CYAN_CONCRETE);
            }
            for (int z = 0; z < 16; z++) {
                unit.modifier().setBlock(unit.absoluteStart().blockX() + 8, 3, unit.absoluteStart().blockZ() + z, Block.CYAN_CONCRETE);
            }
        });
        // Init lighting
        instance.setChunkSupplier(LightingChunk::new);
        // Instance settings
        instance.setTimeRate(0);
        instance.setTime(8000);
        instance.enableAutoChunkLoad(false);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(MinestomServer.instance);
            player.setRespawnPoint(new Pos(100, 42, 100));
            player.setGameMode(GameMode.CREATIVE);
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}