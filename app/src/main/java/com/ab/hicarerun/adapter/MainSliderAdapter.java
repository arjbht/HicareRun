package com.ab.hicarerun.adapter;

import com.ab.hicarerun.R;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * Created by Arjun Bhatt on 6/24/2019.
 */
public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.bed_bugs);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.cockroach);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.cockroaches);
                break;
            case 3:
                viewHolder.bindImageSlide(R.drawable.termite);
                break;

        }
    }
}