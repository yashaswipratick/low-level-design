package com.lld.phase2.solid.isp.problem4.fix.impl;


import com.lld.phase2.solid.isp.problem4.fix.Printer;
import com.lld.phase2.solid.stubs.Document;

public class BasicPrinter implements Printer {


    @Override
    public void print(Document doc) {
        System.out.println("Print completed....");
    }
}
