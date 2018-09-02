package com.eriklievaart.javalightning.control;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.eriklievaart.javalightning.api.MultiPartParameter;
import com.eriklievaart.javalightning.api.Parameters;
import com.eriklievaart.javalightning.control.param.MultiParameters;
import com.eriklievaart.javalightning.control.param.SingleParameters;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class ParametersSupplier implements Supplier<Parameters> {
	private static final String FILENAME_HEADER_ATTRIBUTE = "filename";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String CONTENT_DISPOSITION_HEADER = "content-disposition";

	private LogTemplate log = new LogTemplate(getClass());

	private final HttpServletRequest request;
	private final List<CloseableSilently> closeables;

	public ParametersSupplier(HttpServletRequest request, List<CloseableSilently> closeables) {
		this.request = request;
		this.closeables = closeables;
	}

	@Override
	public Parameters get() {
		String contentType = request.getHeader(CONTENT_TYPE_HEADER);
		log.debug("contentType: $", contentType);
		if (contentType == null || !contentType.toLowerCase().trim().startsWith("multipart/")) {
			return createSimpleParameterMap();
		}
		try {
			return getMultiParts();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeIOException("unable to process upload; " + e.getMessage(), e);
		}
	}

	private Parameters getMultiParts() throws IOException, ServletException {
		Collection<Part> parts = request.getParts();
		Map<String, MultiPartParameter> multiparts = NewCollection.map();

		log.trace("parts.size() = $", parts.size());
		for (Part part : parts) {
			for (String header : part.getHeaderNames()) {
				log.debug("header found $: $", header, part.getHeader(header));
			}
			String header = part.getHeader(CONTENT_DISPOSITION_HEADER);
			MultiPartParameter mpp = MultiPartParameter.instance(getFileName(header), part.getInputStream());
			closeables.add(mpp);
			multiparts.put(part.getName(), mpp);
		}
		return new MultiParameters(multiparts);
	}

	private String getFileName(String header) {
		Map<String, String> parseHttpHeader = HeaderParser.parse(header);

		String filename = parseHttpHeader.get(FILENAME_HEADER_ATTRIBUTE);

		if (Str.isBlank(filename)) {
			return "unknown";
		}
		log.trace("filename found: %", filename);
		return filename;
	}

	private Parameters createSimpleParameterMap() {
		log.trace("singlepart form");
		Map<String, List<String>> parameters = NewCollection.map();
		request.getParameterMap().forEach((key, array) -> parameters.put(key, Arrays.asList(array)));
		return new SingleParameters(parameters);
	}
}
