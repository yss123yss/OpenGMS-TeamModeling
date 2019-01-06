package Group;

import Link.LinkDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jsoup.helper.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet(name="DeleteGroupServlet",urlPatterns = "/DeleteGroupServlet")
public class DeleteGroupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB = new LinkDB();
        try{
            MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork", "Group");
            Document groupDoc=linkDB.searchOneByField(col,"groupID", req.getParameter("groupID"));
            String memberListStr=groupDoc.getString("memberList");
            ArrayList<String> memberList = new ArrayList<String>(Arrays.asList(memberListStr.split(",")));
            col = linkDB.GetCollection("CollaborativeWork", "User");
            if(memberList.equals("")){
                for(String member : memberList){
                    Document userDoc=linkDB.searchOneByField(col,"userName",member);
                    String joinedGroupStr=userDoc.getString("joinedGroup");
                    ArrayList<String> joinedGroup=new ArrayList<String>(Arrays.asList(joinedGroupStr.split(",")));
                    joinedGroup.remove(req.getParameter("groupID"));
                    joinedGroupStr=StringUtil.join(joinedGroup,",");
                    BasicDBObject conditionUser = new BasicDBObject();
                    conditionUser.put("userName", member);
                    BasicDBObject newUserData = new BasicDBObject();
                    newUserData.put("joinedGroup",joinedGroupStr);
                    BasicDBObject updateUserData=new BasicDBObject("$set",newUserData);
                    col.updateOne(conditionUser,updateUserData);
                }
            }
            try{
                col = linkDB.GetCollection("CollaborativeWork", "Group");
                linkDB.deleteByField(col,"groupID",req.getParameter("groupID"));
                out.write("Success");
            }
            catch (Exception e){
                out.write("Failure");
            }
        }catch (Exception e){
            out.write("Failure");
        }
        out.flush();
        out.close();
    }
}
