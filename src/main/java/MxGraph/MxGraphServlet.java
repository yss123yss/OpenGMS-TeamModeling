package MxGraph;
import Link.LinkDB;
import com.mongodb.BasicDBObject;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebServlet(name="MxGraphServlet",urlPatterns = "/MxGraphServlet")
public class MxGraphServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        LinkDB linkDB=new LinkDB();
        String ModelType="";
        if(request.getParameter("Model").equals("Concept")){
            ModelType="ConceptModelTask";
        }
        else if (request.getParameter("Model").equals("Logical")){
            ModelType="LogicalModelTask";
        }
        else if (request.getParameter("Model").equals("Computable")){
            ModelType="ComputableModelTask";
        }
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork",ModelType);
        PrintWriter out = response.getWriter();
        Date date =new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (request.getParameter("Type").equals("Save")){

            String ProjectID=UUID.randomUUID().toString();
            Document doc = new Document()//储存上传数据库信息
                    .append("ProjectID",ProjectID)
                    .append("TaskName",request.getParameter("TaskName"))
                    .append("Description",request.getParameter("Description"))
                    .append("GraphXML",request.getParameter("GraphXML"))
                    .append(request.getParameter("Model")+"XML",request.getParameter(request.getParameter("Model")+"XML"))
                    .append("CID",request.getParameter("CID"))
                    .append("CreateTime",dateFormat.format(date));
            try{
                linkDB.docUploadtoDatabase(col,doc);
                out.write(ProjectID);
            }catch (Exception e){
                e.printStackTrace();
                out.write("Save Fail");
            }
        }
        else if (request.getParameter("Type").equals("Read")){
            FindIterable<Document> findIterable=linkDB.searchByField(col,"CID",request.getParameter("CID"));
            MongoCursor<Document> mongoCursor;
            List<String> data =new ArrayList<>();
            try {
                mongoCursor = findIterable.iterator();
                while(mongoCursor.hasNext()){
                    Document document=mongoCursor.next();
                    data.add(document.toJson());
                }
                mongoCursor.close();
            }catch (Exception e){
                System.out.println("Graph Reading Fail!");
            }
            out.write(data.toString());
        }
        else if (request.getParameter("Type").equals("Update")){
            BasicDBObject query=new BasicDBObject();
            query.put("ProjectID",request.getParameter("ProjectID"));
            BasicDBObject newDoc=new BasicDBObject();
            newDoc.put("GraphXML",request.getParameter("GraphXML"));
            newDoc.put(request.getParameter("Model")+"XML",request.getParameter(request.getParameter("Model")+"XML"));
            newDoc.put("CreateTime",dateFormat.format(date));
            newDoc.put("TaskName",request.getParameter("TaskName"));
            newDoc.put("Description",request.getParameter("Description"));

            BasicDBObject updateObj=new BasicDBObject();
            updateObj.put("$set",newDoc);
            col.updateOne(query,updateObj);
            out.write("success");
        }
        else if(request.getParameter("Type").equals("Delete")){
            try {
                linkDB.deleteByField(col,"ProjectID",request.getParameter("ProjectID"));
                out.write("success");
            }catch (Exception e){
                out.write("fail");
            }
        }
        out.flush();
        out.close();
    }
}
