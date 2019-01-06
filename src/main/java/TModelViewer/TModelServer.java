package TModelViewer;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/TModel")
public class TModelServer {
    private Session session = null;//初始化集合，用来存放每个客户端对应的服务器端的websocket对象
    private static CopyOnWriteArraySet<TModelServer> servers = new CopyOnWriteArraySet<>();
    //

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        servers.add(this);
        System.out.println("连接已经建立,sessionID:" + session.getId());
    }


    @OnMessage
    public void onMessage(String message) {

        System.out.println("3D model URL:" + message);
        try {
            //向客户端发送消息
            for (TModelServer server : servers) {
                server.session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        servers.remove(this);
        System.out.println("连接已经关闭");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }
}
