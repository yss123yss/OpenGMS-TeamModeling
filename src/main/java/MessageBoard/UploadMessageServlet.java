package MessageBoard;

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

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

@WebServlet(name="UploadMessageServlet",urlPatterns = "/UploadMessageServlet")
public class UploadMessageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LinkDB linkDB=new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling","MessageBoard");//由库名+表名，得到要添加数据的表单


        //设置对客户端请求进行重新编码的编码
        req.setCharacterEncoding("utf-8");
        //使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
        resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        //指定对服务器响应进行重新编码的编码
        resp.setCharacterEncoding("UTF-8");
        //获取向前台写数据的输出流
        PrintWriter writer = resp.getWriter();

        bMessage bmessage=new bMessage();
        HttpSession session = req.getSession();//获取客户端
        bmessage.bSender=session.getAttribute("email").toString();//获取用户邮箱(唯一)
        System.out.println(bmessage.bSender);

        bmessage.bTitle=req.getParameter("Title");
        bmessage.bContent=req.getParameter("Content");
        bmessage.toMessageID=req.getParameter("toMessageID");//回复的信息ID
        bmessage.bMessageID="这是本条消息的ID";//消息ID
        bmessage.bKey=req.getParameter("Key");//获取关键字数组
        System.out.println(bmessage.bKey);
        Date date =new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        bmessage.bTime= dateFormat.format(date);//日期

        Document doc = new Document()//储存上传数据库信息
                .append("bTitle",bmessage.bTitle)
                .append("bMessageID",bmessage.bMessageID)
                .append("toMessageID",bmessage.toMessageID)
                .append("bContent",bmessage.bContent)
                .append("bSender",bmessage.bSender)
                .append("bTime",bmessage.bTime)
                .append("bKey",bmessage.bKey);
        try{
            linkDB.docUploadtoDatabase(col,doc);//上传到数据库
            System.out.println("消息上传数据库成功！");
            writer.write("Success");
        }catch (Exception e){
            System.out.println("上传数据失败！");
            writer.write("False");
        }
    }
}
