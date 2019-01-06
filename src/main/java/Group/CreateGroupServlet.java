package Group;

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
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "CreateGroupServlet", urlPatterns = "/CreateGroupServlet")
public class CreateGroupServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB=new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork","Group");
        Date date =new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        HttpSession session = req.getSession();
        String memberListStr="";
        Document doc = new Document()//储存上传数据库信息
                .append("creator",req.getParameter("creator"))
                .append("groupName",req.getParameter("groupName"))
                .append("groupID",req.getParameter("groupID"))
                .append("groupDescription",req.getParameter("groupDescription"))
                .append("memberList",memberListStr)
                .append("createTime",dateFormat.format(date));
        try{
            linkDB.docUploadtoDatabase(col,doc);
            out.write(doc.toJson());
        }catch (Exception e){
            out.write("Fail");
            e.printStackTrace();
        }
        out.flush();
        out.close();
    }
}
