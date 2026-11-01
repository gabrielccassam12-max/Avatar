package com.avatar.client;

import com.avatar.net.SyncPayload;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

/** Latest server data for the HUD, casting poses, who is in Avatar State and the camera shake. */
public final class ClientState {
    private ClientState() {}

    public static final class Vis {
        public boolean avatar;
        public int element = -1;
    }

    private static final Map<Integer, int[]> POSES = new HashMap<>();
    public static final Map<Integer, Vis> VIS = new HashMap<>();

    public static SyncPayload last;
    public static long[] cdEnd = new long[64];
    public static float shownEnergy;
    private static long clock;
    private static int shakeStrength, shakeLeft, shakeTotal;

    public static long time() {
        return clock;
    }

    public static void startPose(int entityId, int pose, int ticks) {
        POSES.put(entityId, new int[]{pose, (int) clock, (int) clock + ticks});
    }

    public static void clear() {
        POSES.clear();
        VIS.clear();
        last = null;
        shakeLeft = 0;
    }

    /** {pose, strength 0..1} for the entity, or null when it is not casting. */
    public static float[] poseFor(int entityId) {
        int[] p = POSES.get(entityId);
        if (p == null) return null;
        float now = clock + MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        float in = Math.min(1f, (now - p[1] + 1f) / 3f);
        float out = Math.min(1f, (p[2] - now) / 4f);
        float k = Math.max(0f, Math.min(in, out));
        return k <= 0f ? null : new float[]{p[0], k};
    }

    public static void setState(int entityId, boolean avatar, int element) {
        Vis v = VIS.computeIfAbsent(entityId, k -> new Vis());
        v.avatar = avatar;
        v.element = element;
    }

    public static void shake(int strength, int ticks) {
        if (strength >= shakeStrength * (shakeLeft / (float) Math.max(1, shakeTotal))) {
            shakeStrength = strength;
            shakeLeft = ticks;
            shakeTotal = ticks;
        }
    }

    public static void set(SyncPayload p) {
        last = p;
        if (p == null) return;
        for (int i = 0; i < cdEnd.length && i < p.cd().length; i++) cdEnd[i] = clock + p.cd()[i];
    }

    public static void tick(MinecraftClient mc) {
        clock++;
        POSES.values().removeIf(v -> clock > v[2]);
        if (mc.world == null) {
            VIS.clear();
        } else {
            VIS.keySet().removeIf(id -> mc.world.getEntityById(id) == null);
        }
        if (shakeLeft > 0) {
            shakeLeft--;
            if (mc.player != null && !mc.isPaused()) {
                float k = shakeLeft / (float) Math.max(1, shakeTotal);
                float amp = shakeStrength * 0.16f * k;                       // degrees of view jitter
                mc.player.setYaw(mc.player.getYaw() + (float) (Math.random() - 0.5) * 2f * amp);
                mc.player.setPitch(Math.max(-90f, Math.min(90f, mc.player.getPitch() + (float) (Math.random() - 0.5) * 2f * amp)));
            }
        }
    }
}
