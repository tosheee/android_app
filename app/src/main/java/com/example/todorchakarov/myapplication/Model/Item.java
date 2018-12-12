package com.example.todorchakarov.myapplication.Model;

public class Item {
    private String text, subText;
    private boolean expandable;

    public Item(){ }

    public Item(String text, String subText, boolean expandable){
        this.text = text;
        this.subText = subText;
        this.expandable = expandable;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}
