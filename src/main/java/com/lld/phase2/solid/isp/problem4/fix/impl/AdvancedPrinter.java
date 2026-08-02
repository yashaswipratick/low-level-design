package com.lld.phase2.solid.isp.problem4.fix.impl;


import com.lld.phase2.solid.isp.problem4.fix.Fax;
import com.lld.phase2.solid.isp.problem4.fix.Printer;
import com.lld.phase2.solid.isp.problem4.fix.Scanner;
import com.lld.phase2.solid.stubs.Document;

public class AdvancedPrinter implements Printer, Scanner, Fax {


    @Override
    public void print(Document doc) {
        System.out.println("Print completed....");
    }

    @Override
    public void fax(Document doc) {
        System.out.println("Fax completed....");
    }

    @Override
    public void scan(Document doc) {
        System.out.println("Scan completed....");
    }
}
