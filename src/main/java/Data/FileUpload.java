package Data;

import Link.LinkDB;
import com.mongodb.client.MongoCollection;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.net.InetAddress;

@WebServlet(name="fileupload",urlPatterns = "/FileUpload")
public class FileUpload extends HttpServlet {
    private LinkDB linkDB=new LinkDB();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        //PrintWriter的实例就是指像前台的jsp页面输出结果
        PrintWriter out = resp.getWriter();
        //获取服务器IP地址
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress();
        // 创建文件夹
        String projectPath = req.getSession().getServletContext().getRealPath("");//项目保持路径
//        System.out.println("项目保持路径为："+projectPath);
        //创建一个json对象
        JSONArray jsonArray=new JSONArray();
        //1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        //3、判断提交上来的数据是否是上传表单的数据
        if (!ServletFileUpload.isMultipartContent(req)) {
            PrintWriter writer = resp.getWriter();
            writer.flush();
            System.out.println("文件不为多媒体文件，不可上传！");
            return;
        }

        Date date =new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String time= dateFormat.format(date);

        //放在对应的文件夹下面


        try {

            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> items = upload.parseRequest(req);//参数是HttpServletRequest对象


            for (FileItem item : items) {//遍历所有客户端提交的参数(包括文件域)
                String key = item.getFieldName();//取出文件域的键
                String value = item.getName();//取出文件域的值
                //这里value值是拿到的文件名,对value进行处理,处理以后根据后缀名存储到不同的文件夹中
                String[] value_process = value.split("\\.");
                String name=value_process[0];//文件名
                String suffix = value_process[1];//后缀
                //保存上传的文件到服务器本地文件夹内
                String regexp="[^A-Za-z_0-9\\u4E00-\\u9FA5]";//正则表达式取非数字、字母及汉字的字符
                String name_no=name.replaceAll(regexp,"_");//将非字的字符替换

                String folder = projectPath + "FileUpload\\" + key;
                File temp = new File(folder);
                if (!temp.exists()) {
                    temp.mkdirs();
                }
                String path = temp +"\\" +name_no+"."+suffix;//服务器本地路径
                System.out.println("本地路径为："+path);
                item.write(new File(path));//储存文件到本地路径
                String reqPath = req.getRequestURL().toString();
                System.out.println("reqPath:"+reqPath);
                String url = reqPath.replaceAll("localhost",ip) + "/" +key+"/"+ name_no+"."+suffix;//服务器上传路径
                System.out.println("url:"+url);
                JSONObject json = new JSONObject();//储存返回信息
                Document doc = new Document();//储存上传数据库信息
                if(key.equals("File")||key.equals("Publication")||key.equals("Multimedia")){
                    json.put("Path", url);
                    json.put("FileName",value);
                    json.put("Type",key);

                    doc.append("Path",json.getString("Path"));
                    doc.append("FileName",json.getString("FileName"));
                    doc.append("Time",time);
                    doc.append("Abstract","......");
                    doc.append("Uploader","xxx");
                }else {
                }

                MongoCollection<Document> col = linkDB.GetCollection("CollaborativeModeling",key);//由库名+表名，得到要添加数据的表单
                jsonArray.add(json);
                //col.insertOne(doc);
                linkDB.docUploadtoDatabase(col,doc);//写入数据库的对应表单

                System.out.println("上传信息到数据库成功！");
//                System.out.println("读取到的文件名为："+doc.getString("FileName"));
//                System.out.println("保存路径为："+json.getString("Path"));
            }
            System.out.println("文件上传返回信息："+jsonArray.toString());
            out.write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println("上传数据失败！");
            e.printStackTrace();
            String url = "";
            out.write(url);
        }
    }
}
