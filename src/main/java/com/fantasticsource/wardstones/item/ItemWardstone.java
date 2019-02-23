package com.fantasticsource.wardstones.item;

import com.fantasticsource.wardstones.BlocksAndItems;
import com.fantasticsource.wardstones.Wardstones;
import net.minecraft.item.ItemBlock;

public class ItemWardstone extends ItemBlock
{
    public ItemWardstone()
    {
        super(BlocksAndItems.blockWardstone);

        setUnlocalizedName(Wardstones.MODID + ":wardstone");
        setRegistryName("wardstone");
    }
}
