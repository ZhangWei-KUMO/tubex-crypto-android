package com.example.tubex.ui.home;

import java.util.List;

public class BinanceResponse {
    private String code;
    private String message;
    private String messageDetail;
    private List<TopSearchItem> data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail;
    }

    public List<TopSearchItem> getData() {
        return data;
    }

    public void setData(List<TopSearchItem> data) {
        this.data = data;
    }
}