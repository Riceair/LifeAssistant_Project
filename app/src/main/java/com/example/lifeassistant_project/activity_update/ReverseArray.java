package com.example.lifeassistant_project.activity_update;

import java.nio.ByteBuffer;

public class ReverseArray {
    static public byte[] Reverse_ByteBuffer(byte[] b)
    {
        int j = b.length - 1;
        ByteBuffer new_b = ByteBuffer.allocate(b.length);
        for(int i = 0;i < b.length; i++)
        {
            new_b.put(b[j]);
            j--;
        }
        return new_b.array();
    }
}

