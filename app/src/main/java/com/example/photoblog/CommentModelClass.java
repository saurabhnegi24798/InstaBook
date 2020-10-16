package com.example.photoblog;

public class CommentModelClass {
    private String mImageUri,comment;

    public CommentModelClass(String mImageUri, String comment) {
        this.mImageUri = mImageUri;
        this.comment = comment;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
