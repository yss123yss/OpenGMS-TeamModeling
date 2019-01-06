package ChatRoom.Dao;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface IModelDao {
	String GetModelTree();
	//数据库
	MongoDatabase GetDB(String dbName);
	MongoCollection<Document> GetCollection(String dbName, String collName);
	//增（Create）
	boolean CreateCollection(String collectionName);
	boolean InsertDoc(Document doc);
	//删（Delete）
	boolean DeleteDocById(String Id);//删除单条记录
	//改（Update）
	boolean UpdateDocById(String Id); //更新单条记录
	//查（Retrieve）

	MongoCursor<Document> RetrieveDocsByFilter(MongoCollection<Document> coll, Bson filter);//条件查询
	Document RetrieveDocById(MongoCollection<Document> coll, String id); //通过Id查询文档
	Document RetrieveDocsByName(MongoCollection<Document> collectionName, String modelName); //通过name查询文档
	Document RetrieveDocByOneField(MongoCollection<Document> collectionName, String dbFieldName, String fieldName);

	//分页查询
	MongoCursor<Document> RetrieveDocsLimit(MongoCollection<Document> col, Bson filter, BasicDBObject nameFilter, int page);
	MongoCursor<Document> RetrieveDocsLimitByTime(MongoCollection<Document> col, Bson filter, BasicDBObject timeFilter, int page);

	BasicDBObject getSort(String sortType, int asc);

	Boolean ping(String ipAddress, int pingTimes, int timeOut);
}
