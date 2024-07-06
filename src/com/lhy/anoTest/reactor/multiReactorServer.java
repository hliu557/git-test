package com.lhy.anoTest.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class multiReactorServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector1,selector2;
    private SubReactor[] subReactors;


    multiReactorServer(int port) throws IOException {
        this.selector1 = Selector.open();
        this.selector2 = Selector.open();
        this.serverSocketChannel = serverSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        SelectionKey register = serverSocketChannel.register(selector1, SelectionKey.OP_ACCEPT);
        register.attach(new acceptHandler(selector1,serverSocketChannel));
        this.subReactors = new SubReactor[]{ new SubReactor(selector1),new SubReactor(selector2)};
    }
    public void startService(){
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }


    public static void main(String[] args) throws IOException {
        multiReactorServer ms = new multiReactorServer(9999);
        ms.startService();
    }

    class SubReactor implements Runnable{
        private Selector selector;
        SubReactor(Selector sl){
            this.selector = sl;
        }


        @Override
        public void run() {
            while(!Thread.interrupted()){
                try {
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    selectionKeys.iterator().forEachRemaining(selectedKey -> {
                        dispatch(selectedKey);
                    });
                    selectionKeys.clear();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        private void dispatch(SelectionKey key){
            Runnable attachment = (Runnable) key.attachment();
            attachment.run();
        }
    }
    class acceptHandler implements Runnable{
        private Selector selector;
        private ServerSocketChannel serverSocketChannel;
        acceptHandler(Selector selector, ServerSocketChannel serverSocketChannel){
            this.selector = selector;
            this.serverSocketChannel = serverSocketChannel;
        }
        @Override
        public void run() {
            try {
                SocketChannel accept = serverSocketChannel.accept();
                System.out.println(123);
                new openHandler(selector2,accept);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class openHandler implements Runnable{
        private final Selector selector;
        private final SocketChannel socketChannel;
        private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        openHandler(Selector selector, SocketChannel sk) throws IOException {
            this.selector = selector;
            this.socketChannel = sk;
            socketChannel.configureBlocking(false);
            SelectionKey register = socketChannel.register(selector, 0);
            register.attach(this);
            register.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            System.out.println(2333);
            selector.wakeup();


        }

        @Override
        public void run() {
            System.out.println(888);
            int length;
            while(true){
                try {
                    if (!((length=socketChannel.read(byteBuffer))>0)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(new String(byteBuffer.array(),0,10)+123333);
                byteBuffer.flip();
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }

}
