package Management.Dao;

import ChatRoom.Dao.ModelDaoImpl;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.InputStream;
import java.util.Properties;

public class UserDaoImpl implements IUserDao {

    private static InputStream inputStream = ModelDaoImpl.class.getClassLoader().getResourceAsStream("TeamWorking.properties");
    private static Properties properties;
    private static MongoClient mongoClient = null;


    static {
        properties = new Properties();
        try {
            properties.load(inputStream);
            String host = properties.getProperty("host");
            Integer port = new Integer(properties.getProperty("port"));
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.connectionsPerHost(10);
            builder.connectTimeout(10000);
            builder.maxWaitTime(120000);
            builder.socketKeepAlive(false);
            builder.cursorFinalizerEnabled(true);
            builder.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
            builder.writeConcern(WriteConcern.SAFE);
            MongoClientOptions options = builder.build();

            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = new ServerAddress(host, port);
            mongoClient = new MongoClient(serverAddress, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*连接到mongodb服务*/
    @Override
    public void initMongoDB() {
        MongoClient mongoClient = null;
        String host = properties.getProperty("host");
        Integer port = new Integer(properties.getProperty("port"));

        try {
            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

            builder.connectionsPerHost(10);
            builder.connectTimeout(10000);
            builder.maxWaitTime(120000);
            builder.socketKeepAlive(false);
            builder.cursorFinalizerEnabled(true);
            builder.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
            builder.writeConcern(WriteConcern.SAFE);
            MongoClientOptions options = builder.build();

            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = new ServerAddress(host, port);
            mongoClient = new MongoClient(serverAddress, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*连接到数据库*/
    @Override
    public MongoDatabase getMongoDatabase(String databaseName) {
        //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        if (databaseName != null && !"".equals(databaseName)) {
            //连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
            System.out.println("Connect to database successfully");
            return mongoDatabase;
        }
        return null;
    }

    /*创建集合*/
    @Override
    public void createCollection(String databaseName, String collectionName) {
        MongoDatabase mongoDatabase = getMongoDatabase(databaseName);
        try {
            mongoDatabase.createCollection(collectionName);
            System.out.println("Create collection " + collectionName + " successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*获得集合*/
    @Override
    public MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        //选择集合对象
        MongoDatabase mongoDatabase = getMongoDatabase(databaseName);
        MongoCollection<Document> collection = null;
        try {
            collection = mongoDatabase.getCollection(collectionName);
            System.out.println("Get collection " + collectionName + " successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }


    /*条件查询*/
    @Override
    public MongoCursor<Document> RetrieveDocsByFilter(MongoCollection<Document> mongoCollection, Bson filter) {
        // TODO Auto-generated method stub
        return mongoCollection.find(filter).iterator();
    }

    /*根据主键id查document*/
    @Override
    public Document RetrieveDocById(MongoCollection<Document> mongoCollection, String id) {
        // TODO Auto-generated method stub
        Document myDoc = null;
        try {
            myDoc = mongoCollection.find(Filters.eq("UID", id)).first();
        } catch (Exception e) {
            return null;
        }
        return myDoc;
    }

    /*根据字段值查询文档*/
    @Override
    public Document RetrieveDocByOneField(MongoCollection<Document> mongoCollection, String dbFieldName, String fieldName) {
        // TODO Auto-generated method stub
        Document myDoc = null;
        try {
            myDoc = mongoCollection.find(Filters.eq(dbFieldName, fieldName)).first();
        } catch (Exception e) {
            return null;
        }
        return myDoc;
    }


    /*判断某集合是否存在*/
    @Override
    public boolean isCollectionExists(MongoDatabase mongoDatabase, String collectionName) {

        for (String name : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /*AND查询*/
    @Override
    public Document RetrieveDocBy2Fields(MongoCollection<Document> mongoCollection, Bson filter1, Bson filter2, String email, String password) {
        Bson filters = Filters.and(Filters.eq("email", email), Filters.eq("password", password));
        Document myDoc = null;
        try {

            myDoc = mongoCollection.find(Filters.and(Filters.eq("email", email), Filters.eq("password", password))).first();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return myDoc;
    }


}
