package com.fantasticsource.wardstones.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

import static com.fantasticsource.wardstones.data.WardstoneData.COMPARE_MODE.POS;

public class WardstoneData
{
    static COMPARE_MODE compareMode = POS;

    UUID id, owner;
    World world;
    BlockPos pos;
    String name;
    int group;

    boolean global;
    boolean corrupted;
    ArrayList<UUID> activatedBy = new ArrayList<>();


    /**
     * This constructor is to be used for COMPARISONS ONLY
     * Namely to find an actual wardstone based on position
     */
    WardstoneData(World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;
    }

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
            case ID:
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
        String str = "ID:     \t" + id + "\r\nOwner:  \t" + owner + "\r\nPos:    \t" + pos + "\r\nName:   \t" + name + "\r\nGroup:  \t" + group + "\r\nGlobal: \t" + global + "\r\nCorrupt:\t" + corrupted + "\r\nActivated By...\r\n\r\n";
        if (activatedBy.size() == 0) str += "(nobody)\r\n";
        for (UUID id : activatedBy) str += id + "\r\n";
        return str;
    }

    public enum COMPARE_MODE
    {
        ID, POS, OWNER
    }
}
