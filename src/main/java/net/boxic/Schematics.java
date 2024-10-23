package net.boxic;

import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.SchematicReader;

import java.io.IOException;
import java.io.InputStream;

public enum Schematics {
    // Enum
    CUBE("cube"),
    CUBE_HALF("cube_half"),
    HALF_RAMP_LOWER("half_ramp_lower"),
    HALF_RAMP_UPPER("half_ramp_upper"),
    TEST("test"),
    ;

    // Variables
    public final Schematic schematic;

    // Constructor
    Schematics(String fileName) {
        Schematic schematic1 = null;
        try {
            // Grab animation Json file
            String resourcePath = "/schem/" + fileName + ".schem";
            try (InputStream inputStream = Schematics.class.getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    throw new IllegalArgumentException("File not found: " + resourcePath);
                }
                else {
                    schematic1 = new SchematicReader().read(inputStream);
                }
            }
        } catch (IOException e) {
            System.out.println("SCHEMATIC REGISTRATION | ERROR | " + fileName + " not found!");
        }
        this.schematic = schematic1;
        System.out.println(this.schematic.offset());
    }
}