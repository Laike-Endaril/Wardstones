package com.fantasticsource.wardstones.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockWardstone extends BlockWardstoneBase
{
    public BlockWardstone()
    {
        super();
        setRegistryName("wardstone");
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileWardstone();
    }
}
