package com.utiltest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanghz.myutil.json.deserializer.JsonToStringDeserializer;

/**
 * @author whz
 * @date 2022/6/21 13:11
 */
public class Abc {
    String a;
    @JsonDeserialize(using = JsonToStringDeserializer.class)
    String b;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "Abc{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                '}';
    }
}
