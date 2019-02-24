package com.fantasticsource.wardstones.block;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.wardstones.BlocksAndItems;
import com.fantasticsource.wardstones.data.WardstoneData;
import com.fantasticsource.wardstones.data.WardstoneManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);

        try
        {
            WardstoneManager.add(new WardstoneData(UUID.randomUUID(), worldIn, pos, "TODO", 0, null));
        }
        catch (Exception e)
        {
            MCTools.crash(e, 300, true);
        }
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

        //TODO remove data
    }
}
