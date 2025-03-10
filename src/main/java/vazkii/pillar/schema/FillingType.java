/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Pillar Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Pillar
 * <p>
 * Pillar is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [25/06/2016, 22:40:45 (GMT)]
 */
package vazkii.pillar.schema;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum FillingType {

    AIR(FillingType::airCondition),
    WATER(FillingType::waterCondition),
    AIR_WATER(FillingType::combinedCondition);

    private final FillingCondition condition;

    FillingType(FillingCondition condition) {
        this.condition = condition;
    }

    private static boolean airCondition(World world, IBlockState state, BlockPos pos) {
        return (state.getBlock().isAir(state, world, pos) || state.getBlock().isReplaceable(world, pos)) && !(state.getBlock() instanceof BlockLiquid);
    }

    private static boolean waterCondition(World world, IBlockState state, BlockPos pos) {
        return state.getBlock() instanceof BlockLiquid;
    }

    private static boolean combinedCondition(World world, IBlockState state, BlockPos pos) {
        return state.getBlock() instanceof BlockLiquid || state.getBlock().isAir(state, world, pos) || state.getBlock().isReplaceable(world, pos);
    }

    public boolean canFill(World world, IBlockState state, BlockPos pos) {
        return condition.canFill(world, state, pos);
    }

    public interface FillingCondition {
        boolean canFill(World world, IBlockState state, BlockPos pos);
    }
}
