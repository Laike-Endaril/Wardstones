package com.fantasticsource.wardstones.data;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;

public class WardstoneManager
{
    private static String dir;

    private static ArrayList<WardstoneData> wardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> corruptedWardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> globalWardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> nonGlobalWardstones = new ArrayList<>();
    private static LinkedHashMap<UUID, ArrayList<WardstoneData>> activatedWardstones = new LinkedHashMap<>();


    public static void init(FMLServerStartingEvent event)
    {
        dir = MCTools.getDataDir(event.getServer()) + "wardstones" + File.separator;
        load();
    }

    public static void save()
    {
    }

    public static void load()
    {
        File file = new File(dir + "corrupted.dat");
        file = new File(dir + "globals.dat");
        file = new File(dir + "nonglobals.dat");
    }


    static void add(UUID id, BlockPos pos, String name, int group, UUID owner, boolean global, boolean corrupted, UUID... activatedBy)
    {
        WardstoneData data = new WardstoneData(id, pos, name, group, owner);
        if (wardstones.contains(data)) throw new IllegalArgumentException("Wardstone already exists!\r\n" + data.toString());

        wardstones.add(data);
        if (corrupted) corrupt(data);
        else if (global) setGlobal(data);
        else
        {
            setNonGlobal(data);
            addActivators(data, activatedBy);
        }
    }

    static void remove(WardstoneData data)
    {
        wardstones.remove(data);

        if (data.corrupted) corruptedWardstones.remove(data);
        else if (data.global) globalWardstones.remove(data);
        else
        {
            clearActivators(data);
            nonGlobalWardstones.remove(data);
        }
    }

    static void clearActivators(WardstoneData data)
    {
        for (UUID id : data.activatedBy)
        {
            ArrayList<WardstoneData> mapData = activatedWardstones.get(id);
            if (mapData != null) mapData.remove(data);
        }

        data.activatedBy.clear();
    }

    static void removeActivators(WardstoneData data, UUID... ids)
    {
        if (data.corrupted || data.global) throw new IllegalArgumentException("Tried to remove activator from corrupted or global wardstone!\r\n" + data.toString());

        for (UUID id : ids)
        {
            ArrayList<WardstoneData> mapData = activatedWardstones.get(id);
            if (mapData != null) mapData.remove(data);

            data.activatedBy.remove(id);
        }
    }

    static void addActivators(WardstoneData data, UUID... ids)
    {
        if (data.corrupted || data.global) throw new IllegalArgumentException("Tried to add activators to corrupted or global wardstone!\r\n" + data.toString());

        for (UUID id : ids)
        {
            activatedWardstones.computeIfAbsent(id, k -> new ArrayList<>()).add(data);
        }

        data.activatedBy.addAll(Arrays.asList(ids));
    }

    static void setGlobal(WardstoneData data)
    {
        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to global!\r\n" + data.toString());

        if (!data.global)
        {
            clearActivators(data);
            nonGlobalWardstones.remove(data);
            globalWardstones.add(data);

            data.global = true;
        }
    }

    static void setNonGlobal(WardstoneData data)
    {
        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to non-global!\r\n" + data.toString());

        if (data.global)
        {
            globalWardstones.remove(data);
            nonGlobalWardstones.add(data);

            data.global = false;
        }
    }

    static void corrupt(WardstoneData data)
    {
        if (data.corrupted) return;

        if (data.global) globalWardstones.remove(data);
        else
        {
            clearActivators(data);
            nonGlobalWardstones.remove(data);
        }
        corruptedWardstones.add(data);

        data.corrupted = true;
    }

    static void purify(WardstoneData data)
    {
        if (!data.corrupted) return;

        corruptedWardstones.remove(data);
        if (data.global) globalWardstones.add(data);
        else nonGlobalWardstones.add(data);

        data.corrupted = false;
    }
}
