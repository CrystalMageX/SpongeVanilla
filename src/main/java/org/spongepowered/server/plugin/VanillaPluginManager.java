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
package org.spongepowered.server.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.plugin.meta.PluginMetadata;
import org.spongepowered.server.launch.plugin.PluginCandidate;
import org.spongepowered.server.launch.plugin.VanillaLaunchPluginManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Singleton
public class VanillaPluginManager implements PluginManager {

    private final Map<String, PluginContainer> plugins = Maps.newHashMap();
    private final Map<Object, PluginContainer> pluginInstances = Maps.newIdentityHashMap();

    private void registerPlugin(PluginContainer plugin) {
        this.plugins.put(plugin.getId(), plugin);
        plugin.getInstance().ifPresent(instance -> this.pluginInstances.put(instance, plugin));
    }

    public void loadPlugins() throws IOException {
        for (PluginContainer container : SpongeImpl.getInternalPlugins()) {
            registerPlugin(container);
        }

        // TODO: Dependencies

        for (PluginCandidate candidate : VanillaLaunchPluginManager.getPlugins()) {
            loadPlugin(candidate);
        }
    }

    private void loadPlugin(PluginCandidate candidate) {
        final String id = candidate.getId();

        if (candidate.getSource().isPresent()) {
            try {
                // Add JAR to classpath
                Launch.classLoader.addURL(candidate.getSource().get().toUri().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Failed to add plugin '" + id + "' to classpath", e);
            }
        }

        Object source = candidate.getSource().orElse(null);
        if (source == null) {
            source = "classpath";
        }

        final PluginMetadata metadata = candidate.getMetadata();
        final String name = metadata.getName() != null ? metadata.getName() : id;

        try {
            Class<?> pluginClass = Class.forName(candidate.getPluginClass());
            PluginContainer container = new VanillaPluginContainer(id, pluginClass,
                    metadata.getName(), metadata.getVersion(), metadata.getDescription(), metadata.getUrl(), metadata.getAuthors(),
                    candidate.getSource());

            registerPlugin(container);
            Sponge.getEventManager().registerListeners(container, container.getInstance().get());

            SpongeImpl.getLogger().info("Loaded plugin: {} {} (from {})", name, container.getVersion().orElse("unknown"), source);
        } catch (Throwable e) {
            SpongeImpl.getLogger().error("Failed to load plugin: {} (from {})", name, source, e);
        }
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        checkNotNull(instance, "instance");

        if (instance instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }

        return Optional.ofNullable(this.pluginInstances.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.ofNullable(this.plugins.get(id));
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return Collections.unmodifiableCollection(this.plugins.values());
    }

    @Override
    public boolean isLoaded(String id) {
        return this.plugins.containsKey(id);
    }

}
