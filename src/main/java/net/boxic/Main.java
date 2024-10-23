package net.boxic;

import net.hollowcube.schem.Rotation;
import net.minestom.server.coordinate.Vec;

public class Main {

    public static GenMap genMap;

    public static void main(String[] args) {

        // Start minestom server
        new MinestomServer();

        // Create GenMap
        genMap = new GenMap(30, 31337);

        // Generate map
        GenMapGen.generate(genMap);
    }
}