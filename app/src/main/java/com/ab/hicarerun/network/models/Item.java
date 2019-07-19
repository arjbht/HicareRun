package com.ab.hicarerun.network.models;

/**
 * Created by Arjun Bhatt on 7/19/2019.
 */
public class Item {

        private String title;
        private String path;
        private String imagepath;

        public String getImagepath() {
            return imagepath;
        }

        public void setImagepath(String imagepath) {
            this.imagepath = imagepath;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

}
