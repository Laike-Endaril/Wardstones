package com.fantasticsource.wardstones;

import com.fantasticsource.wardstones.data.WardstoneMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Wardstones.MODID, name = Wardstones.NAME, version = Wardstones.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.001,)")
public class Wardstones
{
    public static final String MODID = "wardstones";
    public static final String NAME = "Wardstones";
    public static final String VERSION = "1.12.2.000";

    public Wardstones()
    {
        MinecraftForge.EVENT_BUS.register(Wardstones.class);
        MinecraftForge.EVENT_BUS.register(BlocksAndItems.class);
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent event)
    {
        WardstoneMap.init(event);
    }
}
