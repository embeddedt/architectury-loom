/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 FabricMC
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

package net.fabricmc.loom.decompilers.linemap;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;

/**
 * A reader for line map files that in the format of {@link net.fabricmc.loom.decompilers.LineNumberRemapper}.
 * {@linkplain #accept Accepts} a {@link LineMapVisitor} that processes the contents.
 *
 * @author Juuz
 */
public final class LineMapReader implements Closeable {
	private final BufferedReader reader;

	public LineMapReader(BufferedReader reader) {
		this.reader = reader;
	}

	public void accept(LineMapVisitor visitor) throws IOException {
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.isBlank()) continue;

			String[] parts = line.trim().split("\t");

			try {
				if (!line.startsWith("\t")) {
					visitor.visitClass(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
				} else {
					visitor.visitLine(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				}
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				throw new RuntimeException("Could not parse line: " + line, e);
			}
		}
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}