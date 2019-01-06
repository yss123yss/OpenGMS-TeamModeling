package MessageBoard;
import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="SearchMessageServlet",urlPatterns = "/SearchMessageServlet")
public class SearchMessageServlet extends HttpServlet {
    LinkDB linkDB=new LinkDB();
    MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","MessageBoard");//由库名+表名，得到要添加数据的表单
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        JSONArray jsonArray=new JSONArray();

        FindIterable<Document> findIterable = col.find();//生成文档的迭代对象
        MongoCursor<Document> mongoCursor = findIterable.iterator();//定义在迭代器开始的游标

        try{
            while(mongoCursor.hasNext()){//若游标还在则继续
                JSONObject jsonObject=new JSONObject();
                Document document=mongoCursor.next();//指针所指的文档数据(元组)
                jsonObject.put("title",document.getString("bTitle"));
                jsonObject.put("content",document.getString("bContent"));
                jsonObject.put("time",document.getString("bTime"));
                jsonObject.put("author",document.getString("bSender"));
                jsonObject.put("bMessageID", document.getString("bMessageID"));
                jsonObject.put("toMessageID", document.getString("toMessageID"));
                jsonObject.put("bKey",document.getString("bKey"));
//                jsonObject.put("Uploader",document.getString("Uploader"));
//                System.out.println(jsonObject.toString());      //print query data from database in console
                jsonArray.add(jsonObject);
            }
            mongoCursor.close();
        }catch(Exception e){
            System.out.println("Data Reading Fail!");
        }
        PrintWriter out =resp.getWriter();
        out.write(jsonArray.toString());
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //设置对客户端请求进行重新编码的编码
        req.setCharacterEncoding("utf-8");
        //使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
        resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        //指定对服务器响应进行重新编码的编码
        resp.setCharacterEncoding("UTF-8");
        //获取向前台写数据的输出流
        PrintWriter writer = resp.getWriter();

        String Field=req.getParameter("Field");//查询字段
        String Value=req.getParameter("Value");//查询值
        String jsonString;

        if(!Field.equals("Key")){
            FindIterable<Document> findIterable;
            findIterable =linkDB.searchByField(col,Field,Value);//查询
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            JSONArray jsonArray = new JSONArray();
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();
                System.out.println(document);
                JSONObject jsonObject=addJSONobject(document);//将数据存入JSON
                jsonArray.add(jsonObject);
            }
            mongoCursor.close();
            jsonString = jsonArray.toString();
        }
        else {
            jsonString=searchByKey(Value);
        }

        System.out.println(jsonString);
        writer.print(jsonString);
    }
    //查询含有关键字的所有文档
    private String searchByKey(String KeyValue){
        FindIterable<Document> findIterable=col.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        JSONArray jsonArray = new JSONArray();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            if(IsKey(KeyValue,document)){//判断该文档是否属于该标签
                JSONObject jsonObject=addJSONobject(document);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray.toString();
    }

    private JSONObject addJSONobject(Document document){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bTitle", document.getString("bTitle"));
        jsonObject.put("bMessageID", document.getString("bMessageID"));
        jsonObject.put("toMessageID", document.getString("toMessageID"));
        jsonObject.put("bContent", document.getString("bContent"));
        jsonObject.put("bSender", document.getString("bSender"));
        jsonObject.put("bTime", document.getString("bTime"));
        jsonObject.put("bKey",document.getString("bKey"));
        return jsonObject;
    }
    //是否含有该标签
    private boolean IsKey(String KeyValue,Document document){
        String[] allKey=document.getString("bKey").split(",");
        for (int i=0;i<allKey.length;i++){
            if(allKey[i].equals(KeyValue)){
                return true;
            }
        }
        return false;
    }
}
