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

@WebServlet(name = "ReadGroupServlet", urlPatterns = "/ReadGroupServlet")
public class ReadGroupServlet extends HttpServlet {
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
            MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork", "Group");
            Document groupDoc=linkDB.searchOneByField(col,"groupID",req.getParameter("groupID"));
            String groupDocStr=groupDoc.toJson();
            out.write(groupDocStr);
        }
        catch (Exception e){
            out.write("Fail");
            System.out.println("Read Group Fail!");
        }
        out.flush();
        out.close();
    }
}