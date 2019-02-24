package com.fantasticsource.wardstones.data;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

import static com.fantasticsource.wardstones.data.WardstoneData.COMPARE_MODE.POS;

public class WardstoneData
{
    static COMPARE_MODE compareMode = POS;

    UUID id, owner;
    int dimension;
    BlockPos pos;
    String name;
    int group;

    boolean corrupted;
    boolean global;
    boolean found;
    ArrayList<UUID> activatedBy = new ArrayList<>();


    /**
     * This constructor is to be used for COMPARISONS ONLY
     * Namely to find an actual wardstone based on position
     */
    WardstoneData(int dimension, BlockPos pos)
    {
        this.dimension = dimension;
        this.pos = pos;
    }

    public WardstoneData(UUID id, int dimension, BlockPos pos, String name, int group, UUID owner)
    {
        this.id = id;
        this.dimension = dimension;
        this.pos = pos;
        this.name = name;
        this.group = group;
        this.owner = owner;
    }

    public WardstoneData(UUID id, int dimension, BlockPos pos, String name, int group, UUID owner, boolean global, boolean corrupted, boolean found, ArrayList<UUID> activatedBy)
    {
        this.id = id;
        this.dimension = dimension;
        this.pos = pos;
        this.name = name;
        this.group = group;
        this.owner = owner;
        this.corrupted = corrupted;
        this.global = global;
        this.found = found;
        this.activatedBy = activatedBy;
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
                return dimension == other.dimension && pos.equals(other.pos);
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

    public boolean isCorrupted()
    {
        return corrupted;
    }

    public boolean isGlobal()
    {
        return global;
    }

    public boolean isFound()
    {
        return found;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public boolean isActivatedFor(UUID id)
    {
        return activatedBy.contains(id);
    }

    public enum COMPARE_MODE
    {
        ID, POS, OWNER
    }
}
