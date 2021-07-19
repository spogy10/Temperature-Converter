package com.jr.poliv.temperatureconverteralpha.classes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

public class BojFeed {
    @Root
    public static class Rss {

        @Attribute
        private String version;

        @Element
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }

        public String getVersion() {
            return version;
        }
    }

    public static class Channel {

        @Element
        private String lastBuildDate;

        @Element
        private String pubDate;

        @Element
        private String title;

        @ElementList(entry="link", inline=true)
        private List<String> link;

        @Element
        private String description;

        @Element
        private String webMaster;

        @Element
        private Item item;

        public String getDescription() {
            return description;
        }

        public Item getItem() {
            return item;
        }

        public String getLastBuildDate() {
            return lastBuildDate;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getTitle() {
            return title;
        }

        public String getWebMaster() {
            return webMaster;
        }
    }

    public static class Item{
        @Element
        private String title;

        @Element
        private String link;

        @Element
        private String description;

        @Element
        private String pubDate;

        @Element
        private Guid guid;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }

        public String getPubDate() {
            return pubDate;
        }

        public Guid getGuid() {
            return guid;
        }
    }

    public static class Guid{
        @Attribute
        private boolean isPermaLink;

        @Text
        private String value;

        public boolean isPermaLink() {
            return isPermaLink;
        }

        public String getValue() {
            return value;
        }
    }
}
