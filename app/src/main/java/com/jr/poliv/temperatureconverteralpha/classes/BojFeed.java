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
        private string description;

        @Element
        private Item item;

        public string getDescription() {
            return description;
        }

        public Item getItem() {
            return item;
        }
    }

    public class Item{
        @Element
        private string title;

        @Element
        private string link;

        @Element
        private string description;

        @Element
        private string pubDate;

        @Element
        private Guid guid;

        public string getTitle() {
            return title;
        }

        public string getLink() {
            return link;
        }

        public string getDescription() {
            return description;
        }

        public string getPubDate() {
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
        private string value;

        public boolean isPermaLink() {
            return isPermaLink;
        }

        public string getValue() {
            return value;
        }
    }
}
