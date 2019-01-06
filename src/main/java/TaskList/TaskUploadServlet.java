package TaskList;


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

@WebServlet(name="TaskUploadServlet",urlPatterns = "/TaskUploadServlet")
public class TaskUploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB=new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","Task");

        Date date =new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        HttpSession session = req.getSession();
        Document doc = new Document()//储存上传数据库信息
                .append("TaskName",req.getParameter("TaskName"))
                .append("Description",req.getParameter("Description"))
                .append("StartTime",req.getParameter("StartTime"))
                .append("EndTime",req.getParameter("EndTime"))
                .append("State","Todo")
                .append("CreateTime",dateFormat.format(date));
//                .append("Order",req.getParameter("Order")),//任务在列中顺序
//                .append("Uploader",session.getAttribute("email").toString());//任务创建者
        try{
            linkDB.docUploadtoDatabase(col,doc);
            out.write("Success");
        }catch (Exception e){
            e.printStackTrace();
            out.write("Fail");
        }
    }

}
