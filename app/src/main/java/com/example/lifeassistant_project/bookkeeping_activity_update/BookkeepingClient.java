package com.example.lifeassistant_project.bookkeeping_activity_update;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BookkeepingClient implements Runnable {

        public static int port = 6666; // 設定傳送埠為 20。
        public static String address = "192.168.203.108";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run()  {
            try {
                Socket client = new Socket(address, port);     // 根據 args[0] 的 TCP Socket.
                OutputStream out = client.getOutputStream();

                // send package
                AccountPackage testAccount = new AccountPackage(258, 200, 20, 5, 16, "哈哈哈", "是我啦", true);
                out.write(PackageDecoder.accountPackageEncode(testAccount));
                //

                out.flush();
                InputStream in = client.getInputStream();      // 取得輸入訊息的串流
                BufferedReader s_in = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                StringBuffer buf = new StringBuffer();        // 建立讀取字串。
                ByteBuffer b_buf = ByteBuffer.allocate(1024);
                try {
                    while (true) {            // 不斷讀取。
                        int x = in.read();    // 讀取一個 byte。(read 傳回 -1 代表串流結束)
                        if (x == -1) break;    // x = -1 代表串流結束，讀取完畢，用 break 跳開。
                        byte b = (byte) x;    // 將 x 轉為 byte，放入變數 b.
                        b_buf.put(b);
                        buf.append((char) b);// 假設傳送ASCII字元都是 ASCII。
                    }
                } catch (Exception e) {
                    in.close();                // 關閉輸入串流。
                }
                out.close();

                // deal with first 3 char
                byte[] resultArray = Arrays.copyOfRange(b_buf.array(), 3, b_buf.array().length);
                byte[] typeArray = Arrays.copyOfRange(b_buf.array(), 0, 3);
                String testString = new String(typeArray, StandardCharsets.UTF_8);
                //

                AccountPackage rcvPkg = PackageDecoder.accountPackageDecode(resultArray);
                client.close();                                // 關閉 TcpSocket.
            }catch (Exception e){

            }
        }

}

