package com.fantasticsource.wardstones.data;

import com.fantasticsource.mctools.MCTools;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.UUID;

public class WardstoneMap
{
    private static File file;

    private static LinkedHashMap<UUID, WardstoneData> idToWardstoneMap = new LinkedHashMap<>();
    private static LinkedHashMap<WardstoneData, UUID> wardstoneToIDMap = new LinkedHashMap<>();


    public static void init(FMLServerStartingEvent event)
    {
        file = MCTools.getDataFile(event.getServer(), "wardstones.dat");
        if (file.exists()) load();
        else
        {
            generate();
            save();
        }
    }

    public static void generate()
    {
    }

    public static void save()
    {
    }

    public static void load()
    {
    }
}
