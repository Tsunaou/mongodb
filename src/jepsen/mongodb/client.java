package jepsen.mongodb;

import site.ycsb.generator.*;

public class client {

    public static long nextKeyNum(NumberGenerator keychooser,AcknowledgedCounterGenerator transactioninsertkeysequence){
        long keynum;
        if (keychooser instanceof ExponentialGenerator) {
            do {
                keynum = transactioninsertkeysequence.lastValue() - keychooser.nextValue().intValue();
            } while (keynum < 0);
        } else {
            do {
                keynum = keychooser.nextValue().intValue();
            } while (keynum > transactioninsertkeysequence.lastValue());
        }
        return keynum;
    }

    public static void main(String[] args) {

        // From workloada
        int recordcount = 1000;
        int operationcount = 1000;
        boolean readallfields = true;
        double readproportion = 0.5;
        double writeproportion = 0.5;

        // Default values
        long insertstart = 0;
        long insertcount = 0;

        // From CoreWorkload.java
        int opcount = operationcount;
        int expectednewkeys = (int) ((opcount) * writeproportion * 2.0); // 2 is fudge factor

        DiscreteGenerator operationchooser;
        NumberGenerator keychooser;
        AcknowledgedCounterGenerator transactioninsertkeysequence;

        operationchooser = new DiscreteGenerator();
        keychooser = new ScrambledZipfianGenerator(insertstart, insertstart + insertcount + expectednewkeys);
        transactioninsertkeysequence = new AcknowledgedCounterGenerator(recordcount);

        operationchooser.addValue(readproportion, "READ");
        operationchooser.addValue(writeproportion, "WRITE");

        int read_cnt = 0;
        int write_cnt = 0;
        for(int i=0;i<200000;i++){
            String operation = operationchooser.nextString();
            System.out.println("Operation is " + operation);
            if(operation == null){
                System.err.println("Operation is null");
                System.exit(-1);
            }
            long keynum;
            switch (operation) {
                case "READ":
                    read_cnt++;
                    keynum = client.nextKeyNum(keychooser,transactioninsertkeysequence);
                    System.out.println("keynum is " + keynum);
                    break;
                case "WRITE":
                    write_cnt++;
                    keynum = client.nextKeyNum(keychooser,transactioninsertkeysequence);
                    System.out.println("keynum is " + keynum);
                    break;
                default:
                    System.err.println("Operation is invalid");
            }
        }
        System.out.println("Read Counts is " + read_cnt);
        System.out.println("Write Counts is " + write_cnt);
    }
}
