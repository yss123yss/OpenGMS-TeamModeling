package ChatRoom.Dao;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelDaoImpl implements IModelDao {

	private final static String HOST = "localhost";// 端口
	private final static int PORT = 27017;// 端口
	private final static int POOLSIZE = 100;// 连接数量
	private final static int BLOCKSIZE = 100; // 等待队列长度
	private static MongoClient client= null;
	private static InputStream in= ModelDaoImpl.class.getClassLoader().getResourceAsStream("TeamWorking.properties");
	private static Properties properties;

	static{
		System.out.println(ModelDaoImpl.class.getClassLoader().getResource("geomodel.properties"));
		properties = new Properties();
		try {
			properties.load(in);
			String host = properties.getProperty("host");
			String port = properties.getProperty("port");
			System.out.println("开始连接");
			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

			builder.connectionsPerHost(10);
			builder.connectTimeout(10000);
			builder.maxWaitTime(120000);
			builder.socketKeepAlive(false);
			builder.cursorFinalizerEnabled(true);
			builder.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
			builder.writeConcern(WriteConcern.SAFE);
			MongoClientOptions options = builder.build();

			client = new MongoClient(host,options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initMongoDB(){
		properties = new Properties();
		try {
			properties.load(in);
			String host = properties.getProperty("host");
			String port = properties.getProperty("port");

			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

			builder.connectionsPerHost(10);
			builder.connectTimeout(10000);
			builder.maxWaitTime(120000);
			builder.socketKeepAlive(false);
			builder.cursorFinalizerEnabled(true);
			builder.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
			builder.writeConcern(WriteConcern.SAFE);
			MongoClientOptions options = builder.build();

			client = new MongoClient(host,options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String GetModelTree() {
		return null;
	}

	@Override
	public MongoDatabase GetDB(String dbName) {
		// TODO Auto-generated method stub
		if (dbName != null && !"".equals(dbName)) {
			if(client==null){
				initMongoDB();
			}
			MongoDatabase database = client.getDatabase(dbName);
			return database;
		}
		return null;
	}

	@Override
	public MongoCollection<Document> GetCollection(String dbName, String collName) {
		// TODO Auto-generated method stub
		if (null == collName || "".equals(collName)) {
			return null;
		}
		if (null == dbName || "".equals(dbName)) {
			return null;
		}
		MongoCollection<Document> collection = GetDB(dbName).getCollection(collName);
		return collection;
	}

	@Override
	public boolean CreateCollection(String collectionName) {
		return false;
	}

	public boolean InsertDoc(Document doc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean DeleteDocById(String Id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean UpdateDocById(String Id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Document RetrieveDocsByName(MongoCollection<Document> coll, String modelName) {
		// TODO 返回整个Collection的document
		Document docs = null;
		try {
			docs = coll.find(Filters.eq("name", modelName)).first();
		} catch (Exception e) {
			return null;
		}
		return docs;

	}



	@Override
	/*
	 * 根据主键id查document
	 * @see com.geomodel.com.dao.IModelDao#RetrieveDocById(com.mongodb.client.MongoCollection, java.lang.String)
	 */
	public Document RetrieveDocById(MongoCollection<Document> coll, String id) {
		// TODO Auto-generated method stub
		if(client==null){
			initMongoDB();
		}
		Document myDoc = null;
		try {
			myDoc = coll.find(Filters.eq("UID", id)).first();
		} catch (Exception e) {
			return null;
		}
		return myDoc;
	}

	@Override
	/*
	 *条件查询
	 * @see com.geomodel.com.dao.IModelDao#RetrieveDocsByFilter(com.mongodb.client.MongoCollection, org.bson.conversions.Bson)
	 */
	public MongoCursor<Document> RetrieveDocsByFilter(
            MongoCollection<Document> coll, Bson filter) {
		// TODO Auto-generated method stub
		return coll.find(filter).iterator();
	}

	@Override
	/*
	 * 根据一个字段匹配一个文档
	 * 参数：col，数据库字段名称，根本匹配的字段名称
	 * */
	public Document RetrieveDocByOneField(
            MongoCollection<Document> coll, String dbFieldName,
            String fieldName) {
		// TODO Auto-generated method stub
		if(client==null){
			initMongoDB();
		}
		Document myDoc = null;
		try {
			myDoc = coll.find(Filters.eq(dbFieldName, fieldName)).first();
		} catch (Exception e) {
			return null;
		}
		return myDoc;
	}

	@Override
	public MongoCursor<Document> RetrieveDocsLimit(MongoCollection<Document> col, Bson filter, BasicDBObject nameFilter, int page) {
		if(filter==null){
			return col.find().sort(nameFilter).limit(10).skip((page-1)*10).iterator();
		}else{
			return col.find(filter).sort(nameFilter).limit(10).skip((page-1)*10).iterator();
		}
	}

	@Override
	public MongoCursor<Document> RetrieveDocsLimitByTime(MongoCollection<Document> col, Bson filter, BasicDBObject timeFilter, int page) {
		return col.find(filter).sort(timeFilter).limit(10).skip((page-1)*10).iterator();
	}

	public BasicDBObject getSort(String sortType,int asc){
		BasicDBObject sortObj = new BasicDBObject();
		if(sortType.equals("name")){
			sortObj.append("Name",asc);
		}else if(sortType.equals("time")){
			sortObj.append("CreateTime",asc);
		}else{
			sortObj.append("LoadCount",-1);
		}
		return sortObj;
	}

	@Override
	public Boolean ping(String ipAddress, int pingTimes, int timeOut) {
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
		String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;

		try {
			System.out.println(pingCommand);
			Process p = r.exec(pingCommand);
			if (p == null) {
				return false;
			}
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
			int connectedCount = 0;
			String line = null;
			while ((line = in.readLine()) != null) {
				connectedCount += getCheckResult(line);
			}   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
			return connectedCount == pingTimes;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int getCheckResult(String line) {
		Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",    Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			return 1;
		}
		return 0;
	}
}
