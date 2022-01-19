package net.lindseybot.shared.utils;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 * <p>
 * Modified from https://github.com/callicoder/java-snowflake
 * <p>
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of Snowflake per node in your distributed system cluster.
 */
public class Snowflake {

    // (2^42)-1 = 4398046511103 + EPOCH = Will die on Thu May 15 2160 07:35:11Z
    private static final int EPOCH_BITS = 42;
    // Max 1024 Nodes
    private static final int NODE_ID_BITS = 10;
    // Max 4096 Increments per Millis
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch (January 1, 2021 Midnight UTC = 2021-01-01T00:00:00Z)
    private static final long DEFAULT_CUSTOM_EPOCH = 1609459200000L;

    private final long nodeId;
    private final long customEpoch;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Let Snowflake generate a nodeId
    public Snowflake() {
        this.nodeId = createNodeId();
        this.customEpoch = DEFAULT_CUSTOM_EPOCH;
    }

    public synchronized long next() {
        long currentTimestamp = timestamp();
        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }
        lastTimestamp = currentTimestamp;
        return currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private long timestamp() {
        return Instant.now().toEpochMilli() - customEpoch;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte macPort : mac) {
                        sb.append(String.format("%02X", macPort));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }

    public long[] parse(long id) {
        long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
        long maskSequence = (1L << SEQUENCE_BITS) - 1;

        long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS)) + customEpoch;
        long nodeId = (id & maskNodeId) >> SEQUENCE_BITS;
        long sequence = id & maskSequence;

        return new long[]{timestamp, nodeId, sequence};
    }

    @Override
    public String toString() {
        return "Snowflake Settings [EPOCH_BITS=" + EPOCH_BITS + ", NODE_ID_BITS=" + NODE_ID_BITS
                + ", SEQUENCE_BITS=" + SEQUENCE_BITS + ", CUSTOM_EPOCH=" + customEpoch
                + ", NodeId=" + nodeId + "]";
    }

}
