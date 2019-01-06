package Management.Dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface IUserDao {

    //连接到mongodb服务
    void initMongoDB();

    //连接到mongodb
    MongoDatabase getMongoDatabase(String databaseName);

    //创建集合
    void createCollection(String databaseName, String collectionName);

    //获得集合
    MongoCollection<Document> getCollection(String databaseName, String collectionName);

    //条件查询
    MongoCursor<Document> RetrieveDocsByFilter(MongoCollection<Document> coll, Bson filter);

    //根据主键id查document
    Document RetrieveDocById(MongoCollection<Document> coll, String id);

    //根据字段值查询document
    Document RetrieveDocByOneField(MongoCollection<Document> coll, String dbFieldName, String fieldName);


    //判断某集合是否存在
    boolean isCollectionExists(MongoDatabase mongoDatabase, String collectionName);

    Document RetrieveDocBy2Fields(MongoCollection<Document> mongoCollection, Bson filter1, Bson filter2, String account, String password);
}
