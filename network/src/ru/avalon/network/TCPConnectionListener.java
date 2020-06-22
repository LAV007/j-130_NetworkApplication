package ru.avalon.network;

/**
 * Описали события, которые могуь возникнуть
 * Дополнительный уровень абстракции
 * Данный интерфейс позволяет использовать класс TCPConnection в разных частях программы
 * (подсовывать разные слушатели событий - TCPConnectionListener eventListener) ---> по-разному реагировать на события
 * аналогия с обработкой события при нажатии на кнопку
 */
public interface TCPConnectionListener {

    /**
     * Соединение запустили, можем с ним работать
     * @param tcpConnection
     */
    void onConnectionReady(TCPConnection tcpConnection);

    /**
     * Соединение приняло входящую строку
     * @param tcpConnection
     * @param value
     */
    void onReceiveString(TCPConnection tcpConnection, String value);

    /**
     * Прерывание сети
     * @param tcpConnection
     */
    void onDisconnect (TCPConnection tcpConnection);

    /**
     * Что-то пошло не так
     * @param tcpConnection
     * @param exception
     */
    void onException (TCPConnection tcpConnection, Exception exception);
}
