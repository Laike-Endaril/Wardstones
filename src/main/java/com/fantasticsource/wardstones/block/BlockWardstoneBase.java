package com.fantasticsource.wardstones.block;

import com.fantasticsource.wardstones.Wardstones;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockWardstoneBase extends Block
{
    public BlockWardstoneBase()
    {
        super(Material.ROCK);
        setBlockUnbreakable();
        setResistance(Float.MAX_VALUE);
        setUnlocalizedName(Wardstones.MODID + ":wardstone");
        setSoundType(SoundType.STONE);
        disableStats();

        if (this.getClass() == BlockWardstoneBase.class) setRegistryName("wardstonebase");
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }
}
