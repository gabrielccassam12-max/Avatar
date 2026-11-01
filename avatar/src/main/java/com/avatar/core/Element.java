package com.avatar.core;

import net.minecraft.text.Text;

/** The four nations. color = HUD colour, ring/spark = particle names for ground rings. */
public enum Element {
    FIRE(0xFF7A1A, "f_ring", "f_fire", "f_big", "f_core"),
    WATER(0x3FA9FF, "w_ring", "w_drop", "w_big", "w_ice"),
    EARTH(0xB08C58, "e_ring", "e_rock", "e_big", "e_dust"),
    AIR(0xDDEBFF, "a_ring", "a_wind", "a_big", "a_streak");

    public final int color;
    public final String ring, small, big, accent;

    Element(int color, String ring, String small, String big, String accent) {
        this.color = color;
        this.ring = ring;
        this.small = small;
        this.big = big;
        this.accent = accent;
    }

    public static Element of(int i) {
        Element[] v = values();
        return i < 0 || i >= v.length ? null : v[i];
    }

    public Text displayName() {
        return Text.translatable("element.avatar." + ordinal());
    }
}
