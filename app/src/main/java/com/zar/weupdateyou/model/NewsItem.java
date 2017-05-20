package com.zar.weupdateyou.model;

/**
 * Created by KJS on 11/14/2016.
 */
public class NewsItem {

   public int type; // 0: news 1: section
   public String guid;
   public String title;   // name when it is section : read, unread
   public String description;
   public String imgLink;
   public String link;
   public String timeText;
   public long timeDiff;
   public String tag;
   public String source;
   public String shares;
   public int originalIndex;
   public boolean isHot;
   public String read;
   public boolean isDismissed = false;
   public NewsItem()
   {
      type = 0;
      guid="";
      title="";
      imgLink="";
      link = "";
      timeText = "";
      tag = "";
      source ="";
      shares = "";
      description = "";
      originalIndex = -1;
      isHot = false;
      read = "";
      isDismissed = false;

   }


}
