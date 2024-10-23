package net.boxic;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import net.hollowcube.schem.Rotation;
import net.minestom.server.coordinate.Vec;

public class GenMapGen {

    public static void generate(GenMap genMap) {
        JNoise verySmallNoise = JNoise.newBuilder().perlin(genMap.seed, Interpolation.LINEAR, FadeFunction.QUINTIC_POLY)
                .scale(1 / 16.01)
                .addModifier(v -> (v + 1) / 2.0)
                .clamp(0.0, 1.0)
                .build();
        JNoise smallNoise = JNoise.newBuilder().perlin(genMap.seed + 1, Interpolation.LINEAR, FadeFunction.QUINTIC_POLY)
                .scale(1 / 16.01)
                .addModifier(v -> (v + 1) / 2.0)
                .clamp(0.0, 1.0)
                .build();
        JNoise mediumNoise = JNoise.newBuilder().perlin(genMap.seed + 2, Interpolation.LINEAR, FadeFunction.QUINTIC_POLY)
                .scale(1 / 16.01)
                .addModifier(v -> (v + 1) / 2.0)
                .clamp(0.0, 1.0)
                .build();
        JNoise largeNoise = JNoise.newBuilder().perlin(genMap.seed + 3, Interpolation.LINEAR, FadeFunction.QUINTIC_POLY)
                .scale(1 / 16.01)
                .addModifier(v -> (v + 1) / 2.0)
                .clamp(0.0, 1.0)
                .build();
        JNoise veryLargeNoise = JNoise.newBuilder().perlin(genMap.seed + 4, Interpolation.LINEAR, FadeFunction.QUINTIC_POLY)
                .scale(1 / 16.01)
                .addModifier(v -> (v + 1) / 2.0)
                .clamp(0.0, 1.0)
                .build();
        for (int x = 0; x < genMap.size; x++) {
            for (int z = 0; z < genMap.size; z++) {
                double value = 0;
                value -= verySmallNoise.evaluateNoise(x * 16.0, z * 16.0) * 0.05;
                value += smallNoise    .evaluateNoise(x *  8.0, z *  8.0) * 0.50;
                value += mediumNoise   .evaluateNoise(x *  6.0, z *  6.0) * 0.50;
                value += largeNoise    .evaluateNoise(x *  4.0, z *  4.0) * 0.15;
                value -= veryLargeNoise.evaluateNoise(x *  2.0, z *  2.0) * 0.05;
                if (value > 0.55) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 0, z));
                }
                if (value > 0.62) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 1, z));
                }
                if (value > 0.695) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 2, z));
                }
                if (value > 0.76) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 3, z));
                }
                if (value > 0.83) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 4, z));
                }
                if (value > 0.9) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 5, z));
                }
                if (value > 0.95) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 6, z));
                }
                if (value > 1.00) {
                    genMap.place(Schematics.CUBE, Rotation.NONE, new Vec(x, 7, z));
                }
            }
        }
        fillSurroundedBy3(genMap);
        fillSurroundedBy4(genMap);
    }

    public static void fillSurroundedBy3(GenMap genMap) {
        for (int x = 0; x < genMap.size; x++) {
            for (int y = 0; y < genMap.height; y++) {
                for (int z = 0; z < genMap.size; z++) {
                    // Get tile
                    GenMap.Tile tile = genMap.tiles.get(new Vec(x, y, z));
                    // Tile already filled
                    if (tile.full) continue;
                    int filledSides = 0;
                    // Check neighbors
                    GenMap.Tile neighbor;
                    // +X
                    neighbor = genMap.tiles.get(new Vec(x + 1, y, z));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // +Z
                    neighbor = genMap.tiles.get(new Vec(x, y, z + 1));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // -X
                    neighbor = genMap.tiles.get(new Vec(x - 1, y, z));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // -Z
                    neighbor = genMap.tiles.get(new Vec(x, y, z - 1));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // Fill due to being surrounded
                    if (filledSides >= 3) {
                        genMap.place(Schematics.CUBE, Rotation.NONE, tile.position);
                    }
                }
            }
        }
    }

    public static void fillSurroundedBy4(GenMap genMap) {
        for (int x = 0; x < genMap.size; x++) {
            for (int y = 0; y < genMap.height; y++) {
                for (int z = 0; z < genMap.size; z++) {
                    // Get tile
                    GenMap.Tile tile = genMap.tiles.get(new Vec(x, y, z));
                    // Tile already filled
                    if (tile.full) continue;
                    int filledSides = 0;
                    // Check neighbors
                    GenMap.Tile neighbor;
                    // +X
                    neighbor = genMap.tiles.get(new Vec(x + 1, y, z));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // +Z
                    neighbor = genMap.tiles.get(new Vec(x, y, z + 1));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // -X
                    neighbor = genMap.tiles.get(new Vec(x + 1, y, z));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // -Z
                    neighbor = genMap.tiles.get(new Vec(x, y, z - 1));
                    // Tile is not set
                    if (neighbor == null) {
                        // Counts as full
                        filledSides++;
                    }
                    // Tile is set
                    else {
                        // Tile is filled
                        if (neighbor.full) filledSides++;
                    }
                    // Fill due to being surrounded
                    if (filledSides >= 4) {
                        genMap.place(Schematics.CUBE, Rotation.NONE, tile.position);
                    }
                }
            }
        }
    }
}