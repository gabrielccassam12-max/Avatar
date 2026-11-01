package com.avatar.client;

import com.avatar.Avatar;
import com.avatar.net.ActionPayload;
import com.avatar.net.OpenChoicePayload;
import com.avatar.net.PosePayload;
import com.avatar.net.ShakePayload;
import com.avatar.net.StatePayload;
import com.avatar.net.SyncPayload;
import com.avatar.particle.CubeProfile;
import com.avatar.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.SimpleParticleType;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class AvatarClient implements ClientModInitializer {
    private static final String CAT = "key.categories.avatar";
    /** The seven basic techniques of the element in use. */
    public static final KeyBinding[] SLOT = new KeyBinding[7];
    public static KeyBinding SPECIAL, NEXT, AVATAR, ELEMENT, MENU;
    private static final boolean[] down = new boolean[8];

    private static KeyBinding key(String name, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.avatar." + name, InputUtil.Type.KEYSYM, code, CAT));
    }

    @Override
    public void onInitializeClient() {
        for (Map.Entry<String, SimpleParticleType> en : ModParticles.TYPES.entrySet()) {
            CubeProfile pf = ModParticles.PROFILES.get(en.getKey());
            ParticleFactoryRegistry.getInstance().register(en.getValue(), sprites ->
                    (ParticleFactory<SimpleParticleType>) (type, world, x, y, z, vx, vy, vz) ->
                            new CubeParticle(world, x, y, z, vx, vy, vz, sprites, pf));
        }

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, helper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer r) helper.register(new AvatarFeature(r));
        });

        int[] codes = {GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_N, GLFW.GLFW_KEY_M};
        for (int i = 0; i < SLOT.length; i++) SLOT[i] = key("slot" + (i + 1), codes[i]);
        SPECIAL = key("special", GLFW.GLFW_KEY_G);
        NEXT = key("next", GLFW.GLFW_KEY_R);
        AVATAR = key("avatar", GLFW.GLFW_KEY_H);
        ELEMENT = key("element", GLFW.GLFW_KEY_Y);
        MENU = key("menu", GLFW.GLFW_KEY_U);

        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.ID, (payload, context) -> ClientState.set(payload));
        ClientPlayNetworking.registerGlobalReceiver(PosePayload.ID, (payload, context) ->
                ClientState.startPose(payload.entityId(), payload.pose(), payload.ticks()));
        ClientPlayNetworking.registerGlobalReceiver(StatePayload.ID, (payload, context) ->
                ClientState.setState(payload.entityId(), payload.avatar(), payload.element()));
        ClientPlayNetworking.registerGlobalReceiver(ShakePayload.ID, (payload, context) ->
                ClientState.shake(payload.strength(), payload.ticks()));
        ClientPlayNetworking.registerGlobalReceiver(OpenChoicePayload.ID, (payload, context) ->
                context.client().execute(() -> context.client().setScreen(new ChoiceScreen())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientState.clear());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientState.tick(client);
            if (client.player == null) return;
            boolean free = client.currentScreen == null;
            for (int i = 0; i < SLOT.length; i++) edge(i, free && SLOT[i].isPressed(), 0, i);
            edge(7, free && SPECIAL.isPressed(), 1, 0);
            while (NEXT.wasPressed()) if (free) ClientPlayNetworking.send(new ActionPayload(3, 0, 0));
            while (AVATAR.wasPressed()) if (free) ClientPlayNetworking.send(new ActionPayload(2, 0, 0));
            while (ELEMENT.wasPressed()) if (free) ClientPlayNetworking.send(new ActionPayload(4, 0, 0));
            while (MENU.wasPressed()) if (free) client.setScreen(new MenuScreen());
            for (KeyBinding k : SLOT) while (k.wasPressed()) { }
            while (SPECIAL.wasPressed()) { }
        });

        HudRenderCallback.EVENT.register(AvatarHud::render);
    }

    /** Sends "pressed" and "released" when the key changes state. */
    private static void edge(int idx, boolean now, int kind, int slot) {
        if (now && !down[idx]) ClientPlayNetworking.send(new ActionPayload(kind, slot, 0));
        if (!now && down[idx]) ClientPlayNetworking.send(new ActionPayload(kind, slot, 1));
        down[idx] = now;
    }
}
