package mantle.blocks.abstracts;

import static mantle.lib.CoreRepo.logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author progwml6 base class for itemBlocks with different unlocalized names based on metadata
 */

public class MultiItemBlock extends ItemBlock {

    private final String[] blockType;
    @SerializedName(value = "secondUnlocalizedName")
    private String unlocalizedName;
    private final String append;
    private final int[] specialIndex = { -1, -1 };

    public MultiItemBlock(Block b, String itemBlockUnlocalizedName, String[] blockTypes) {
        super(b);
        if (itemBlockUnlocalizedName.isEmpty()) this.unlocalizedName = super.getUnlocalizedName();
        else this.unlocalizedName = itemBlockUnlocalizedName;
        this.blockType = blockTypes;
        this.append = "";
    }

    public MultiItemBlock(Block b, String itemBlockUnlocalizedName, String appendToEnd, String[] blockTypes) {
        super(b);
        this.unlocalizedName = itemBlockUnlocalizedName;
        this.blockType = blockTypes;
        this.append = "." + appendToEnd;
    }

    public void setSpecialIndex(int clampIndex, int stringBuilderIndex) {
        specialIndex[0] = clampIndex;
        specialIndex[1] = stringBuilderIndex;
    }

    public int getMetadata(int meta) {
        return meta;
    }

    protected int getLocalizationMeta(ItemStack itemStack) {
        return itemStack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int pos = MathHelper.clamp_int(
                getLocalizationMeta(itemstack),
                0,
                (specialIndex[0] > -1) ? specialIndex[0] : (blockType.length - 1));
        int sbIndex = (specialIndex[1] > -1) ? pos : (specialIndex[1] - pos);
        if (sbIndex < 0) sbIndex = -1 * sbIndex;
        try {
            return unlocalizedName + "." + blockType[sbIndex - 1] + append;
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.warn("[MultiItemBlock] Caught array index error in getUnlocalizedName: {}", ex.getMessage());
            logger.warn("[MultiItemBlock] Returning unlocalized name: {}", getUnlocalizedName());
            return getUnlocalizedName();
        }
    }

}
