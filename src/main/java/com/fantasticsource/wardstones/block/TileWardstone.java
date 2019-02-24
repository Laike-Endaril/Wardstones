package com.fantasticsource.wardstones.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileWardstone extends TileEntity implements ITickable
{
    //TODO This should mostly be used for rendering purposes!  Not for (most) data!

    public TileWardstone()
    {
    }

    @Override
    public void update()
    {
        //TODO use or remove ITickable implementation
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();

        //TODO save data
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        //TODO load data
    }
}
