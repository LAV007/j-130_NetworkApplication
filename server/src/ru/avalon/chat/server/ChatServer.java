package ru.avalon.chat.server;

import ru.avalon.network.TCPConnection;
import ru.avalon.network.TCPConnectionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    //список соединений
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    /**
     * Создаем ServerSocket, который слушает TCP-порт 8189
     */
    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){ //принимаем входящее соединение
                try {
                    new TCPConnection(this, serverSocket.accept()); //метод accept() ждет нового соединения. Как только соединение установленно этот метод возвращет объект Socket
                } catch (IOException ioException) {
                    System.out.println("TSPConnection exception: " + ioException);
                }
            }
        } catch (IndexOutOfBoundsException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     *
     * @param value
     */
    private void sendToAllClients(String value){
        System.out.println(value);
        final int count = connections.size();
        for (int i = 0; i < count; i++) connections.get(i).sendString(value);

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllClients("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllClients(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllClients("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception: " + exception);
    }
}