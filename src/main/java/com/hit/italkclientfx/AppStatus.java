package com.hit.italkclientfx;

import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.User;

public class AppStatus {

    private User currentUser;
    private Post currentPost;
    private Comment currentComment;
    private String currentSearch;
    private boolean VerificationFlag;

    public AppStatus() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Post getCurrentPost() {
        return currentPost;
    }

    public void setCurrentPost(Post currentPost) {
        this.currentPost = currentPost;
    }

    public Comment getCurrentComment() {
        return currentComment;
    }

    public void setCurrentComment(Comment currentComment) {
        this.currentComment = currentComment;
    }

    public String getCurrentSearch() {
        return currentSearch;
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    public boolean isVerified() {
        return VerificationFlag;
    }

    public void setVerificationFlag(boolean verificationFlag) {
        VerificationFlag = verificationFlag;
    }
}
