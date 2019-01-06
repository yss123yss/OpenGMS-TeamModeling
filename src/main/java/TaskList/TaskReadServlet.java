package TaskList;

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

@WebServlet(name="TaskReadServlet",urlPatterns = "/TaskReadServlet")
public class TaskReadServlet extends HttpServlet {

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
        FindIterable<Document> findIterable=null;
        MongoCursor<Document> mongoCursor;
        if (req.getParameter("Type").equals("Load")){
            List<String> strings =new ArrayList<>();
            findIterable = linkDB.searchAll(col);//生成文档的迭代对象
            try {
                mongoCursor = findIterable.iterator();//定义在迭代器开始的游标
                while(mongoCursor.hasNext()){//若游标还在则继续
                    Document document=mongoCursor.next();//指针所指的文档数据(元组)
                    strings.add(document.toJson());
                }
                mongoCursor.close();
            }catch (Exception e){
                System.out.println("Task Reading Fail!");
            }
            out.write(strings.toString());
        }
        else if(req.getParameter("Type").equals("Edit")){
            findIterable = linkDB.searchByOID(col,req.getParameter("TaskID"));
            String str="";
            try {
                mongoCursor = findIterable.iterator();//定义在迭代器开始的游标
                while(mongoCursor.hasNext()){//若游标还在则继续
                    Document document=mongoCursor.next();//指针所指的文档数据(元组)
                    str=document.toJson();
                }
                mongoCursor.close();
            }catch (Exception e){
                System.out.println("Task Reading Fail!");
            }
            out.write(str);
        }
        out.flush();
        out.close();
    }
}
