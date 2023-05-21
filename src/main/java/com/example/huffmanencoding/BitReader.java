package com.example.huffmanencoding;
import java.io.FileInputStream;
import java.io.IOException;

public class BitReader {
    private final FileInputStream inputStream;
    private int lastByteCompletionCount = 0;
    private int bitPosition = 7;

    private int currentByte;

    public BitReader (FileInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public char readBit() throws IOException {
        if (bitPosition == 7)
            if ((currentByte = inputStream.read()) == -1)
                return '2';

        // if it is the last byte in the input file - compute the number of redundant bits
        if (inputStream.available() == 0) {
            for (int i = 0; i <= 7; i++) {
                if ((currentByte & (1 << bitPosition)) == 0)
                    lastByteCompletionCount++;
                else if ((currentByte & (1 << bitPosition)) != 0) {
                    lastByteCompletionCount++;
                    break;
                }
            }
        }

        if (bitPosition == lastByteCompletionCount && lastByteCompletionCount != 0)
            return '2';

        char read = (currentByte & (1 << bitPosition)) != 0 ? '1' : '0';

        bitPosition--;

        if (bitPosition < 0)
            bitPosition = 7;

        return read;
    }
}