package com.han.model;

import java.util.List;

public class ImgData {
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    String error;
    List<Data> data;

    public class Data {
        Urls urls;

        String uid;

        String ext;

        public String getUid() {
            return uid;
        }

        public void setUid(final String uid) {
            this.uid = uid;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(final String ext) {
            this.ext = ext;
        }

        public Urls getUrls() {
            return urls;
        }

        public void setUrls(Urls urls) {
            this.urls = urls;
        }

        public class Urls {
            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }

            String original;
        }
    }


}