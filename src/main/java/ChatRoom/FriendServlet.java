package ChatRoom;

import Link.LinkDB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="FriendServlet",urlPatterns = "/FriendServlet")
public class FriendServlet extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out=resp.getWriter();
        LinkDB linkDB=new LinkDB();
        MongoCollection<org.bson.Document> collection=linkDB.GetCollection("CollaborativeModeling","User");

        FindIterable<org.bson.Document> findIterable = collection.find();
        MongoCursor<org.bson.Document> mongoCursor = findIterable.iterator();

        JSONArray jsonArray =new JSONArray();

        while (mongoCursor.hasNext())
        {
            JSONObject jsonObject=new JSONObject();
            org.bson.Document document=mongoCursor.next();
            System.out.println(document);

//            jsonObject.put("id",document.getString("id"));
            jsonObject.put("name",document.getString("userName"));
//            jsonObject.put("password",document.getString("password"));
//            jsonObject.put("phone",document.getString("mobilephone"));
//            jsonObject.put("gender",document.getString("gender"));
//            jsonObject.put("country",document.getString("country"));
//            jsonObject.put("city",document.getString("city"));
//            jsonObject.put("organization",document.getString("organization"));
//            jsonObject.put("dateOfBirth",document.getString("dateOfBirth"));
//            jsonObject.put("email",document.getString("email"));
//            jsonObject.put("introduction",document.getString("introduction"));
            jsonArray.add(jsonObject);
        }
        mongoCursor.close();
        out.write(jsonArray.toString());
        out.flush();
        out.close();
    }
}
