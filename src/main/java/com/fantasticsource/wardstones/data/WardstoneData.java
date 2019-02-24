package com.fantasticsource.wardstones.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

import static com.fantasticsource.wardstones.data.WardstoneData.COMPARE_MODE.DATA;

public class WardstoneData
{
    static COMPARE_MODE compareMode = DATA;

    UUID id, owner;
    World world;
    BlockPos pos;
    String name;
    int group;

    boolean global;
    boolean corrupted;
    ArrayList<UUID> activatedBy = new ArrayList<>();


    public WardstoneData(UUID id, World world, BlockPos pos, String name, int group, UUID owner)
    {
        this.id = id;
        this.world = world;
        this.pos = pos;
        this.name = name;
        this.group = group;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof WardstoneData)) return false;

        WardstoneData other = (WardstoneData) obj;
        switch (compareMode)
        {
            case DATA:
                return id.equals(other.id);
            case POS:
                return world.equals(other.world) && pos.equals(other.pos);
            case OWNER:
                return owner.equals(other.owner);
        }

        return false;
    }

    @Override
    public String toString()
    {
        String str = id + "\r\n" + owner + "\r\n" + pos + "\r\n" + name + "\r\n" + group + "\r\n" + global + "\r\n" + corrupted + "\r\n";
        for (UUID id : activatedBy) str += id + "\r\n";
        return str;
    }

    public enum COMPARE_MODE
    {
        DATA, POS, OWNER
    }
}
