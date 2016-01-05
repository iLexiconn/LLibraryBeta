package net.ilexiconn.llibrary.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author iLexiconn
 * @see net.ilexiconn.llibrary.common.block.BlockMountable
 * @since 0.1.0
 */
public class EntityMountableBlock extends Entity {
    public int blockPosX;
    public int blockPosY;
    public int blockPosZ;
    public Block block;

    public EntityMountableBlock(World world) {
        super(world);
    }

    public EntityMountableBlock(World world, int x, int y, int z, float mountX, float mountY, float mountZ) {
        super(world);
        noClip = true;
        preventEntitySpawning = true;
        blockPosX = x;
        blockPosY = y;
        blockPosZ = z;
        block = world.getBlock(x, y, z);

        setPosition(mountX, mountY, mountZ);
        setSize(0f, 0f);
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != player) {
            return true;
        } else {
            if (!worldObj.isRemote) {
                player.mountEntity(this);
            }
            return true;
        }
    }

    @Override
    public void onEntityUpdate() {
        worldObj.theProfiler.startSection("entityBaseTick");
        if (riddenByEntity == null || riddenByEntity.isDead) {
            setDead();
        } else if (worldObj.getBlock(blockPosX, blockPosY, blockPosZ) != block) {
            interactFirst((EntityPlayer) riddenByEntity);
        }
        ticksExisted++;
        worldObj.theProfiler.endSection();
    }

    @Override
    public void entityInit() {
        setSize(0f, 0f);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTag) {

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTag) {

    }
}