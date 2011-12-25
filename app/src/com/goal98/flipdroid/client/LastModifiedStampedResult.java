package com.goal98.flipdroid.client;

public class LastModifiedStampedResult{
        Object result;
        long lastModified;

        public LastModifiedStampedResult(long lastModified, Object result) {
            this.lastModified = lastModified;
            this.result = result;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }