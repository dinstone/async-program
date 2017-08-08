package com.dinstone.async.tomcat;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AsyncTaskServlet
 */
public class AsyncTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final ExecutorService executorService = Executors.newFixedThreadPool(3);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AsyncTaskServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// async(response);

		sync(response);
	}

	private void sync(HttpServletResponse response) throws IOException {
		rest("https://www.baidu.com/");

		response.getWriter().append("Served at: ").append("sync");
	}

	private void async(HttpServletResponse response) throws IOException {
		Future<Boolean> future = executorService.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				rest("https://www.baidu.com/");

				return true;
			}

		});

		try {
			// wait task ok
			future.get();
		} catch (Exception e) {
		}

		response.getWriter().append("Served at: ").append("async");
	}

	private void rest(String reqUrl) {
		try {
			long s = System.currentTimeMillis();
			HttpClientUtil.netForm(reqUrl, null, null);
			long e = System.currentTimeMillis();

			System.out.println("access " + reqUrl + " take's " + (e - s) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();

		executorService.shutdown();
	}

}
