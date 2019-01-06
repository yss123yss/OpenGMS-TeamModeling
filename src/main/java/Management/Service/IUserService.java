package Management.Service;

import Management.Domain.User;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface IUserService {
    /*登录*/
    String userRegister(User user);

    /*注册*/
    boolean userLogin(User user);

    /*验证用户是否存在*/
    String checkUserExist(MongoCollection<Document> collection, String email, String mobilephone);

    /*验证邮箱格式*/
    boolean checkEmailFormat(String account);

    /*验证账号密码*/
    Document checkPassword(String account, String password);
    //ToDO 邮箱权限验证

}
