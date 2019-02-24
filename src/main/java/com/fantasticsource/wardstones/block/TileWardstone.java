package com.fantasticsource.wardstones.block;

import com.fantasticsource.wardstones.data.WardstoneData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileWardstone extends TileEntity implements ITickable
{
    WardstoneData data;

    public TileWardstone()
    {
    }

    @Override
    public void update()
    {
        //TODO use or remove ITickable implementation
    }
}
