package Group;

import Link.LinkDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="UpdateGroupServlet",urlPatterns = "/UpdateGroupServlet")
public class UpdateGroupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB = new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork", "Group");
        try {
            BasicDBObject condition = new BasicDBObject();
            condition.put("groupID", req.getParameter("groupID"));
            BasicDBObject newData = new BasicDBObject();
            newData.put("groupName",req.getParameter("groupName"));
            newData.put("groupDescription",req.getParameter("groupDescription"));
            BasicDBObject updateData=new BasicDBObject("$set",newData);
            col.updateOne(condition,updateData);

            out.write("Success");
        }
        catch (Exception e){
            System.out.println("Group Update Fail!");
        }
        out.flush();
        out.close();
    }
}
