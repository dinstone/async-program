package com.dinstone.async.vertx.handler;

import com.dinstone.async.vertx.util.HttpClientUtil;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.RoutingContext;

public class AsyncTaskHandler implements Handler<RoutingContext> {

	private HttpClient httpClient;

	public AsyncTaskHandler(Vertx vertx) {
		httpClient = vertx.createHttpClient();
	}

	@Override
	public void handle(RoutingContext rc) {
		// sync(rc);
		// async(rc);
		multiAction(rc);
	}

	private void multiAction(RoutingContext rc) {
		Future<Buffer> action2Future = asyncHttpCall("http://localhost:8180/async-vertx/404");
		Future<Buffer> action1Future = asyncHttpCall("https://www.baidu.com/");

		// all ok then result is ok,
		CompositeFuture.all(action1Future, action2Future).setHandler(ar -> {
			if (ar.succeeded()) {
				System.out.println("very good,both ok");
				for (Object element : ar.result().list()) {
					System.out.println("result = " + element);
				}
				rc.response().end("processing is ok");
			} else {
				System.out.println("some one is failed");

				CompositeFuture cf = (CompositeFuture) ar;
				for (int i = 0; i < cf.size(); i++) {
					if (cf.failed(i)) {
						System.out.println(cf.cause(i));
					} else {
						System.out.println("i'm ok " + cf.resultAt(i));
					}
				}

				rc.response().end("processing is failed");
			}
		});

	}

	private Future<Buffer> asyncHttpCall(String reqUrl) {
		Future<Buffer> future = Future.future();

		long s = System.currentTimeMillis();
		httpClient.getAbs(reqUrl).handler(res -> {
			if (res.statusCode() >= 400) {
				future.fail(res.statusMessage());
				return;
			}

			res.bodyHandler(buffer -> {
				long e = System.currentTimeMillis();
				System.out.println("access " + reqUrl + " take's " + (e - s) + "ms");

				future.complete(buffer);
			}).exceptionHandler(t -> {
				future.fail(t);
			});
		}).exceptionHandler(t -> {
			future.fail(t);
		}).end();

		return future;
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
