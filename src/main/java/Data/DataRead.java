package Data;

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.FindIterable;
import net.sf.json.JSONArray;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name="data_read",urlPatterns = "/DataRead")
public class DataRead extends HttpServlet {

    private LinkDB linkDB=new LinkDB();
//    private MongoCollection<Document> col = modelDao.GetCollection("CollaborativeModeling","File");//由库名+表名，得到要添加数据的表单
private MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","File");
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        FindIterable<Document> findIterable = linkDB.searchAll(col);//生成文档的迭代对象
        MongoCursor<Document> mongoCursor = findIterable.iterator();//定义在迭代器开始的游标

        JSONArray jsonArray=new JSONArray();
        try {
            while(mongoCursor.hasNext()){//若游标还在则继续
                Document document=mongoCursor.next();//指针所指的文档数据(元组)
                jsonArray.add(document.toJson());
            }
            mongoCursor.close();
        }catch (Exception e){
            System.out.println("Data Reading Fail!");
        }
        PrintWriter out=resp.getWriter();
//        System.out.println(jsonArray.toString());
        out.write(jsonArray.toString());
        out.flush();
        out.close();
    }
}
