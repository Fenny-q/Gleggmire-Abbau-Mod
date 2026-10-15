// Gleggmire hat keine Haare

package de.gleggexe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class GleggClient implements ClientModInitializer {

    BlockPos block = null;
    int bild = 0;
    Random random = new Random();

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player != null && client.level != null
                    && client.screen == null
                    && client.options.keyAttack.isDown()
                    && client.hitResult instanceof BlockHitResult hit
                    && !client.level.getBlockState(hit.getBlockPos()).isAir()) {

                BlockPos neu = hit.getBlockPos();

                if (!neu.equals(block)) {
                    block = neu;
                    bild = random.nextInt(10) + 1;
                }

            } else {
                block = null;
                bild = 0;
            }
        });

        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {

            if (block == null || bild == 0) {
                return;
            }

            Identifier textur = Identifier.fromNamespaceAndPath(
                    "gleggexe",
                    "textures/block/glegg" + bild + ".png"
            );

            Vec3 kamera = context.worldState().cameraRenderState.pos;
            PoseStack p = context.matrices();

            p.pushPose();

            p.translate(
                    block.getX() - kamera.x,
                    block.getY() - kamera.y,
                    block.getZ() - kamera.z
            );

            VertexConsumer v = context.consumers().getBuffer(
                    RenderTypes.entityCutoutNoCull(textur)
            );

            wuerfel(v, p, -0.003f, 1.003f);

            Minecraft mc = Minecraft.getInstance();

            if (mc.gameMode != null) {

                int stufe = mc.gameMode.getDestroyStage();

                if (stufe >= 0 && stufe < 10) {

                    Identifier risse = Identifier.fromNamespaceAndPath(
                            "minecraft",
                            "textures/block/destroy_stage_" + stufe + ".png"
                    );

                    VertexConsumer v2 = context.consumers().getBuffer(
                            RenderTypes.entityTranslucent(risse)
                    );

                    wuerfel(v2, p, -0.007f, 1.007f);
                }
            }

            p.popPose();
        });
    }

    void wuerfel(VertexConsumer v, PoseStack p, float a, float b) {

        seite(v, p, a,a,b, b,a,b, b,b,b, a,b,b);
        seite(v, p, b,a,a, a,a,a, a,b,a, b,b,a);
        seite(v, p, a,b,b, b,b,b, b,b,a, a,b,a);
        seite(v, p, a,a,a, b,a,a, b,a,b, a,a,b);
        seite(v, p, b,a,b, b,a,a, b,b,a, b,b,b);
        seite(v, p, a,a,a, a,a,b, a,b,b, a,b,a);
    }

    void seite(VertexConsumer v, PoseStack p,
               float x1,float y1,float z1,
               float x2,float y2,float z2,
               float x3,float y3,float z3,
               float x4,float y4,float z4) {

        punkt(v,p,x1,y1,z1,0,1);
        punkt(v,p,x2,y2,z2,1,1);
        punkt(v,p,x3,y3,z3,1,0);
        punkt(v,p,x4,y4,z4,0,0);
    }

    void punkt(VertexConsumer v, PoseStack p,
               float x,float y,float z,float u,float w) {

        v.addVertex(p.last(),x,y,z)
                .setColor(255,255,255,255)
                .setUv(u,w)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(0,1,0);
    }
}
