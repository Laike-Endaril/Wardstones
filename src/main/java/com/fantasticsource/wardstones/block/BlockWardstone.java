package com.fantasticsource.wardstones.block;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.wardstones.BlocksAndItems;
import com.fantasticsource.wardstones.data.WardstoneData;
import com.fantasticsource.wardstones.data.WardstoneManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        try
        {
            WardstoneData data = WardstoneManager.get(worldIn, pos);
            if (data == null) throw new Exception(playerIn.getName() + " interacted with errored wardstone!\r\nDimension: " + worldIn.provider.getDimension() + "\r\nPos: " + pos + "\r\n");

            if (data.isCorrupted())
            {
                //Corrupted
                //TODO teleport player randomly
            }
            else if (data.isGlobal())
            {
                //Global
                if (!data.isFound())
                {
                    WardstoneManager.setFound(data);
                    //TODO set global owner if enabled in config
                    //TODO global waypoint
                }
                else
                {
                    if (playerIn.isSneaking())
                    {
                        if (playerIn.getGameProfile().getId().equals(data.getOwner()))
                        {
                            //TODO global edit menu
                        }
                    }
                    else
                    {
                        //TODO global teleport menu
                    }
                }
            }
            else
            {
                //Non-global
                if (!data.isFound())
                {
                    WardstoneManager.setFound(data);
                    //TODO set non-global owner if enabled in config
                }

                if (!data.isActivatedFor(playerIn.getGameProfile().getId()))
                {
                    WardstoneManager.addActivators(data, playerIn.getGameProfile().getId());
                    //TODO non-global waypoint
                }
                else
                {
                    if (playerIn.isSneaking())
                    {
                        if (playerIn.getGameProfile().getId().equals(data.getOwner()))
                        {
                            //TODO non-global edit menu
                        }
                    }
                    else
                    {
                        //TODO non-global teleport menu
                    }
                }
            }
            return true;
        }
        catch (Exception e)
        {
            MCTools.crash(e, 305, false);
        }

        return false;
    }
}
