package com.guanshiyun.print.runner;

import com.guanshiyun.print.start.SystemStartPrintln;
import org.springframework.boot.CommandLineRunner;

public class AppStartRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        SystemStartPrintln.myPrintln();
    }
}
