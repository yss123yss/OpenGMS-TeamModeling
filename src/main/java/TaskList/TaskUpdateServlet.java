package TaskList;

import Link.LinkDB;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="TaskUpdateServlet",urlPatterns = "/TaskUpdateServlet")
public class TaskUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        LinkDB linkDB = new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling", "Task");

        if(req.getParameter("Type").equals("Delete")){//删除某任务
            linkDB.deleteByOID(col,req.getParameter("TaskID"));
            out.write("Success");
        }
        else {
            try {
                BasicDBObject condition=new BasicDBObject();
                condition.put("_id",new ObjectId(req.getParameter("TaskID")));
                BasicDBObject new_data=new BasicDBObject();
                if (req.getParameter("Type").equals("Update")){//更新任务信息
                    new_data.put("TaskName",req.getParameter("TaskName"));
                    new_data.put("Description",req.getParameter("Description"));
                    new_data.put("StartTime",req.getParameter("StartTime"));
                    new_data.put("EndTime",req.getParameter("EndTime"));
                }
                else if (req.getParameter("Type").equals("Move")){//更新任务状态
                    new_data.put("Order",req.getParameter("Order"));
                    new_data.put("State",req.getParameter("State"));
                }
                BasicDBObject update_data=new BasicDBObject("$set",new_data);
                col.updateOne(condition,update_data);
            }catch (Exception e){
                System.out.println("Task Reading Fail!");
            }
        }
        out.flush();
        out.close();
    }
}