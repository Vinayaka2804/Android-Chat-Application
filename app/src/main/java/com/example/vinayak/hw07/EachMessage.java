package com.example.vinayak.hw07;

/**
 * Created by Vinayaka on 11/22/2016.
 */
public class EachMessage {

    String text,time,image,from,to,pushid;
    int read,imgdelcount;

    public int getImgdelcount() {
        return imgdelcount;
    }

    public void setImgdelcount(int imgdelcount) {
        this.imgdelcount = imgdelcount;
    }

    public String getPushid() {
        return pushid;
    }

    public void setPushid(String pushid) {
        this.pushid = pushid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
