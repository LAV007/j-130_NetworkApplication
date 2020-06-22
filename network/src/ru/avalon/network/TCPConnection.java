package ru.avalon.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

//Основной класс для сетевого соединения
public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener; //слушатель событий
    private final BufferedReader in;
    private final BufferedWriter out;

    //Конструктор расчитан на то, что socket буден создаваться внутри
    public  TCPConnection (TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port)); //вызов из отдого конструктора другого конструктора
    }

    //Перегруженный конструктор расчитан на то, что уже кто-то снаружи сделает соединение (кто-то снаружи создаст socket) ---> используем готовый socket
    //Кто создает соединение заботится о том, чтобы передыть экземпляр этого соединеничя
    public  TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        String message = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, message);
                    }
                } catch (IOException exception) {
                    eventListener.onException(TCPConnection.this, exception);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    /**
     * Метод для отправки сообщения
     * @param value
     */
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n"); //символ конца читаемой строки (признак того, что читаемая строка закончилась)
            out.flush(); //сбрасывает буферы и отправляет
        } catch (IOException exception) {
            eventListener.onException(TCPConnection.this, exception);
            disconnect();
        }
    }

    /**
     * Метод для прерывания соединения
     */
    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException exception) {
            eventListener.onException(TCPConnection.this, exception);
        }
    }

    /**
     * Смотреть кто подключился, кто отключился
     * @return
     */
    @Override //реализация полиморфизма (переопределенный метод)
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress()/*Адрес, с которого установлено соединение*/ + ": " + socket.getPort() /*Номер порта*/;
    }
}