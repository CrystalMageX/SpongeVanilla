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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public final class PluginCandidate {

    private final String id;
    private final String pluginClass;
    private final Optional<Path> source;

    private PluginMetadata metadata;

    @Nullable private Set<PluginCandidate> requirements;
    @Nullable private Set<String> missingRequirements;

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

    public Set<String> getMissingRequirements() {
        checkState(this.missingRequirements != null, "Requirements not collected yet");
        return this.missingRequirements;
    }

    public boolean updateRequirements() {
        checkState(this.requirements != null, "Requirements not collected yet");
        if (this.requirements.isEmpty()) {
            return true;
        }

        Iterator<PluginCandidate> itr = this.requirements.iterator();
        while (itr.hasNext()) {
            final PluginCandidate candidate = itr.next();
            if (!candidate.getMissingRequirements().isEmpty()) {
                itr.remove();
                this.missingRequirements.add(candidate.getId());
            }
        }

        return !this.missingRequirements.isEmpty();
    }

    public boolean collectRequirements(Collection<String> loadedPlugins, Map<String, PluginCandidate> candidates) {
        checkState(this.requirements == null, "Requirements already collected");

        if (this.metadata.getRequiredDependencies().isEmpty()) {
            this.requirements = ImmutableSet.of();
            this.missingRequirements = ImmutableSet.of();
            return true;
        }

        this.requirements = new HashSet<>();
        this.missingRequirements = new HashSet<>();

        for (PluginMetadata.Dependency dependency : this.metadata.getRequiredDependencies()) {
            final String id = dependency.getId();
            if (id.equals(this.id) || loadedPlugins.contains(id)) {
                continue;
            }

            PluginCandidate candidate = candidates.get(id);
            if (candidate != null) {
                this.requirements.add(candidate);
            } else {
                this.missingRequirements.add(id);
            }
        }

        return this.missingRequirements.isEmpty();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PluginCandidate candidate = (PluginCandidate) o;
        return this.id.equals(candidate.id);

    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}
