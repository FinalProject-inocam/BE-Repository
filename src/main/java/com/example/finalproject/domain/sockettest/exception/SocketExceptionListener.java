package com.example.finalproject.domain.sockettest.exception;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocketExceptionListener implements ExceptionListener {

    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        runExceptionHandling(e, client);
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        runExceptionHandling(e, client);
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        runExceptionHandling(e, client);
        client.disconnect();
    }

    @Override
    public void onPingException(Exception e, SocketIOClient client) {
        runExceptionHandling(e, client);
    }

    @Override
    public void onPongException(Exception e, SocketIOClient client) {
        runExceptionHandling(e, client);
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        return false;
    }

    private void runExceptionHandling(Exception e, SocketIOClient client) {
        final String message;

        if (e instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) e;

            message = e.getMessage();
        } else if(e.getCause() instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException)e.getCause();

            message = e.getMessage();
        } else {
            e.printStackTrace();
            message = e.getMessage();
        }

        client.sendEvent("error", message);

    }

}