package com.fantasticsource.wardstones.data;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;

public class WardstoneManager
{
    private static String dir = null;

    private static boolean ready = false;
    private static ArrayList<Pair<WardstoneData, Object[]>> waiting = new ArrayList<>();

    private static ArrayList<WardstoneData> wardstones = new ArrayList<>();
    private static LinkedHashMap<UUID, ArrayList<WardstoneData>> activatedWardstones = new LinkedHashMap<>();


    public static void init(FMLServerStartingEvent event)
    {
        dir = MCTools.getDataDir(event.getServer()) + "wardstones" + File.separator;
        File file = new File(dir);
        if ((!file.exists() || !file.isDirectory()) && file.mkdir()) System.out.println("Directory created: " + dir);
        loadAll();

        ready = true;
        for (Pair<WardstoneData, Object[]> pair : waiting)
        {
            WardstoneData data = pair.getKey();
            switch ((int) pair.getValue()[0])
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
                case 7:
                    addActivators(data, (UUID[]) pair.getValue()[1]);
                    break;
                case 8:
                    removeActivators(data, (UUID[]) pair.getValue()[1]);
                    break;
                case 9:
                    setFound(data);
                    break;
                case 10:
                    setUnFound(data);
                    break;
                case 11:
                    setGroup(data, (Integer) pair.getValue()[1]);
                    break;
                case 12:
                    setOwner(data, (UUID) pair.getValue()[1]);
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
                boolean corrupted = Boolean.parseBoolean(reader.readLine().trim());
                boolean global = Boolean.parseBoolean(reader.readLine().trim());
                boolean found = Boolean.parseBoolean(reader.readLine().trim());

                ArrayList<UUID> activatedBy = new ArrayList<>();
                line = reader.readLine().trim();
                while (!line.equals(""))
                {
                    activatedBy.add(UUID.fromString(line));
                    line = reader.readLine().trim();
                }

                reader.close();

                add(new WardstoneData(id, dimension, new BlockPos(x, y, z), name, group, owner, corrupted, global, found, activatedBy));
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
            writer.write(data.corrupted + "\r\n");
            writer.write(data.global + "\r\n");
            writer.write(data.found + "\r\n");
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
            waiting.add(new Pair<>(data, new Object[]{0}));
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
            waiting.add(new Pair<>(fake, new Object[]{1}));
            return;
        }

        remove(fake);
    }

    static void clearActivators(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{2}));
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
            waiting.add(new Pair<>(data, new Object[]{3}));
            return;
        }

        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to global!\r\n" + data.toString());

        if (!data.global)
        {
            clearActivators(data);

            data.global = true;

            save(data);
        }
    }

    static void setNonGlobal(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{4}));
            return;
        }

        if (data.corrupted) throw new IllegalArgumentException("Tried to set corrupted wardstone to non-global!\r\n" + data.toString());

        if (data.global)
        {
            data.global = false;

            save(data);
        }
    }

    static void corrupt(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{5}));
            return;
        }

        if (!data.corrupted)
        {
            if (data.global) data.global = false;
            else clearActivators(data);

            data.corrupted = true;

            save(data);
        }
    }

    static void purify(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{6}));
            return;
        }

        if (data.corrupted)
        {
            data.corrupted = false;

            save(data);
        }
    }

    public static void addActivators(WardstoneData data, UUID... activators)
    {
        if (activators.length == 0) return;
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{7, activators.clone()}));
            return;
        }
        if (data.corrupted || data.global) return;

        for (UUID id : data.activatedBy)
        {
            activatedWardstones.computeIfAbsent(id, k -> new ArrayList<>()).add(data);
        }

        data.activatedBy.addAll(Arrays.asList(activators));

        save(data);
    }

    public static void removeActivators(WardstoneData data, UUID... activators)
    {
        if (activators.length == 0) return;
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{8, activators.clone()}));
            return;
        }
        if (data.corrupted || data.global) return;

        ArrayList<WardstoneData> set;
        for (UUID id : data.activatedBy)
        {
            set = activatedWardstones.get(id);
            if (set != null) set.remove(data);
        }

        data.activatedBy.removeAll(Arrays.asList(activators));

        save(data);
    }

    public static void setFound(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{9}));
            return;
        }

        data.found = true;

        save(data);
    }

    public static void setUnFound(WardstoneData data)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{10}));
            return;
        }

        data.found = false;

        save(data);
    }

    public static void setGroup(WardstoneData data, int group)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{11, group}));
            return;
        }

        data.group = group;

        save(data);
    }

    public static void setOwner(WardstoneData data, UUID owner)
    {
        if (!ready)
        {
            waiting.add(new Pair<>(data, new Object[]{12, owner}));
            return;
        }

        data.owner = owner;

        save(data);
    }


    public static WardstoneData get(World world, BlockPos pos)
    {
        int index = wardstones.indexOf(new WardstoneData(world.provider.getDimension(), pos));
        return index < 0 ? null : wardstones.get(index);
    }
}
