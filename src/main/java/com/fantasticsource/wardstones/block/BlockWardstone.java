package com.fantasticsource.wardstones.block;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.wardstones.BlocksAndItems;
import com.fantasticsource.wardstones.data.WardstoneData;
import com.fantasticsource.wardstones.data.WardstoneManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockWardstone extends BlockWardstoneBase
{
    public BlockWardstone()
    {
        super();
        setRegistryName("wardstone");
    }


    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }


    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);

        if (!worldIn.isRemote)
        {
            try
            {
                WardstoneManager.add(new WardstoneData(UUID.randomUUID(), worldIn.provider.getDimension(), pos, "TODO", 0, null));
            }
            catch (Exception e)
            {
                MCTools.crash(e, 300, true);
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);

        BlockPos down = pos.down();
        state = worldIn.getBlockState(down);
        if (state.getBlock() == BlocksAndItems.blockWardstoneBase)
        {
            BlocksAndItems.blockWardstoneBase.breakBlock(worldIn, down, state);
            worldIn.setBlockState(down, Blocks.AIR.getDefaultState());
        }

        if (!worldIn.isRemote)
        {
            try
            {
                WardstoneManager.remove(worldIn, pos);
            }
            catch (Exception e)
            {
                MCTools.crash(e, 301, false);
            }
        }
    }
}
