package com.feup.sdis.chord;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

import com.feup.sdis.messages.requests.chord.ClosestPreceedingRequest;
import com.feup.sdis.messages.requests.chord.FindSuccessorRequest;
import com.feup.sdis.messages.requests.chord.GetPredecessorRequest;
import com.feup.sdis.messages.requests.chord.NotifyRequest;
import com.feup.sdis.messages.responses.chord.ClosestPreceedingResponse;
import com.feup.sdis.messages.responses.chord.FindSuccessorResponse;
import com.feup.sdis.messages.responses.chord.GetPredecessorResponse;
import com.feup.sdis.peer.MessageListener;

/**
 * CHORD TODO
 * 
 * - Add list of r sucessors as described in the paper 
 * - Move updating threads to a scheduled thread pool
 * - Handle peer failing - Handle peer shutdown
 * - See concurrency of chord (maybe we need some syncrhonized methods)
 * - Implemente the "predecessor failure-checking" of the protocol 
 * - WARNING: Chord table sizes and key sizes are receiving transformations to allow testing for smaller tables, this may caused
 * unexpected behaviour.
 */

public class Chord {

    // Constants
    private final boolean DEBUG_MODE = true;
    private final int FINGER_TABLE_SIZE = 8;
    private final int FIX_FINGERS_INTERVAL_MS = 250;
    private final int STABILIZE_INTERVAL_MS = 250;


    public static Chord chordInstance;
    private SocketAddress[] fingerTable;
    private UUID[] stepValues;
    private SocketAddress self = null;
    private SocketAddress predecessor = null;
    private int next = 1;

    // create
    public Chord(SocketAddress self) {
        this.fingerTable = new SocketAddress[FINGER_TABLE_SIZE];
        this.stepValues = new UUID[FINGER_TABLE_SIZE];

        // TODO: Remove this, only used to allow for smaller table sizes for testing
        self.setPeerID(Chord.normalizeToSize(self.getPeerID(), FINGER_TABLE_SIZE));
        this.self = self;
        Arrays.fill(this.fingerTable,this.self);
        this.initKeyLookupSteps();
    }

    // join
    public Chord(SocketAddress self, SocketAddress node) {
        this(self);

        // TODO: Remove this, only used to allow for smaller table sizes for testing
        node.setPeerID(Chord.normalizeToSize(node.getPeerID(), FINGER_TABLE_SIZE));

        ClosestPreceedingResponse res = MessageListener.sendMessage(new ClosestPreceedingRequest(self), node);
        this.setSucessor(res.getAddress());
    }

    public SocketAddress getSucessor(){

        return this.fingerTable[0];
    }

    public SocketAddress setSucessor(SocketAddress newSucessor){

        return this.fingerTable[0] = newSucessor;
    }

    public synchronized SocketAddress closestPreceedingNode(UUID key) {

        for (int i = fingerTable.length - 1; i >= 0; i--) {

            if (this.fingerTable[i] == null)
                continue;

            if (this.betweenTwoKeys(self.getPeerID(), key, this.fingerTable[i].getPeerID(), false, false))
                return this.fingerTable[i];
        }

        return self;
    }

    public SocketAddress findSuccessor(UUID key) {

        if (this.betweenTwoKeys(this.self.getPeerID(), this.getSucessor().getPeerID(), key, false, true)) {

            return this.getSucessor();
        } else {
            
            SocketAddress cpn = this.closestPreceedingNode(key);
            FindSuccessorResponse res = MessageListener.sendMessage(new FindSuccessorRequest(key), cpn);
            return res.getAddress();
        }

    }

    public void stabilize() {

        if(DEBUG_MODE)
            System.out.println("> CHORD: Stabilizing table");

        SocketAddress successorsPerceivedPredecessorAddr = null;

        if (this.getSucessor() != this.self) {

            // Find the perceived predecessor of this node's sucessor
            GetPredecessorResponse successorsPerceivedPredecessor = MessageListener
                    .sendMessage(new GetPredecessorRequest(), this.getSucessor());

            successorsPerceivedPredecessorAddr = successorsPerceivedPredecessor.getAddress();

            // Sucessor has no predecessor, notify him
            if (successorsPerceivedPredecessorAddr == null) {
                MessageListener.sendMessage(new NotifyRequest(self), this.getSucessor());
                return;
            }
        }
        else{
            // Successor is self
            successorsPerceivedPredecessorAddr = this.predecessor;

            if (successorsPerceivedPredecessorAddr == null) 
                return;
        }

        UUID successorsPerceivedPredecessorID = successorsPerceivedPredecessorAddr.getPeerID();

        // Update the sucessor
        if (this.betweenTwoKeys(this.self.getPeerID(), this.getSucessor().getPeerID(), successorsPerceivedPredecessorID,
                false, false)) {

            this.setSucessor(successorsPerceivedPredecessorAddr);
            
            if(DEBUG_MODE)
                System.out.println("> CHORD: Sucessor updated to " + successorsPerceivedPredecessorAddr);
        }

        MessageListener.sendMessage(new NotifyRequest(self), this.getSucessor());
    }

    public boolean notify(SocketAddress newPred) {

        UUID candidateID = newPred.getPeerID();

        if (this.predecessor == null || this.betweenTwoKeys(this.predecessor.getPeerID(), this.self.getPeerID(),
                candidateID, false, false)) {

            if(DEBUG_MODE)
                System.out.println("> CHORD: Predecessor updated to "+ newPred );
            this.predecessor = newPred;

            return true;
        }

        return false;
    }

    public void fixFingers() {

        UUID neededID = this.stepValues[next];
        SocketAddress newFinger = this.findSuccessor(neededID);

        if (this.fingerTable[next].equals(newFinger)){

            next = (next + 1) % FINGER_TABLE_SIZE;
            return;
        }
        if(DEBUG_MODE)
            System.out.println("> CHORD: Fixing fingers was "+ this.fingerTable[next] + " (next = " + next + ")");

        synchronized (this.fingerTable) {

            this.fingerTable[next] = newFinger;
        }
        if(DEBUG_MODE)
            System.out.println("> CHORD: Added " + newFinger);

        next = (next + 1) % FINGER_TABLE_SIZE;

        
    }

    public SocketAddress getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(SocketAddress predecessor) {
        this.predecessor = predecessor;
    }

    public void checkPredecessor() {

        return;
    }

    private void initKeyLookupSteps() {

        BigInteger maxV = new BigInteger(String.valueOf(2));
        maxV = maxV.pow(this.FINGER_TABLE_SIZE);
        for (int i = 0; i < this.FINGER_TABLE_SIZE; i++) {
            BigInteger nextVal = Chord.convertToBigInteger(this.self.getPeerID());
            int step = (int) Math.pow(2, i);
            nextVal = nextVal.add(new BigInteger(String.valueOf(step + "")));
            
            nextVal = nextVal.mod(maxV);
            UUID neededID = Chord.convertFromBigInteger(nextVal);

            this.stepValues[i] = neededID;
        }
    }

    /*
     * HELPER FUNCTIONS
     */

    public boolean betweenTwoKeys(UUID a, UUID b, UUID c, boolean closedLeft, boolean closedRight) {

        if( (closedLeft && c.equals(a)) || (closedRight && c.equals(b)))
            return true;

        if( (!closedLeft && c.equals(a)) || (!closedRight && c.equals(b)))
            return false;

        // Whole circle is valid
        if(b.equals(a) && !c.equals(a))
            return true;

        return (a.compareTo(b) < 0) ? (a.compareTo(c) < 0) && (c.compareTo(b) < 0) : !((b.compareTo(c) < 0 ) && (c.compareTo(a) < 0 ));
    }

    public String state() {

        String message = "Chord state: \n";
        message += "  - Self" + "\n";
        message += "  > " + self + "\n";
        message += "  - Predecessor" + "\n";
        message += "  > " + predecessor + "\n";
        message += "  - Successor" + "\n";
        message += "  > " + this.getSucessor() + "\n";
        message += "  - Finger table" + "\n";
        for (int i = 0; i < this.fingerTable.length; i++) {
            message += "  > entry #" + i + " - " + this.fingerTable[i] + "\n";
        }

        return message;
    }

    // TODO : These threads should be handled by a scheduled executor to avoid sleeps
    public void initThreads() {

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        Chord.chordInstance.stabilize();
                        Thread.sleep(STABILIZE_INTERVAL_MS);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        Chord.chordInstance.stabilize();
                        Thread.sleep(FIX_FINGERS_INTERVAL_MS);
                        Chord.chordInstance.fixFingers();
                        Thread.sleep(FIX_FINGERS_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        });

        // t1.start();
        t2.start();
    }

    // TODO: Move this from here

    public static final BigInteger B = BigInteger.ONE.shiftLeft(64); // 2^64
    public static final BigInteger L = BigInteger.valueOf(Long.MAX_VALUE);

    public static BigInteger convertToBigInteger(UUID id) {
        BigInteger lo = BigInteger.valueOf(id.getLeastSignificantBits());
        BigInteger hi = BigInteger.valueOf(id.getMostSignificantBits());

        // If any of lo/hi parts is negative interpret as unsigned

        if (hi.signum() < 0)
            hi = hi.add(B);

        if (lo.signum() < 0)
            lo = lo.add(B);

        return lo.add(hi.multiply(B));
    }

    public static UUID convertFromBigInteger(BigInteger x) {
        BigInteger[] parts = x.divideAndRemainder(B);
        BigInteger hi = parts[0];
        BigInteger lo = parts[1];

        if (L.compareTo(lo) < 0)
            lo = lo.subtract(B);

        if (L.compareTo(hi) < 0)
            hi = hi.subtract(B);

        return new UUID(hi.longValueExact(), lo.longValueExact());
    }

    public static UUID normalizeToSize(UUID id, int bits){
        BigInteger maxV = new BigInteger(String.valueOf(2));
        maxV = maxV.pow(bits);
        BigInteger val = Chord.convertToBigInteger(id);
        val = val.mod(maxV);
        UUID neededID = Chord.convertFromBigInteger(val);
        
        return neededID;
        
    }
}