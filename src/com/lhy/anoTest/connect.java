package com.lhy.anoTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;

public class connect {
    public static void startClient() throws IOException {
        InetSocketAddress localhost = new InetSocketAddress("localhost", 9999);
        SocketChannel open = SocketChannel.open(localhost);
        open.configureBlocking(false);
        while(!open.finishConnect()){};
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("yigeyigeyige".getBytes(StandardCharsets.UTF_8));
        allocate.flip();
        open.write(allocate);
        open.shutdownOutput();
        open.close();
    }

    public static void main(String[] args) throws IOException {
        startClient();
    }
}
