package Management.Controller;

//import ChatRoom.MongoOperator;
import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "DescriptionSave2BackendServlet", urlPatterns = "/DescriptionSave2BackendServlet")
public class DescriptionSave2BackendServlet extends HttpServlet {

    String host = "222.192.7.75";
    Integer port = 27017;
    String databaseName = "CollaborativeModeling";
    String collectionName = "ProblemDescription";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //得到前台返回的title和content，存入数据库
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");
//        MongoOperator mongoOperator = new MongoOperator();
        LinkDB linkDB=new LinkDB();
//        MongoDatabase mongoDatabase = mongoOperator.initMongoDB(host, port, databaseName);
        MongoCollection mongoCollection;
        if (linkDB.isCollectionExists(databaseName, collectionName)) {
            mongoCollection = linkDB.GetCollection(databaseName, collectionName);
        } else {
            linkDB.createCollection(databaseName, collectionName);
            mongoCollection = linkDB.GetCollection(databaseName, collectionName);

            List<String> a = new ArrayList<>();
        }

        try {
            Document document = new Document("email", email).append("title", title).append("content", content);
            List<Document> documents = new ArrayList<Document>();
            documents.add(document);
            mongoCollection.insertMany(documents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(title);
        System.out.println(content);


        //从数据库取值，发送到前台
//        MongoOperator mongoOperator=new MongoOperator();
//        MongoDatabase mongoDatabase=mongoOperator.initMongoDB(host,port,databaseName);
//        MongoCollection<Document> mongoCollection=null;
//        PrintWriter out=null;
//        out=resp.getWriter();
//
//        if(mongoOperator.isCollectionExists(mongoDatabase,collectionName))
//        {
//            mongoCollection=mongoOperator.getCollection(mongoDatabase,collectionName);
//            FindIterable<Document> findIterable = mongoCollection.find();
//            MongoCursor<Document> mongoCursor = findIterable.iterator();
//            JSONArray json =new JSONArray();
//
//            while (mongoCursor.hasNext())
//            {
//                JSONObject jsonObject=new JSONObject();
//                Document document=mongoCursor.next();
//                System.out.println(document);
//                String emailFromSession=(String)session.getAttribute("email");
//                String emailFromDB=document.getString("email");
//                String titleFromDB=document.getString("title");
//                String contentFromDB=document.getString("content");
//                if(emailFromDB.equals(emailFromSession)) {
//
//                    jsonObject.put("email", emailFromDB);
//                    jsonObject.put("title", titleFromDB);
//                    jsonObject.put("content", contentFromDB);
//                    json.add(jsonObject);
//                }
//            }
//
//            String jsonString= json.toString();
//            out.print(jsonString);
//        }
//        else
//        {
//            out.print("0");
//        }
    }

}
