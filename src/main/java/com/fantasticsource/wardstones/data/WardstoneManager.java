package com.fantasticsource.wardstones.data;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class WardstoneManager
{
    private static String dir = null;

    private static boolean ready = false;
    private static ArrayList<Pair<WardstoneData, Integer>> waiting = new ArrayList<>();

    private static ArrayList<WardstoneData> wardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> corruptedWardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> globalWardstones = new ArrayList<>();
    private static ArrayList<WardstoneData> nonGlobalWardstones = new ArrayList<>();
    private static LinkedHashMap<UUID, ArrayList<WardstoneData>> activatedWardstones = new LinkedHashMap<>();


    public static void init(FMLServerStartingEvent event)
    {
        dir = MCTools.getDataDir(event.getServer()) + "wardstones" + File.separator;
        File file = new File(dir);
        if ((!file.exists() || !file.isDirectory()) && file.mkdir()) System.out.println("Directory created: " + dir);
        loadAll();

        ready = true;
        for (Pair<WardstoneData, Integer> pair : waiting)
        {
            WardstoneData data = pair.getKey();
            switch (pair.getValue())
            {
                case 0:
                    add(data);
                    break;
                case 1:
                    remove(data);
                    break;
                case 2:
                    clearActivators(data);
                    break;
                case 3:
                    setGlobal(data);
                    break;
                case 4:
                    setNonGlobal(data);
                    break;
                case 5:
                    corrupt(data);
                    break;
                case 6:
                    purify(data);
                    break;
            }
        }
        waiting.clear();
    }

    public static void reset()
    {
        dir = null;

        ready = false;
        waiting = new ArrayList<>();

        wardstones = new ArrayList<>();
        corruptedWardstones = new ArrayList<>();
        globalWardstones = new ArrayList<>();
        nonGlobalWardstones = new ArrayList<>();
        activatedWardstones = new LinkedHashMap<>();
    }


    public static void loadAll()
    {
        File[] files = new File(dir).listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                load(file);
            }
        }
    }

    static void load(File file)
    {
        try
        {
            String filename = file.getName();
            if (filename.contains(".dat") && !filename.contains(".dat."))
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                UUID id = UUID.fromString(filename.replace(".dat", "").trim());
                int dimension = Integer.parseInt(reader.readLine().trim());
                int x = Integer.parseInt(reader.readLine().trim());
                int y = Integer.parseInt(reader.readLine().trim());
                int z = Integer.parseInt(reader.readLine().trim());
                String name = reader.readLine().trim();
                int group = Integer.parseInt(reader.readLine().trim());
                String line = reader.readLine().trim();
                UUID owner = line.equals("null") ? null : UUID.fromString(line);
                boolean global = Boolean.parseBoolean(reader.readLine().trim());
                boolean corrupted = Boolean.parseBoolean(reader.readLine().trim());

                ArrayList<UUID> activatedBy = new ArrayList<>();
                line = reader.readLine().trim();
                while (!line.equals(""))
                {
                    activatedBy.add(UUID.fromString(line));
                    line = reader.readLine().trim();
                }

                reader.close();

                add(new WardstoneData(id, dimension, new BlockPos(x, y, z), name, group, owner, global, corrupted, activatedBy));
            }
        }
        catch (Exception e)
        {
            MCTools.crash(e, 304, true);
        }
    }


    static void save(WardstoneData data)
    {
        try
        {
            File file = new File(dir + data.id + ".dat");
            if (!file.exists() && !file.createNewFile()) throw new Exception("Could not create file: " + file.toString());

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(data.dimension);
            writer.write(data.pos.getX() + "\r\n");
            writer.write(data.pos.getY() + "\r\n");
            writer.write(data.pos.getZ() + "\r\n");
            writer.write(data.name + "\r\n");
            writer.write(data.group + "\r\n");
            writer.write(data.owner == null ? "null\r\n" : data.owner.toString() + "\r\n");
            writer.write(data.global + "\r\n");
            writer.write(data.corrupted + "\r\n");
            for (UUID id : data.activatedBy)
            {
                writer.write(id.toString() + "\r\n");
            }
            writer.close();
        }
        catch (Exception e)
        {
            MCTools.crash(e, 302, true);
        }
    }


    public static void add(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 0));
            return;
        }

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

    static void remove(WardstoneData fake)
    {
        try
        {
            int index = wardstones.indexOf(fake);
            if (index >= 0)
            {
                WardstoneData data = wardstones.get(index);

                wardstones.remove(index);

                if (data.corrupted) corruptedWardstones.remove(data);
                else if (data.global) globalWardstones.remove(data);
                else
                {
                    clearActivators(data);
                    nonGlobalWardstones.remove(data);
                }

                File file = new File(dir + data.id + ".dat");
                if (file.exists() && !file.delete()) throw new Exception("Could not delete file: " + file.toString());
            }
        }
        catch (Exception e)
        {
            MCTools.crash(e, 303, true);
        }
    }

    public static void remove(World world, BlockPos pos)
    {
        WardstoneData fake = new WardstoneData(world.provider.getDimension(), pos);
        if (!ready)
        {
            waiting.add(new Pair<>(fake, 1));
            return;
        }

        remove(fake);
    }

    static void clearActivators(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 2));
            return;
        }

        for (UUID id : data.activatedBy)
        {
            ArrayList<WardstoneData> mapData = activatedWardstones.get(id);
            if (mapData != null) mapData.remove(data);
        }

        data.activatedBy.clear();

        save(data);
    }

    static void setGlobal(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 3));
            return;
        }

        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to global!\r\n" + data.toString());

        if (!data.global)
        {
            clearActivators(data);
            nonGlobalWardstones.remove(data);
            globalWardstones.add(data);

            data.global = true;
        }

        save(data);
    }

    static void setNonGlobal(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 4));
            return;
        }

        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to non-global!\r\n" + data.toString());

        if (data.global)
        {
            globalWardstones.remove(data);
            nonGlobalWardstones.add(data);

            data.global = false;
        }

        save(data);
    }

    static void corrupt(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 5));
            return;
        }

        if (data.corrupted) return;

        if (data.global) globalWardstones.remove(data);
        else
        {
            clearActivators(data);
            nonGlobalWardstones.remove(data);
        }
        corruptedWardstones.add(data);

        data.corrupted = true;

        save(data);
    }

    static void purify(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, 6));
            return;
        }

        if (!data.corrupted) return;

        corruptedWardstones.remove(data);
        if (data.global) globalWardstones.add(data);
        else nonGlobalWardstones.add(data);

        data.corrupted = false;

        save(data);
    }
}
