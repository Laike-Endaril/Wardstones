package com.fantasticsource.wardstones.item;

import com.fantasticsource.wardstones.BlocksAndItems;
import com.fantasticsource.wardstones.Wardstones;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWardstone extends ItemBlock
{
    public ItemWardstone()
    {
        super(BlocksAndItems.blockWardstone);

        setUnlocalizedName(Wardstones.MODID + ":wardstone");
        setRegistryName("wardstone");
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(block, pos, false, facing, null))
        {
            int i = getMetadata(itemstack.getMetadata());
            IBlockState state = block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos.up(), facing, hitX, hitY, hitZ, state))
            {
                worldIn.setBlockState(pos, BlocksAndItems.blockWardstoneBase.getDefaultState());

                state = worldIn.getBlockState(pos);
                SoundType soundtype = state.getBlock().getSoundType(state, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
}
