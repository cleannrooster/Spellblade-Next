package net.spellbladenext.fabric.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class Hexblade extends HorizontalDirectionalBlock {
    private final VoxelShape[] occlusionByIndex;
    private final Object2IntMap<BlockState> stateToIndex = new Object2IntOpenHashMap();
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final DirectionProperty FACING;

    public Hexblade(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)));
        this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    private static int indexFor(Direction direction) {
        return 1 << direction.get2DDataValue();
    }
    protected VoxelShape[] makeShapes(float f, float g, float h, float i, float j) {
        float k = 8.0F - f;
        float l = 8.0F + f;
        float m = 8.0F - g;
        float n = 8.0F + g;
        VoxelShape voxelShape = Block.box((double)k, 0.0D, (double)k, (double)l, (double)h, (double)l);
        VoxelShape voxelShape2 = Block.box((double)m, (double)i, 0.0D, (double)n, (double)j, (double)n);
        VoxelShape voxelShape3 = Block.box((double)m, (double)i, (double)m, (double)n, (double)j, 16.0D);
        VoxelShape voxelShape4 = Block.box(0.0D, (double)i, (double)m, (double)n, (double)j, (double)n);
        VoxelShape voxelShape5 = Block.box((double)m, (double)i, (double)m, 16.0D, (double)j, (double)n);
        VoxelShape voxelShape6 = Shapes.or(voxelShape2, voxelShape5);
        VoxelShape voxelShape7 = Shapes.or(voxelShape3, voxelShape4);
        VoxelShape[] voxelShapes = new VoxelShape[]{Shapes.empty(), voxelShape3, voxelShape4, voxelShape7, voxelShape2, Shapes.or(voxelShape3, voxelShape2), Shapes.or(voxelShape4, voxelShape2), Shapes.or(voxelShape7, voxelShape2), voxelShape5, Shapes.or(voxelShape3, voxelShape5), Shapes.or(voxelShape4, voxelShape5), Shapes.or(voxelShape7, voxelShape5), voxelShape6, Shapes.or(voxelShape3, voxelShape6), Shapes.or(voxelShape4, voxelShape6), Shapes.or(voxelShape7, voxelShape6)};

        for(int o = 0; o < 16; ++o) {
            voxelShapes[o] = Shapes.or(voxelShape, voxelShapes[o]);
        }

        return voxelShapes;
    }
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }
    protected int getAABBIndex(BlockState blockState) {
        return this.stateToIndex.computeIfAbsent(blockState, (blockStatex) -> {
            int i = 0;
            if ((Boolean)blockStatex.equals(NORTH)) {
                i |= indexFor(Direction.NORTH);
            }

            if ((Boolean)blockStatex.equals(EAST)) {
                i |= indexFor(Direction.EAST);
            }

            if ((Boolean)blockStatex.equals(SOUTH)) {
                i |= indexFor(Direction.SOUTH);
            }

            if ((Boolean)blockStatex.equals(WEST)) {
                i |= indexFor(Direction.WEST);
            }

            return i;
        });
    }
    static {
        FACING = HorizontalDirectionalBlock.FACING;
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("You have triumphed over Magus. Place to ward off Hexblade invasions "));
        list.add(Component.translatable("in a 64 block radius, or carry to prevent yourself from being Hexed."));
        super.appendHoverText(itemStack, blockGetter, list, tooltipFlag);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return this.occlusionByIndex[this.getAABBIndex(blockState)];
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> function) {
        return super.getShapeForEachState(function);
    }

    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.getShape(blockState, blockGetter, blockPos, collisionContext);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53334_) {
        p_53334_.add(FACING,NORTH, EAST, WEST, SOUTH);
    }
}
