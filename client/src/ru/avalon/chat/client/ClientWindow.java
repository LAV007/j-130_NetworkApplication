package ru.avalon.chat.client;

import ru.avalon.network.TCPConnection;
import ru.avalon.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "46.175.213.227";
    private static final int PORT = 160;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField nicknameTF = new JTextField("Alex");
    private final JTextField inputTF = new JTextField();

    private TCPConnection connection;

    private ClientWindow(){
        super("j-130_NetworkApplication");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); //окно всегда посередине экрана
        setAlwaysOnTop(true); //показывать главный фрэйм поверх остальных окон

        log.setEditable(false); //запрет редактирования текстового поля
        log.setLineWrap(true); //автоматический перенос слов

        inputTF.addActionListener(this);

        add(log, BorderLayout.CENTER); //добавляем текстовое поля на главный фрэйм
        add(inputTF, BorderLayout.SOUTH); //добавляем поле ввода на главный фрэйм и размещаем его внизу окна
        add(nicknameTF, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException exception) {
            printMessage("Connection exception: " + exception);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = inputTF.getText();
        if (message.equals("")) return;
        inputTF.setText(null);
        connection.sendString(nicknameTF.getText() + ": " + message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMessage("Connection exception: " + exception);
    }

    private synchronized void printMessage(String message){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength()); //автоматическое поднятие текста
            }
        });
    }
}