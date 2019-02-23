package com.fantasticsource.wardstones;

import com.fantasticsource.mctools.MCTools;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

@Mod(modid = Wardstones.MODID, name = Wardstones.NAME, version = Wardstones.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.001,)")
public class Wardstones
{
    public static final String MODID = "wardstones";
    public static final String NAME = "Wardstones";
    public static final String VERSION = "1.12.2.000";

    public static final String FILENAME = "wardstones.dat";
    public static File file;

    public Wardstones()
    {
//        MinecraftForge.EVENT_BUS.register(Wardstones.class);
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent event)
    {
        file = MCTools.getDataFile(event.getServer(), FILENAME);
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
