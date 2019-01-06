package ChatRoom;

import
        Link.LinkDB;
import com.mongodb.client.MongoCollection;
import net.sf.json.JSONObject;
import org.bson.Document;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/Chat") //类层次的注解，功能主要是将目前的类定义成一个WebSocket服务器端。注解的值是WebSocket的名字
public class ChatServer{

    private Session session=null;
    //初始化集合，用来存放每个客户端对应的服务器端的WebSocket对象
    private static CopyOnWriteArraySet<ChatServer> servers=new CopyOnWriteArraySet<>();
    //定义了当一个新用户连接成功后所调用的方法
    @OnOpen
    public void onOpen(Session session)
    {
        this.session=session;
        servers.add(this);
        System.out.println("连接已经建立,sessionID:"+session.getId());

    }
    //接收消息后所调用的方法
    @OnMessage
    public void onMessage(String message)
    {
        System.out.println("客户端发送的数据:"+message);
        LinkDB linkDB = new LinkDB();

        JSONObject json = JSONObject.fromObject(message);
        String from = json.getString("from");
        String to = json.getString("to");
        String content = json.getString("input");
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        sdf.format(dt);

        try {
            MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","MessageRecord");//由库名+表名，得到要添加数据的表单
            Document doc = new Document()
                    .append("from", from)
                    .append("to", to)
                    .append("msg", content).append("createTime", sdf.format(dt))
                    .append("type", "message");
            col.insertOne(doc);  //写入数据库的对应表单

        } catch (Exception e) {
            System.out.println("message recoding fail!");
        }
        try
        {
            //向客户端发送消息
            for (ChatServer server:servers)
            {
                server.session.getBasicRemote().sendText(message);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    @OnClose
    public void onClose()
    {
        servers.remove(this);
        System.out.println("连接已经关闭");
    }
    @OnError
    public void onError(Session session,Throwable error)
    {
        System.out.println("发生错误");
        error.printStackTrace();
    }
}


