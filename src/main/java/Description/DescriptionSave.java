package Description;

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
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

@WebServlet(name = "DescriptionSave", urlPatterns = "/DescriptionSave")
public class DescriptionSave extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //得到前台返回的title和content，存入数据库
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        LinkDB linkDB=new LinkDB();
        MongoCollection collection=linkDB.GetCollection("CollaborativeModeling","ProblemDescription");

        String title = req.getParameter("title");//获取Value
        String content = req.getParameter("content");

        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");

        try {
            Document document = new Document();
            document.append("email", email)
                    .append("title", title)
                    .append("content", content);
            List<Document> documents = new ArrayList<Document>();
            documents.add(document);
            collection.insertMany(documents);//写入记录
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
