package com.codex.testing;

import com.codex.testing.interfaces.Ipay;

public class Pay implements Ipay {
    public String index(){
        return "paid";
    }
}
