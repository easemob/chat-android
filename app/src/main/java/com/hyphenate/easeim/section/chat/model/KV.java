package com.hyphenate.easeim.section.chat.model;

public class KV<K, V>{
        private K first;
        private V second;

        public KV(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() {
            return first;
        }

        public void setFirst(K first) {
            this.first = first;
        }

        public V getSecond() {
            return second;
        }

        public void setSecond(V second) {
            this.second = second;
        }
    }