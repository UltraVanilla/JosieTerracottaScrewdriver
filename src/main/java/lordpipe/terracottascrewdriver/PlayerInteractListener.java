package lordpipe.terracottascrewdriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

public class PlayerInteractListener implements Listener {
    private TerracottaScrewdriver plugin;

    public PlayerInteractListener(TerracottaScrewdriver pl) {
        plugin = pl;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // must be holding brick
        if (item == null || item.getType() != Material.BRICK) return;
        // block must exist
        if (block == null) return;

        BlockData data = block.getBlockData();

        Material blockType = block.getType();

        // do not allow modification of double chests
        if (blockType == Material.CHEST && ((Chest) data).getType() != Chest.Type.SINGLE) return;
        // do not allow modification of nether and end portals
        if (Tag.PORTALS.isTagged(blockType)) return;
        if (Tag.FEATURES_CANNOT_REPLACE.isTagged(blockType)) return;
        // do not allow modification of technical blocks
        if (blockType == Material.COMMAND_BLOCK) return;
        if (blockType == Material.CHAIN_COMMAND_BLOCK) return;
        if (blockType == Material.REPEATING_COMMAND_BLOCK) return;
        if (blockType == Material.STRUCTURE_BLOCK) return;
        // will float if turned
        if (blockType == Material.PUMPKIN_STEM) return;
        if (blockType == Material.MELON_STEM) return;
        if (Tag.WALL_SIGNS.isTagged(blockType)) return;
        if (blockType == Material.WALL_TORCH) return;
        if (blockType == Material.SOUL_WALL_TORCH) return;
        if (blockType == Material.REDSTONE_WALL_TORCH) return;
        if (Tag.BEDS.isTagged(blockType)) return;
        if (Tag.DOORS.isTagged(blockType)) return;
        if (Tag.TALL_FLOWERS.isTagged(blockType)) return;
        if (blockType == Material.TALL_GRASS) return;
        if (blockType == Material.LARGE_FERN) return;
        if (blockType == Material.PISTON_HEAD) return;
        if (blockType == Material.PISTON && ((Piston) data).isExtended()) return;
        if (blockType == Material.SMALL_DRIPLEAF) return;
        if (blockType == Material.BIG_DRIPLEAF) return;
        if (blockType == Material.BIG_DRIPLEAF_STEM) return;
        // player shouldn't be able to target these blocks, but just in case
        if (blockType == Material.WATER) return;
        if (blockType == Material.LAVA) return;

        boolean madeChanges = false;

        boolean forwards = action == Action.LEFT_CLICK_BLOCK;

        if (data instanceof Orientable) {
            madeChanges = true;
            Orientable orientable = (Orientable) data;
            Axis currentAxis = orientable.getAxis();

            ArrayList<Axis> axes = orientable.getAxes().stream().collect(Collectors.toCollection(ArrayList::new));

            int index = axes.indexOf(currentAxis);

            if (forwards) index++;
            else index--;

            if (index < 0) index = axes.size() - 1;
            else index %= axes.size();

            orientable.setAxis(axes.get(index));
        } else if (data instanceof Directional) {
            madeChanges = true;
            Directional directional = (Directional) data;
            BlockFace currentDirection = directional.getFacing();

            ArrayList<BlockFace> directions = directional.getFaces().stream().collect(Collectors.toCollection(ArrayList::new));
            Collections.swap(directions, 1, 2);

            int index = directions.indexOf(currentDirection);

            if (forwards) index++;
            else index--;

            boolean flipover = false;

            if (index < 0) {
                index = directions.size() - 1;
                flipover = true;
            } else {
                index %= directions.size();
            }

            directional.setFacing(directions.get(index));
            
            if (data instanceof Bisected && ((index == 0 && forwards) || flipover)) {
                // if we are starting from the first orientation, flip the block
                Bisected bisected = (Bisected) data;
                Bisected.Half currentHalf = bisected.getHalf();

                if (currentHalf == Bisected.Half.BOTTOM) bisected.setHalf(Bisected.Half.TOP);
                else if (currentHalf == Bisected.Half.TOP) bisected.setHalf(Bisected.Half.BOTTOM);
            } else if (data instanceof FaceAttachable && ((index == 0 && forwards) || flipover)) {
                // if we are starting from the first orientation, rotate through attached states
                FaceAttachable attachable = (FaceAttachable) data;
                FaceAttachable.AttachedFace currentAttached = attachable.getAttachedFace();

                List<FaceAttachable.AttachedFace> attachableFaces = Arrays.asList(FaceAttachable.AttachedFace.values());

                int attachableIndex = attachableFaces.indexOf(currentAttached);

                attachableIndex++;
                attachableIndex %= directions.size();

                attachable.setAttachedFace(attachableFaces.get(attachableIndex));
            }
        } else if (data instanceof Bisected) {
            madeChanges = true;
            Bisected bisected = (Bisected) data;
            Bisected.Half currentHalf = bisected.getHalf();

            if (currentHalf == Bisected.Half.BOTTOM) bisected.setHalf(Bisected.Half.TOP);
            else if (currentHalf == Bisected.Half.TOP) bisected.setHalf(Bisected.Half.BOTTOM);
        } else if (data instanceof Slab) {
            madeChanges = true;
            Slab slab = (Slab) data;
            Slab.Type currentHalf = slab.getType();

            if (currentHalf == Slab.Type.BOTTOM) slab.setType(Slab.Type.TOP);
            else if (currentHalf == Slab.Type.TOP) slab.setType(Slab.Type.BOTTOM);
        }

        if (madeChanges) {
            event.setCancelled(true);
            BlockState originalState = block.getState();

            block.setBlockData(data, true);
            block.getState().update(true);

            CraftBlock craftBlock = (CraftBlock) block;

            BlockPos pos = craftBlock.getPosition();

            LevelAccessor handle = craftBlock.getHandle();

            BlockPos.MutableBlockPos curPos = new BlockPos.MutableBlockPos();
            Direction[] aenumdirection = new Direction[] {
                Direction.WEST, Direction.EAST, Direction.NORTH,
                Direction.SOUTH, Direction.DOWN, Direction.UP
            };
            for (int l = 0; l < aenumdirection.length; ++l) {
                Direction enumdirection = aenumdirection[l];
                curPos.setWithOffset(pos, enumdirection);
                net.minecraft.world.level.block.state.BlockState state = handle.getBlockState(curPos);
                state.updateNeighbourShapes(handle, curPos, 3);
            }
            net.minecraft.world.level.block.state.BlockState state = handle.getBlockState(pos);
            state.updateNeighbourShapes(handle, pos, 3);

            new BlockPlaceEvent(block, originalState, block, item, player, true, EquipmentSlot.HAND)
                .callEvent();
        }
    }
}
