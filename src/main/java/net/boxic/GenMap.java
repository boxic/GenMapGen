package net.boxic;

import net.hollowcube.schem.Rotation;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class GenMap {

    // Variables
    public final int size;
    public final int height = 8;
    public final int seed;
    public final HashMap<Vec, Tile> tiles;
    public final LinkedList<PlacementAction> placementActions;

    // Constructor
    public GenMap(int size, int seed) {

        // Variables
        this.size = size;
        this.seed = seed;
        this.tiles = new HashMap<Vec, Tile>();
        this.placementActions = new LinkedList<PlacementAction>();

        // Load chunks
        this.loadChunks();

        // Register tiles
        for (int x = 0; x < this.size; x++) {

            for (int y = 0; y < this.height; y++) {

                for (int z = 0; z < this.size; z++) {
                    // Create tile
                    Tile tile = new Tile(new Vec(x, y, z));
                    // Register tile
                    this.tiles.put(tile.position, tile);
                }
            }
        }

        // Register placeQueue task
        MinecraftServer.getSchedulerManager().scheduleTask(this.placeQueue(), TaskSchedule.immediate(), TaskSchedule.tick(5), ExecutionType.TICK_END);
    }

    private Runnable placeQueue() {
        GenMap genMap = this;
        return new Runnable() {
            @Override
            public void run() {
                Iterator<PlacementAction> iterator = genMap.placementActions.iterator();

                while (iterator.hasNext()) {
                    PlacementAction action = iterator.next();

                    try {
                        if (genMap.canPlace(action)) {
                            // Execute action
                            // Get tile
                            Tile tile = genMap.tiles.get(action.position);
                            // Place in tile
                            tile.place(action.schematic, action.rotation);
                            // Remove action from queue
                            iterator.remove();
                        }
                        // If we can't execute yet, leave it in the queue for next time
                    } catch (Exception e) {
                        // Handle any errors during execution
                        System.err.println("Error processing action: " + e.getMessage());
                        // Optionally remove failed actions or leave them to retry
                        // iterator.remove();
                    }
                }
            }
        };
    }

    private boolean canPlace(PlacementAction action) {

        for (int x = -1; x < 2; x++) {
            // Check all in Y height
            for (int y = 0; y < this.height; y++) {
                for (int z = -1; z < 2; z++) {
                    // Get tile
                    Tile loopTile = this.tiles.get(action.position.add(x, y - action.position.y(), z));
                    // Tile exists
                    if (loopTile != null) {
                        // Tile is locked, do not access chunks
                        if (loopTile.locked) return false;
                    }
                }
            }
        }
        // No chunks locked, can place!
        return true;
    }

    public void loadChunks() {

        // List to store all our futures
        ArrayList<CompletableFuture<Chunk>> allFutures = new ArrayList<>();

        // Populate the list with futures
        for (int x = -2; x < size + 1; x++) {
            for (int z = -2; z < size + 1; z++) {
                // Load chunk
                allFutures.add(MinestomServer.instance.loadChunk(x, z));
            }
        }

        // Wait for all futures to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                allFutures.toArray(new CompletableFuture[0])
        );

        // Block until all complete
        allOf.join();
    }

    public void place(PlacementAction placementAction) {
        // Can place
        if (this.canPlace(placementAction)) {
            // Get tile
            Tile tile = this.tiles.get(placementAction.position);
            // Place in tile
            tile.place(placementAction.schematic, placementAction.rotation);
        }
        // Cannot place
        else {
            // Add to queue
            this.placementActions.add(placementAction);
        }
        // Mark as full
        this.tiles.get(placementAction.position).full = true;
    }

    public void place(Schematics schematic, Rotation rotation, Vec position) {
        this.place(new PlacementAction(schematic, rotation, position));
    }

    public class Tile {

        // Variables
        public final Vec position;
        public final Vec centerPos;
        public boolean locked;
        public boolean full;

        // Constructor
        public Tile(Vec position) {

            // Variables
            this.position = position;
            this.centerPos = new Vec(0, 4 + 8, 0).add(new Vec(-8, -9, -8)).add(this.position.mul(16));
            this.locked = false;
        }

        public void place(Schematics schematic, Rotation rotation) {
            this.locked = true;
            switch (rotation) {
                case NONE -> {
                    Vec originLoc = this.centerPos.add(new Vec(0, 0, 0));;
                    schematic.schematic.build(Rotation.NONE, true).apply(MinestomServer.instance, originLoc, () -> this.locked = false);
                }
                case CLOCKWISE_90 -> {
                    Vec originLoc = this.centerPos.add(new Vec(16, 0, 0));
                    schematic.schematic.build(Rotation.CLOCKWISE_90, true).apply(MinestomServer.instance, originLoc, () -> this.locked = false);
                }
                case CLOCKWISE_180 -> {
                    Vec originLoc = this.centerPos.add(new Vec(16, 0, 16));
                    schematic.schematic.build(Rotation.CLOCKWISE_180, true).apply(MinestomServer.instance, originLoc, () -> this.locked = false);
                }
                case CLOCKWISE_270 -> {
                    Vec originLoc = this.centerPos.add(new Vec(0, 0, 16));
                    schematic.schematic.build(Rotation.CLOCKWISE_270, true).apply(MinestomServer.instance, originLoc, () -> this.locked = false);
                }
            }
        }
    }

    public record PlacementAction(Schematics schematic, Rotation rotation, Vec position) {
    }
}