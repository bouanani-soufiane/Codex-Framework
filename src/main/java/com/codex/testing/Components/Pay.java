package com.codex.testing.Components;

import com.codex.testing.Components.interfaces.Ipay;

public class Pay implements Ipay {
    public String index(){
        return "paid";
    }
}
