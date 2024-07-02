package zeh.mingle.fluids;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import zeh.mingle.AllTags;
import zeh.mingle.foundation.utility.Color;

import java.util.function.Consumer;

public class MoltenFluidType extends FluidType {

    public final ResourceLocation stillTexture;
    public final ResourceLocation flowingTexture;
    public final ResourceLocation overlayTexture;
    public final ResourceLocation renderOverlayTexture;
    private final int color;
    private final Vector3f fogColor;

    public MoltenFluidType(Properties properties,
                           ResourceLocation stillTexture, ResourceLocation flowingTexture,
                           ResourceLocation overlayTexture, ResourceLocation renderOverlayTexture,
                           int color, int fog) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.renderOverlayTexture = renderOverlayTexture;
        this.color = color;
        this.fogColor = new Color(fog, false).asVectorF();
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return overlayTexture;
            }

            @Override
            public int getTintColor() {
                return color;
            }

            @Override
            public @NotNull Vector3f modifyFogColor(@NotNull Camera camera, float partialTick, @NotNull ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, @NotNull Vector3f fluidFogColor) {
                return fogColor;
            }

            @Override
            public void modifyFogRender(@NotNull Camera camera, @NotNull FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, @NotNull FogShape shape) {
                RenderSystem.setShaderFogStart(-8F);
                RenderSystem.setShaderFogEnd(1.0f);
            }

        });
    }

    @Override
    public boolean move(@NotNull FluidState state, LivingEntity entity, @NotNull Vec3 movementVector, double gravity) {
        if (!entity.isOnFire()) entity.setRemainingFireTicks(4);
        else entity.setRemainingFireTicks(20 * 4);
        entity.setSpeed(0.25f);
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.25f));
        return false;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        super.setItemMovement(entity);
        entity.lavaHurt();
    }

}