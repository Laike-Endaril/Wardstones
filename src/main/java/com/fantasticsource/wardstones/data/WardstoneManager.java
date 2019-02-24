package com.fantasticsource.wardstones.data;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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


    public static void init(FMLServerStartingEvent event) throws Exception
    {
        dir = MCTools.getDataDir(event.getServer()) + "wardstones" + File.separator;
        File file = new File(dir);
        if ((!file.exists() || !file.isDirectory()) && file.mkdir()) System.out.println("Directory created: " + dir);
        loadAll();
    }

    public static void saveAll() throws Exception
    {
        for (WardstoneData data : wardstones) save(data);
    }

    public static void loadAll() throws Exception
    {
        BufferedReader reader;
        File[] files = new File(dir).listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                //TODO wait until there are some saved files to double check values of first
            }
        }
    }

    static void save(WardstoneData data) throws Exception
    {
        File file = new File(dir + data.id + ".dat");
        if (!file.exists())
        {
            System.out.println(file.toString());
            if (!file.createNewFile()) throw new Exception("Could not create file: " + file.toString());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(data.owner == null ? "null\r\n" : data.owner.toString() + "\r\n");
        writer.write(data.pos.getX() + "\r\n");
        writer.write(data.pos.getY() + "\r\n");
        writer.write(data.pos.getZ() + "\r\n");
        writer.write(data.name + "\r\n");
        writer.write(data.group + "\r\n");
        writer.write(data.global + "\r\n");
        writer.write(data.corrupted + "\r\n");
        for (UUID id : data.activatedBy)
        {
            writer.write(id.toString() + "\r\n");
        }
        writer.close();
    }


    public static void add(WardstoneData data) throws Exception
    {
        if (wardstones.contains(data)) throw new IllegalArgumentException("Wardstone already exists at this position!\r\n" + data.toString());

        wardstones.add(data);
        if (data.corrupted) corrupt(data);
        else if (data.global) setGlobal(data);
        else
        {
            setNonGlobal(data);

            for (UUID id : data.activatedBy)
            {
                activatedWardstones.computeIfAbsent(id, k -> new ArrayList<>()).add(data);
            }
        }

        save(data);
    }

    static void add(UUID id, World world, BlockPos pos, String name, int group, UUID owner, boolean global, boolean corrupted, UUID... activatedBy)
    {
        WardstoneData data = new WardstoneData(id, world, pos, name, group, owner);
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
