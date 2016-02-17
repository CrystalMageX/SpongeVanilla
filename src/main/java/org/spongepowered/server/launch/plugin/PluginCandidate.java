/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.server.launch.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.plugin.meta.PluginMetadata;

import java.nio.file.Path;
import java.util.Optional;

import javax.annotation.Nullable;

public final class PluginCandidate {

    private final String id;
    private final String pluginClass;
    private final Optional<Path> source;

    private PluginMetadata metadata;

    PluginCandidate(String pluginClass, @Nullable Path source, PluginMetadata metadata) {
        this.pluginClass = checkNotNull(pluginClass, "pluginClass");
        this.source = Optional.ofNullable(source);
        this.metadata = checkNotNull(metadata, "metadata");
        this.id = metadata.getId();
    }

    public String getId() {
        return this.id;
    }

    public String getPluginClass() {
        return this.pluginClass;
    }

    public Optional<Path> getSource() {
        return this.source;
    }

    public PluginMetadata getMetadata() {
        return this.metadata;
    }

    void setMetadata(PluginMetadata metadata) {
        this.metadata = checkNotNull(metadata, "metadata");
    }

}
