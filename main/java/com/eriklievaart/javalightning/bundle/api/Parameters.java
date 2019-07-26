package com.eriklievaart.javalightning.bundle.api;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.toolkit.convert.api.ConversionException;

public interface Parameters {

	public boolean contains(String key);

	public boolean containsAll(String... keys);

	public abstract List<String> getStrings(String key);

	public abstract String getString(String key);

	public abstract String getString(String key, String fallback);

	public void getString(String key, Consumer<String> ifPresent);

	public int getInteger(String key) throws ConversionException;

	public int getInteger(String key, int fallback) throws ConversionException;

	public void getInteger(String key, Consumer<Integer> ifPresent) throws ConversionException;

	public long getLong(String key) throws ConversionException;

	public long getLong(String key, long fallback) throws ConversionException;

	public void getLong(String key, Consumer<Long> ifPresent) throws ConversionException;

	public abstract MultiPartParameter getMultiPart(String key) throws IOException;

	public Collection<String> getKeys();

	public Map<String, String> getMap();

	public Map<String, String> getMap(String... keys);
}
