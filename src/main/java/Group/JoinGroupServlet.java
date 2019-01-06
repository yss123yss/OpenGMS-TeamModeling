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

@WebServlet(name = "JoinGroupServlet", urlPatterns = "/JoinGroupServlet")
public class JoinGroupServlet extends HttpServlet {
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
            Document groupDoc=linkDB.searchOneByField(col,"groupID", req.getParameter("groupID"));
            if (groupDoc != null) {
                String memberListStr=groupDoc.getString("memberList");
                ArrayList<String> memberList = new ArrayList<String>(Arrays.asList(memberListStr.split(",")));
                Boolean exist=false;
                if(!memberList.get(0).equals("")){
                    if(groupDoc.getString("creator").equals(req.getParameter("userName"))){
                        exist=true;
                    }
                    else {
                        for(int i=0;i<memberList.size();i++){
                            if(memberList.get(i).equals(req.getParameter("userName"))){
                                exist=true;
                                break;
                            }
                        }
                    }

                }
                if (exist){
                    out.write("Exist");
                }
                else {
                    if(memberList.get(0).equals("")){
                        memberListStr=req.getParameter("userName");
                    }
                    else {
                        memberList.add(req.getParameter("userName"));
                        memberListStr = StringUtil.join(memberList,",");//数组转字符串，“，”作为分隔符
                    }

                    BasicDBObject conditionGroup = new BasicDBObject();
                    conditionGroup.put("groupID", req.getParameter("groupID"));
                    BasicDBObject newData = new BasicDBObject();
                    newData.put("memberList",memberListStr);
                    BasicDBObject updateData=new BasicDBObject("$set",newData);
                    col.updateOne(conditionGroup,updateData);

                    col = linkDB.GetCollection("CollaborativeWork", "User");
                    Document userDoc=linkDB.searchOneByField(col,"userName",req.getParameter("userName"));
                    String joinedGroupStr=userDoc.getString("joinedGroup");
                    ArrayList<String> joinedGroup=new ArrayList<String>(Arrays.asList(joinedGroupStr.split(",")));
                    if (joinedGroup.get(0).equals("")){
                        joinedGroupStr=req.getParameter("groupID");
                    }
                    else {
                        joinedGroup.add(req.getParameter("groupID"));
                        joinedGroupStr=StringUtil.join(joinedGroup,",");
                    }
                    BasicDBObject conditionUser = new BasicDBObject();
                    conditionUser.put("userName", req.getParameter("userName"));
                    BasicDBObject newUserData = new BasicDBObject();
                    newUserData.put("joinedGroup",joinedGroupStr);
                    BasicDBObject updateUserData=new BasicDBObject("$set",newUserData);
                    col.updateOne(conditionUser,updateUserData);

                    col = linkDB.GetCollection("CollaborativeWork", "Group");
                    groupDoc=linkDB.searchOneByField(col,"groupID", req.getParameter("groupID"));
                    String groupDataStr=groupDoc.toJson();
                    out.write(groupDataStr);
                }

            }
            else {
                out.write("None");
            }
        }
        catch (Exception e){
            System.out.println("Join Group Fail!");
            out.write("Fail");
        }
        out.flush();
        out.close();
    }
}