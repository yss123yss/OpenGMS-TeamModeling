package Management.Service;

import ChatRoom.Dao.ModelDaoImpl;
import Management.Dao.UserDaoImpl;
import Management.Domain.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.jsoup.helper.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceImpl implements IUserService {

    private static UserDaoImpl userDao = new UserDaoImpl();
    private static Properties properties = new Properties();
    private static InputStream inputStream = ModelDaoImpl.class.getClassLoader().getResourceAsStream("TeamWorking.properties");


    static {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String databaseName = properties.getProperty("databaseName");
    private static String collectionName = properties.getProperty("userCollectionName");
    @Override
    public String userRegister(User user) {

        //从配置文件中获取数据库名和表名
        MongoCollection<Document> mongoCollection = userDao.getCollection(databaseName, collectionName);
//        String registerStatus=null;
        //查询a和mobilePhone是否已被注册过
        String registerStatus = checkUserExist(mongoCollection, user.getEmail(), user.getUserName());
        String joinedGroupStr="";
        if (registerStatus.equals("success") ) {
            try {
                Document document = new Document("userName", user.getUserName()).append("email", user.getEmail()).append("password", user.getPassword()).append("joinedGroup", joinedGroupStr)
                        .append("mobilePhone", user.getMobilePhone()).append("gender", user.getGender()).append("jobTitle", user.getJobTitle())
                        .append("country", user.getCountry()).append("city", user.getCity()).append("organization", user.getOrganization())
                        .append("introduction", user.getIntroduction()).append("direction", user.getDirection()).append("homePage", user.getHomePage()).append("avatar", user.getAvatar());
                System.out.println(document.toString());
                mongoCollection.insertOne(document);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return registerStatus;
    }

    @Override
    public boolean userLogin(User user) {
        MongoCollection<Document> mongoCollection = userDao.getCollection(databaseName, collectionName);
        if (checkUserExist(mongoCollection, user.getEmail(), user.getUserName()).equals("success") ) {

        } else {
            //TODO
            System.out.println("This account does not exist.");
        }
        return false;
    }

    /*验证用户是否存在，设计时想的是账户既可以是邮箱，也可以是手机，现规定账号只能是邮箱*/
    @Override
    public String checkUserExist(MongoCollection<Document> collection, String email, String userName) {
        // TODO Auto-generated method stub
        String registerResult = null;
        if (userDao.RetrieveDocByOneField(collection, "email", email) != null) {
            registerResult = "email";
        } else if (userDao.RetrieveDocByOneField(collection, "userName", userName) != null) {
            registerResult = "username";
        } else {
            registerResult = "success";
        }
        return registerResult;
    }

    /*验证账号是否是邮箱，是则返回true，不是返回false*/
    @Override
    public boolean checkEmailFormat(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /*验证密码是否正确*/
    @Override
    public Document checkPassword(String email, String password) {
        MongoCollection<Document> mongoCollection = userDao.getCollection(databaseName, collectionName);
        Document document = new Document("email", email).append("password", password);


        ArrayList<Document> documentList = new ArrayList<>();
        MongoCursor mongoCursor = userDao.RetrieveDocsByFilter(mongoCollection, document);
        while (mongoCursor.hasNext()) {

            documentList.add((Document) mongoCursor.next());

        }
        if (documentList.size() == 1) {
            System.out.println("One documents found in collection " + collectionName);
            return documentList.get(0);
        } else if (documentList.size() < 1) {
            System.out.println("No document found in collection " + collectionName);
        } else {
            System.out.println("Two more documents found in collection " + collectionName);
        }
        return null;

    }

}
