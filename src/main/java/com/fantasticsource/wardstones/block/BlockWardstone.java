package com.fantasticsource.wardstones.block;

import com.fantasticsource.wardstones.BlocksAndItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);

        pos = pos.down();
        state = worldIn.getBlockState(pos);
        if (state.getBlock() == BlocksAndItems.blockWardstoneBase)
        {
            BlocksAndItems.blockWardstoneBase.breakBlock(worldIn, pos, state);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
