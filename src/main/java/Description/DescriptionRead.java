package Description;

import Link.LinkDB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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

@WebServlet(name = "DescriptionRead", urlPatterns = "/DescriptionRead")
public class DescriptionRead extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        LinkDB linkDB=new LinkDB();
        MongoCollection<Document> collection=linkDB.GetCollection("CollaborativeModeling","ProblemDescription");

        FindIterable<Document> findIterable =collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        JSONArray jsonArray = new JSONArray();

        HttpSession session = req.getSession();
        String emailFromSession = (String) session.getAttribute("email");
        System.out.println("emailFromSession:"+emailFromSession);

        while (mongoCursor.hasNext()) {

            JSONObject jsonObject = new JSONObject();
            Document document = mongoCursor.next();
            System.out.println(document);

            String emailFromDB = document.getString("email");
            String title = document.getString("title");
            String content = document.getString("content");

            /*null本身不是一个对象  没有属性 也没有方法 ，所以引用equal方法的对象不能为null*/
            if (emailFromDB != null && emailFromSession != null) {//只获取对应用户所上传的信息
                if (emailFromDB.equals(emailFromSession)) {//匹配到此用户名
                    jsonObject.put("email", emailFromDB);
                    jsonObject.put("title", title);
                    jsonObject.put("content", content);
                    jsonArray.add(jsonObject);
                }
            }
        }
        mongoCursor.close();
        String jsonString = jsonArray.toString();
        System.out.println(jsonString);
        out.print(jsonString);

    }
}
