package com.ab.hicarerun;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by arjun on 03/05/19.
 */

public abstract class ListBaseFragment extends BaseFragment {

  public boolean isLastItemDisplaying(RecyclerView recyclerView) {
    if (recyclerView.getAdapter().getItemCount() != 0) {
      int lastVisibleItemPosition =
          ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
      if (lastVisibleItemPosition != RecyclerView.NO_POSITION
          && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
        return true;
      }
    }
    return false;
  }
}
