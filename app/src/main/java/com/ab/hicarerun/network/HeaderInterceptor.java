package com.ab.hicarerun.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yogi on 10/12/16.
 */

public final class HeaderInterceptor implements Interceptor {
  private final RequestHeader header;

  public HeaderInterceptor(RequestHeader header) {
    this.header = header;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    final Request original = chain.request();
    Response response;
    if (header != null && header.getHeaderName() != null && header.getHeaderValue() != null) {
      Request request = original.newBuilder()
          .header(header.getHeaderName(), header.getHeaderValue())
          .method(original.method(), original.body())
          .build();
      response = chain.proceed(request);
    } else {
      response = chain.proceed(original);
    }

    return response;
  }
}
