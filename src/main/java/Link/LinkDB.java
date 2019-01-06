package Link;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LinkDB {
    private static MongoClient client= null;
    private static InputStream in= LinkDB.class.getClassLoader().getResourceAsStream("TeamWorking.properties");//.class.getClassLoader().getResource("")为获取资源路径

    static{
        Properties properties = new Properties();
        try {
            properties.load(in);//获得(服务器)属性
            String host = properties.getProperty("host");//服务器地址

            client = new MongoClient(host);//连接到数据库服务器
            System.out.println("连接到"+host);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //在数据库中创建集合
    public void createCollection(String  databaseName,String collectionName)
    {
        try
        {
            MongoDatabase mongoDatabase=client.getDatabase(databaseName);
            mongoDatabase.createCollection(collectionName);
            System.out.println("Create collection "+collectionName+" successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回指定数据库的指定集合
    public MongoCollection<Document> GetCollection(String dbName, String collName){
        MongoDatabase mongoDatabase=client.getDatabase(dbName);//获取数据库
        return mongoDatabase.getCollection(collName);//返回数据集合
    }

    //判断某集合是否存在
    public boolean isCollectionExists(String dbName, String collName) {
        for (String name : client.getDatabase(dbName).listCollectionNames()) {//遍历数据库
            if (collName.equals(name)) {//对比集合名
                return true;
            }
        }
        return false;
    }

    //记录上传至数据库
    public void docUploadtoDatabase(MongoCollection<Document> collection, Document document){
        collection.insertOne(document);
    }

    //查询某数据集合的所有文档
    public  FindIterable<Document> searchAll(MongoCollection collection){
        return collection.find();
    }

    //查询某字段某值的文档
    public FindIterable<Document> searchByField(MongoCollection collection,String Field,String Value){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(Field, Value);
        return  collection.find(searchQuery);
    }

    //返回某字段某值的第一条文档
    public Document searchOneByField(MongoCollection<Document> collection,String Field,String Value){
        Document doc=null;
        try {
            doc = collection.find(Filters.eq(Field,Value)).first();
        } catch (Exception e) {
            return null;
        }
        return doc;
    }

    //通过OID进行查询
    public FindIterable<Document> searchByOID(MongoCollection collection,String oid){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", new ObjectId(oid));
        return collection.find(searchQuery);
    }

    //删除某字段某值的文档
    public void deleteByField(MongoCollection collection,String Field,String Value){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(Field, Value);
        collection.deleteMany(searchQuery);
    }

    //删除某OID的文档
    public void deleteByOID(MongoCollection collection,String oid){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("_id", new ObjectId(oid));
        collection.deleteMany(searchQuery);
    }
}
