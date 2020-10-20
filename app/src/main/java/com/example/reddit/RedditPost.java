package com.example.reddit;


import java.util.Date;

public class RedditPost extends BlogPostId{
    public String user_id, desc;
    public Date timestamp;

    public RedditPost(String user_id, String desc, Date timestamp){
        this.user_id = user_id;
        this.desc = desc;
        this.timestamp = timestamp;
    }
    public RedditPost(){
        this.user_id = "John Doe";
        this.desc = "This Message contains No Message";
        this.timestamp = timestamp;
    }
    public String getUser_id(){
        return user_id;

    }

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public Date getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }

    public void setDesc(String desc){
        this.desc = desc;

    }

    public String getDesc(){
        return desc;
    }
}
