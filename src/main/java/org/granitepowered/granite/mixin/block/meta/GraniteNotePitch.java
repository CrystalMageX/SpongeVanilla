package org.granitepowered.granite.mixin.block.meta;

import org.spongepowered.api.block.meta.NotePitch;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class GraniteNotePitch implements NotePitch {

    private final byte id;
    private final String name;

    public GraniteNotePitch(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public byte getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
