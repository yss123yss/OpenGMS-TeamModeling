package User;

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="UserProfileServlet",urlPatterns = "/UserProfileServlet")
public class UserProfileServlet  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB = new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling", "User");
        try{
            Document myDoc=col.find(Filters.eq("userName", req.getParameter("username"))).first();
            out.write(myDoc.toJson());
        }
        catch (Exception e){
            System.out.println("No found!");
        }


    }
}
