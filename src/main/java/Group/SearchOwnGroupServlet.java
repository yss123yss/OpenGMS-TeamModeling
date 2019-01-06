package Group;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SearchOwnGroupServlet", urlPatterns = "/SearchOwnGroupServlet")
public class SearchOwnGroupServlet extends HttpServlet {

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
        FindIterable<Document> findIterable = null;
        MongoCursor<Document> mongoCursor;
        List<String> strings = new ArrayList<>();
        findIterable = linkDB.searchByField(col, "creator", req.getParameter("creator"));
        try {
            mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                strings.add(document.toJson());
            }
            mongoCursor.close();
            out.write(strings.toString());
        } catch (Exception e) {
            out.write("Fail");
            System.out.println("Own Groups Reading Fail!");
        }
        out.flush();
        out.close();
    }
}
