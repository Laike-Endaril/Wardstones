package com.fantasticsource.wardstones.data;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

public class WardstoneData
{
    UUID id, owner;
    BlockPos pos;
    String name;
    int group;

    boolean global;
    boolean corrupted;
    ArrayList<UUID> activatedBy = new ArrayList<>();

    public WardstoneData(UUID id, BlockPos pos, String name, int group, UUID owner)
    {
        this.id = id;
        this.pos = pos;
        this.name = name;
        this.group = group;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof WardstoneData)) return false;
        return id.equals(((WardstoneData) obj).id);
    }

    @Override
    public String toString()
    {
        String str = id + "\r\n" + owner + "\r\n" + pos + "\r\n" + name + "\r\n" + group + "\r\n" + global + "\r\n" + corrupted + "\r\n";
        for (UUID id : activatedBy) str += id + "\r\n";
        return str;
    }
}
