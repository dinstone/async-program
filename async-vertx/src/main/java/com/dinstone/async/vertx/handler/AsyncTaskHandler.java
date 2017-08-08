package com.dinstone.async.vertx.handler;

import com.dinstone.async.vertx.util.HttpClientUtil;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.RoutingContext;

public class AsyncTaskHandler implements Handler<RoutingContext> {

	private HttpClient httpClient;

	public AsyncTaskHandler(Vertx vertx) {
		httpClient = vertx.createHttpClient();
	}

	@Override
	public void handle(RoutingContext rc) {
		sync(rc);
		// async(rc);
	}

	private void async(RoutingContext rc) {
		long s = System.currentTimeMillis();
		String reqUrl = "https://www.baidu.com/";
		httpClient.getAbs(reqUrl).handler(res -> {
			res.bodyHandler(buffer -> {
				long e = System.currentTimeMillis();
				System.out.println("access " + reqUrl + " take's " + (e - s) + "ms");
				rc.response().end("Served at: async");
			}).exceptionHandler(t -> {
				rc.response().end("Served at: async response exception");
			});
		}).exceptionHandler(t -> {
			rc.response().end("Served at: async request exception");
		}).end();
	}

	private void sync(RoutingContext rc) {
		rc.vertx().executeBlocking(f -> {
			try {
				String reqUrl = "https://www.baidu.com/";
				long s = System.currentTimeMillis();
				HttpClientUtil.netForm(reqUrl, null, null);
				long e = System.currentTimeMillis();

				System.out.println("access " + reqUrl + " take's " + (e - s) + "ms");
			} catch (Exception e) {
				e.printStackTrace();
			}

			f.complete();
		}, false, rh -> {
			rc.response().end("Served at: sync");
		});
	}

}
