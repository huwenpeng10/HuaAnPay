package com.example.mrxu.mutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by MrXu on 2017/5/14.
 */

public class TcpRequest {

    private Socket socket;
    private InetSocketAddress inetAddress;
    private EventBus eventBus;
    private CheckResponseThread checkResponseThread;

    public TcpRequest(String host, int port) {
        socket = new Socket();
        inetAddress = new InetSocketAddress(host, port);
        eventBus = EventBus.getDefault();
    }
    /**
     * <li>请求数据</li>
     *
     * @param message
     */
    public void request(String message) {
         checkResponseThread = new CheckResponseThread(
                message);
        checkResponseThread.start();
    }


    private class CheckResponseThread extends Thread {
        private String message;
        public CheckResponseThread(String message) {
            this.message = message;
        }
        @Override
        public void run() {

            super.run();
            TcpRequestMessage tcpRequestMessage = new TcpRequestMessage();
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                socket.setSoTimeout(10000);
                socket.connect(inetAddress);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                outputStream.flush();

                tcpRequestMessage.setTcpRequestMessage(read(inputStream));
                tcpRequestMessage.setCode(1);
                final HashMap<String, String> data = new XmlParser()
                        .parse(new String(tcpRequestMessage.getTcpRequestMessage()));

                //通过TxCode判断是哪种接口
                tcpRequestMessage.setTextCode(data.get("TxCode"));

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                tcpRequestMessage.setCode(-2);
            } catch (InterruptedException e) {
                e.printStackTrace();
                tcpRequestMessage.setCode(-1);
            } catch (Exception e) {
                e.printStackTrace();
                tcpRequestMessage.setCode(-1);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                if (eventBus.getClass() != NoSubscriberEvent.class &&
//                        eventBus.getClass() != SubscriberExceptionEvent.class) {
                    eventBus.post(tcpRequestMessage);
//                }


            }
        }


        /**
         *
         * @param inputStream
         * @return
         * @throws IOException
         * @throws InterruptedException
         */
        // 判断流是否是空,取第一位是否是空

        private byte[] read(InputStream inputStream) throws IOException,
                InterruptedException {
            boolean off = true;
            byte[] b = new byte[1];
            byte[] b1 = new byte[1024 * 3 - 1];
            byte[] b2 = new byte[1024 * 3];
            while (off) {
                Thread.sleep(500);
                if (inputStream.read(b) != -1) {
                    inputStream.read(b1);
                    System.arraycopy(b, 0, b2, 0, b.length);
                    System.arraycopy(b1, 0, b2, b.length, b1.length);
                    off = false;
                }
            }
            return b2;
        }
    }

    public class TcpRequestMessage {
        private byte[] tcpRequestMessage;
        private int code;
        //
        private String textCode;


        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public void setTextCode(String textCode) {
            this.textCode = textCode;
        }
        public String getTextCode() {
            return textCode;
        }
        public byte[] getTcpRequestMessage() {

            return new String(tcpRequestMessage).trim().getBytes();
        }
        public void setTcpRequestMessage(byte[] tcpRequestMessage) {
            this.tcpRequestMessage = tcpRequestMessage;
        }
    }
}
