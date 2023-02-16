/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022-2023 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.configuration.processors;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import dev.architectury.tinyremapper.TinyRemapper;

import net.fabricmc.loom.api.mappings.layered.MappingsNamespace;
import net.fabricmc.loom.configuration.ConfigContext;
import net.fabricmc.loom.util.LazyCloseable;
import net.fabricmc.loom.util.TinyRemapperHelper;

public final class ContextImplHelper {
	private ContextImplHelper() {
	}

	public static LazyCloseable<TinyRemapper> createRemapper(ConfigContext configContext, MappingsNamespace from, MappingsNamespace to) {
		return new LazyCloseable<>(() -> {
			try {
				TinyRemapper tinyRemapper = TinyRemapperHelper.getTinyRemapper(configContext.project(), configContext.serviceManager(), from.toString(), to.toString());
				tinyRemapper.readClassPath(TinyRemapperHelper.getMinecraftDependencies(configContext.project()));

				for (Path minecraftJar : configContext.extension().getMinecraftJars(MappingsNamespace.INTERMEDIARY)) {
					tinyRemapper.readClassPath(minecraftJar);
				}

				return tinyRemapper;
			} catch (IOException e) {
				throw new UncheckedIOException("Failed to create tiny remapper", e);
			}
		}, TinyRemapper::finish);
	}
}
