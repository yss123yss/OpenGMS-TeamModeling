package MessageBoard;

import Link.LinkDB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="DeleteMessageServlet",urlPatterns = "/DeleteMessageServlet")
public class DeleteMessageServlet extends HttpServlet  {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置对客户端请求进行重新编码的编码
        req.setCharacterEncoding("utf-8");
        //使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
        resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        //指定对服务器响应进行重新编码的编码
        resp.setCharacterEncoding("UTF-8");
        //获取向前台写数据的输出流
        PrintWriter writer = resp.getWriter();

        LinkDB linkDB=new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","MessageBoard");//由库名+表名，得到要添加数据的表单
        String Type=req.getParameter("Type");
        String Value=req.getParameter("bMessageID");

        if(Type.equals("One")) {
            linkDB.deleteByField(col, "bMessageID", Value);//仅删除本条消息
        }
        else if(Type.equals("All")){
            DeleteAllMessage(col,Value);//删除消息及其衍生消息
        }
        else {
            System.out.println("删除异常，未设置删除方式！");
        }
    }

    //删除某消息衍生的所有消息
    private void DeleteAllMessage(MongoCollection collection,String bMessageID){
        LinkDB linkDB=new LinkDB();
        FindIterable<Document> findIterable =linkDB.searchByField(collection,"toMessageID",bMessageID);//查询回复此消息的信息
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            DeleteAllMessage(collection,document.getString("bMessageID"));//删除本消息及衍生消息
        }
        mongoCursor.close();

        linkDB.deleteByField(collection,"bMessageID",bMessageID);//删除本条消息
    }
}
