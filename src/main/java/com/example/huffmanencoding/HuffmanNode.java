package com.example.huffmanencoding;

public record HuffmanNode(
        Integer data,
        String code,
        HuffmanNode leftChild,
        HuffmanNode rightChild
) implements Comparable<HuffmanNode> {

    public boolean isLeaf() {
        return (leftChild() == null) && (rightChild() == null);
    }

    public HuffmanNode traverse(char direction) {
        if (direction == '0')
            return leftChild;
        else
            return rightChild;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return code.compareTo(o.code());
    }

    public HuffmanNode getLeftChild(){
        return leftChild;
    }

    public HuffmanNode getRightChild(){
        return rightChild;
    }

    public int getData(){
        return data;
    }

    public String getCode() {
        return code;
    }

}

