package com.ab.hicarerun.network;

/**
 * Created by yogi on 10/12/16.
 */

public class RequestHeader {
  private String headerName;
  private String headerValue;

  public String getHeaderName() {
    return headerName;
  }

  public void setHeaderName(String headerName) {
    this.headerName = headerName;
  }

  public String getHeaderValue() {
    return headerValue;
  }

  public void setHeaderValue(String headerValue) {
    this.headerValue = headerValue;
  }
}
