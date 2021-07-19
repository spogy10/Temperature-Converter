package com.jr.poliv.temperatureconverteralpha.classes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

public class BojFeed {
    @Root
    @Namespace(reference="http://www.w3.org/2005/Atom", prefix="atom")
    public class Rss {
        @Element
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }
    }

    public class Channel {
        @Element
        private String description;

        @Element
        private Item item;

        public String getDescription() {
            return description;
        }

        public Item getItem() {
            return item;
        }
    }

    public class Item{
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

    public class Guid{
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
