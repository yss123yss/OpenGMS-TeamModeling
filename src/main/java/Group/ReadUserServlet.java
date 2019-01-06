package Group;

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ReadUserServlet", urlPatterns = "/ReadUserServlet")
public class ReadUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB = new LinkDB();
        try {
            MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork", "User");
            Document userDoc=linkDB.searchOneByField(col,"userName",req.getParameter("userName"));
            String userDocStr=userDoc.toJson();
            out.write(userDocStr);
        }
        catch (Exception e){
            out.write("Fail");
            System.out.println("Read User Fail!");
        }
        out.flush();
        out.close();
    }
}