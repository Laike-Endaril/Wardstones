package com.fantasticsource.wardstones.data;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class WardstoneData
{
    BlockPos pos;
    String name;
    int group;
    boolean global;
    UUID owner;
    ArrayList<UUID> activatedBy = new ArrayList<>();

    public WardstoneData(BlockPos pos, String name, int group, boolean global, UUID owner, UUID... activatedBy)
    {
        this.pos = pos;
        this.name = name;
        this.group = group;
        this.global = global;
        this.owner = owner;

        this.activatedBy.addAll(Arrays.asList(activatedBy));
    }
}
