package com.lhy.anoTest.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class reactServer implements Runnable{

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public reactServer(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        SelectionKey register = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        register.attach(new acceptHandler());
    }
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                selectedKeys.iterator().forEachRemaining(selectedKey -> {
                    dispatch(selectedKey);
                });
                selectedKeys.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void dispatch(SelectionKey key) {
        Runnable attachment = (Runnable) key.attachment();
        attachment.run();
    }
    private class acceptHandler implements Runnable{
        @Override
        public void run() {
            try {
                new dataHnadler(serverSocketChannel.accept(),selector);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class dataHnadler implements Runnable{
        private final SocketChannel socketChannel;
        private final Selector selector;
        private final ByteBuffer buffer;

        public dataHnadler(SocketChannel sk, Selector selector) throws IOException {
            this.socketChannel = sk;
            this.selector = selector;
            this.buffer = ByteBuffer.allocate(1024);
            socketChannel.configureBlocking(false);
            SelectionKey register = socketChannel.register(selector, 0);
            register.attach(this);
            register.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);


        }


        @Override
        public void run() {
            int len;
            while (true){
                try {
                    if (!((len=socketChannel.read(buffer))>0)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.printf(new String(buffer.array(),0,len));
                System.out.println("2333");

                buffer.flip();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        reactServer reactServer = new reactServer(9999);
        reactServer.run();
    }


}
