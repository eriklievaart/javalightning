package com.eriklievaart.javalightning.control.param;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.eriklievaart.javalightning.api.MultiPartParameter;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class MultiParameters extends AbstractParameters<MultiPartParameter> {
	private LogTemplate log = new LogTemplate(getClass());

	public MultiParameters(Map map) {
		super(map);
	}

	@Override
	public List getStrings(String key) {
		return null;
	}

	@Override
	public String getString(String key) {
		try {
			MultiPartParameter mp = getMultiPart(key);
			FormattedException.on(mp == null, "Unable to read parameter %", key);
			String value = mp.getAsString();
			Check.notNull(value, "Framework Error: Value of % should not be <null>, but an empty String.", key);
			log.trace("retrieving parameter %  as string %", key, value);
			return value;

		} catch (IOException e) {
			throw new RuntimeIOException("Framwork Error: unable to read parameter, this should never happen.", e);
		}
	}

	@Override
	public MultiPartParameter getMultiPart(String key) throws IOException {
		return delegate.get(key);
	}

}
