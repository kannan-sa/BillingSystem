package com.kumarangarden.billingsystem.m_Model;

/**
 * Created by 11000257 on 8/22/2017.
 */

public class Leave {
    public float days;
    public String reason;
    private String key;

    public String GetKey() {
        return key;
    }

    public void SetKey(String key) {
        this.key = key;
    }
}
