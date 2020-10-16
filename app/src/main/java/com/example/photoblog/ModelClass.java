package com.example.photoblog;

public class ModelClass {

    private String imageResource, profileImage, userName, userId, caption;
    private long likesCount, commentsCount;
    private String documentID;

    public ModelClass(String imageResource, String profileImage, long likesCount, long commentsCount,
                      String caption, String userName, String userId,String documentID) {
        this.imageResource = imageResource;
        this.profileImage = profileImage;
        this.userId = userId;
        this.userName = userName;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.caption = caption;
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }
}
