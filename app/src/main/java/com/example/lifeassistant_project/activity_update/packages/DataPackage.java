package com.example.lifeassistant_project.activity_update.packages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class DataPackage {
    protected int connectionTimeout = 5000;
    public ArrayList<DataPackage> sendOperation(String address, int port) throws IOException {return null;}

    protected ByteBuffer getInputByteBuffer(InputStream in, int allocateSize) throws IOException {
//        StringBuffer buf = new StringBuffer();        // 建立讀取字串。
        ByteBuffer b_buf = ByteBuffer.allocate(allocateSize);
        try {
            while (true) {            // 不斷讀取。
                int x = in.read();    // 讀取一個 byte。(read 傳回 -1 代表串流結束)
                if (x==-1) break;     // x = -1 代表串流結束，讀取完畢，用 break 跳開。
                byte b = (byte) x;    // 將 x 轉為 byte，放入變數 b.
                b_buf.put(b);
//                buf.append((char) b); // 假設傳送ASCII字元都是 ASCII。
            }
        } catch (Exception e) {
            in.close();               // 關閉輸入串流。
        }

        return b_buf;
    }
}
