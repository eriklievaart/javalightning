package com.eriklievaart.javalightning.bundle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eriklievaart.javalightning.bundle.api.page.RouteType;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class ContentServlet extends HttpServlet {
	private LogTemplate log = new LogTemplate(getClass());

	private MvcBeans beans;

	public ContentServlet(MvcBeans beans) {
		this.beans = beans;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.debug("received request for url %", req.getRequestURL());
		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		RouteType method = RouteType.parse(req.getMethod());
		String path = req.getRequestURI();
		new ContentServletCall(beans, req, res).render(method, path);
	}

}