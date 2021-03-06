package com.eriklievaart.javalightning.zdemo.controller;

import com.eriklievaart.javalightning.bundle.api.ResponseBuilder;
import com.eriklievaart.javalightning.bundle.api.exception.ExternalRedirectException;
import com.eriklievaart.javalightning.bundle.api.page.PageController;

public class ExternalRedirectController implements PageController {

	@Override
	public void invoke(ResponseBuilder builder) {
		throw new ExternalRedirectException("http://www.github.com");
	}
}
