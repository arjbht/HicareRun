package com.ab.hicarerun.network;

/**
 * Created by yogi on 10/12/16.
 */

public interface NetworkResponseListner<T> {
  void onResponse(int requestCode, T response);

  void onFailure(int requestCode);
}
