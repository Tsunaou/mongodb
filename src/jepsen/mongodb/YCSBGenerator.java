package jepsen.mongodb;

import site.ycsb.WorkloadException;
import site.ycsb.generator.*;

public class YCSBGenerator {

    final String read = "READ";
    final String write = "WRITE";

    private int opCount; // default insertCount
    private double readProportion;
    private double writeProportion;

    private int realReadCount;

    private int realWriteCount;

    private String requestDistrib;
    private String fieldLengthDistribution;

    private int uniformMax;

    private boolean readAllFileds;
    private boolean writeAllFileds;
    private boolean dataIntegrity;

    private DiscreteGenerator operationchooser;
    private NumberGenerator keychooser;
    private AcknowledgedCounterGenerator transactioninsertkeysequence;

    int[] keyCounter;
    int[] readKeyCounter;
    int[] writeKeyCounter;

    public YCSBGenerator(int opCount, double readProportion, double writeProportion, String requestDistrib, int uniformMax) throws WorkloadException {
        this.opCount = opCount;
        this.readProportion = readProportion;
        this.writeProportion = writeProportion;
        this.requestDistrib = requestDistrib;
        this.uniformMax = uniformMax;
        this.init();
    }

    public YCSBGenerator(int opCount, double readProportion, double writeProportion, String requestDistrib) throws WorkloadException {
        this.opCount = opCount;
        this.readProportion = readProportion;
        this.writeProportion = writeProportion;
        this.requestDistrib = requestDistrib;
        this.init();
    }

    private void createOperationGenerator() {
        operationchooser = new DiscreteGenerator();
        operationchooser.addValue(readProportion, read);
        operationchooser.addValue(writeProportion, write);
    }

    private void createKeyChooser() throws WorkloadException {
        if (requestDistrib.compareTo("uniform") == 0) {
            // 均匀分布
            System.out.println("Uniform, max = " + uniformMax);
            keychooser = new UniformLongGenerator(0, uniformMax - 1);
        } else if (requestDistrib.compareTo("exponential") == 0) {
            double percentile = Double.parseDouble(ExponentialGenerator.EXPONENTIAL_PERCENTILE_DEFAULT);
            double frac = Double.parseDouble(ExponentialGenerator.EXPONENTIAL_FRAC_DEFAULT);
            keychooser = new ExponentialGenerator(percentile, opCount * frac);
        } else if (requestDistrib.compareTo("sequential") == 0) {
            keychooser = new SequentialGenerator(0, opCount - 1);
        } else if (requestDistrib.compareTo("zipfian") == 0) {
            // it does this by generating a random "next key" in part by taking the modulus over the
            // number of keys.
            // If the number of keys changes, this would shift the modulus, and we don't want that to
            // change which keys are popular so we'll actually construct the scrambled zipfian generator
            // with a keyspace that is larger than exists at the beginning of the test. that is, we'll predict
            // the number of inserts, and tell the scrambled zipfian generator the number of existing keys
            // plus the number of predicted keys as the total keyspace. then, if the generator picks a key
            // that hasn't been inserted yet, will just ignore it and pick another key. this way, the size of
            // the keyspa(atom (vec (range 2000)))ce doesn't change from the perspective of the scrambled zipfian generator
            final double insertproportion = 0.0;
            int opcount = opCount;
            int expectednewkeys = (int) ((opcount) * insertproportion * 2.0); // 2 is fudge factor
            keychooser = new ScrambledZipfianGenerator(0, opcount + expectednewkeys);
        } else if (requestDistrib.compareTo("latest") == 0) {
            keychooser = new SkewedLatestGenerator(transactioninsertkeysequence);
        } else if (requestDistrib.equals("hotspot")) {
            double hotsetfraction = 0.2;
            double hotopnfraction = 0.8;
            keychooser = new HotspotIntegerGenerator(0, opCount - 1,
                    hotsetfraction, hotopnfraction);
        } else {
            throw new WorkloadException("Unknown request distribution \"" + requestDistrib + "\"");
        }
    }

    private void init() throws WorkloadException {
        transactioninsertkeysequence = new AcknowledgedCounterGenerator(opCount);
        createOperationGenerator();
        createKeyChooser();
        realReadCount = 0;
        realWriteCount = 0;

        keyCounter = new int[uniformMax];
        readKeyCounter = new int[uniformMax];
        writeKeyCounter = new int[uniformMax];


    }

    public YCSBKeyValue nextOperation(){
        String operation = operationchooser.nextString();
//        System.out.println("Operation is " + operation);
        if(operation == null){
            System.err.println("Operation is null");
            System.exit(-1);
        }
        Integer key = null;
        Integer value = null;
        switch (operation) {
            case "READ":
                realReadCount++;
                key = (int) client.nextKeyNum(keychooser,transactioninsertkeysequence);
                keyCounter[key]++;
                readKeyCounter[key]++;
//                System.out.println("keynum is " + keynum);
                break;
            case "WRITE":
                realWriteCount++;
                key = (int) client.nextKeyNum(keychooser,transactioninsertkeysequence);
                keyCounter[(int) key]++;
                writeKeyCounter[key]++;
                value = writeKeyCounter[key];
//                System.out.println("keynum is " + keynum + ", value is " + writeKeyCounter[(int) keynum]);
                break;
            default:
                System.err.println("Operation is invalid");
        }
        return new YCSBKeyValue(key, value);
    }

    void printStatus(){
        System.out.println("Read Counts is " + realReadCount);
        System.out.println("Write Counts is " + realWriteCount);
        for(int i=0;i<uniformMax;i++){
            System.out.println("i="+i+", key counter is " + keyCounter[i]);
            System.out.println("i="+i+", write key counter is " + writeKeyCounter[i]);
            System.out.println("i="+i+", read key counter is " + readKeyCounter[i]);
        }
    }



    public static void main(String[] args) throws WorkloadException {
        int opCount = 10000;
        double readProportion = 0.5;
        double writeProportion = 0.5;
        String requestDistrib = "uniform";

        YCSBGenerator generator = new YCSBGenerator(opCount, readProportion, writeProportion, requestDistrib, 100);

        for(int i=0;i<opCount;i++){
            YCSBKeyValue kv = generator.nextOperation();
        }

        generator.printStatus();

    }


}
