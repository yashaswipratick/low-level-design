package com.lld.phase2.solid.isp.problem4.without_fix;

import com.lld.phase2.solid.stubs.Document;

public class BasicPrinter implements Printer{


    @Override
    public void print(Document doc) {
        System.out.println("Print completed....");
    }

    @Override
    public void scan(Document doc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fax(Document doc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
