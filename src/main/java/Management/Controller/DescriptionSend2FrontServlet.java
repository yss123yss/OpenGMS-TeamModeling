package Management.Controller;


//import ChatRoom.MongoOperator;
import Link.LinkDB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DescriptionSend2FrontServlet", urlPatterns = "/DescriptionSend2FrontServlet")
public class DescriptionSend2FrontServlet extends HttpServlet {
    String host = "222.192.7.75";
    Integer port = 27017;
    String databaseName = "CollaborativeModeling";
    String collectionName = "ProblemDescription";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        //验证从前台接收到的消息是否为“1”


        LinkDB linkDB=new LinkDB();
//        MongoDatabase mongoDatabase = mongoOperator.initMongoDB(host, port, databaseName);
        MongoCollection<Document> mongoCollection = null;
        PrintWriter out = null;
        out = resp.getWriter();

        if (linkDB.isCollectionExists(databaseName, collectionName)) {
            mongoCollection = linkDB.GetCollection(databaseName, collectionName);
            FindIterable<Document> findIterable = mongoCollection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            JSONArray json = new JSONArray();

            while (mongoCursor.hasNext()) {
                JSONObject jsonObject = new JSONObject();
                Document document = mongoCursor.next();
                System.out.println(document);
                HttpSession session = req.getSession();
                String emailFromSession = (String) session.getAttribute("email");
                String emailFromDB = document.getString("email");
                String title = document.getString("title");
                String content = document.getString("content");
                /*null本身不是一个对象  没有属性 也没有方法 ，所以引用equal方法的对象不能为null*/
                if (emailFromDB != null && emailFromSession != null) {
                    if (emailFromDB.equals(emailFromSession)) {

                        jsonObject.put("email", emailFromDB);
                        jsonObject.put("title", title);
                        jsonObject.put("content", content);
                        json.add(jsonObject);
                    }
                }
            }

            String jsonString = json.toString();
            out.print(jsonString);

        } else {
            out.print("0");
        }

    }
}
