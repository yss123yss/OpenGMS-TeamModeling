package Management.Controller;

import Management.Domain.User;
import Management.Service.UserServiceImpl;
import Link.LinkDB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.xmlbeans.impl.util.Base64;
import org.bson.Document;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import javax.swing.text.Document;

@WebServlet(name = "LoginServlet", urlPatterns = "/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置对客户端请求进行重新编码的编码
        req.setCharacterEncoding("utf-8");
        //使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据。
        resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
        //指定对服务器响应进行重新编码的编码
        resp.setCharacterEncoding("UTF-8");
        //获取向前台写数据的输出流
        PrintWriter writer = resp.getWriter();
        //获取前台数据流
        UserServiceImpl userService = new UserServiceImpl();
        User user = new User();
        //检查账户（邮箱）格式
        LinkDB linkDB = new LinkDB();
        MongoCollection<Document> col = linkDB.GetCollection("CollaborativeWork", "User");
        if (userService.checkEmailFormat(req.getParameter("email"))) {

            Document document = userService.checkPassword(req.getParameter("email"), req.getParameter("password"));
            if (document != null) {
                user.setUsernName(document.get("userName").toString());
                user.setEmail(document.get("email").toString());
                user.setPassword(document.get("password").toString());

                // 密码验证成功，将用户数据放入到Session中
                HttpSession session = req.getSession();
                session.setMaxInactiveInterval(30 * 60);//设置session过期时间 为30分钟
                session.setAttribute("userName", user.getUserName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("password", user.getPassword());
//                writer.print(session.getAttribute("userName"));
                JSONObject userSecretData = JSONObject.fromObject(document.toJson());
                userSecretData.remove("avatar");

                JSONObject userData = new JSONObject();
                userData.put("username", document.get("userName").toString());
                userData.put("avatar", document.get("avatar").toString());
//                userData.put("token", createJWT("000", "NNU", 30 * 60 * 1000, userSecretData));
//                String avatar=document.get("avatar").toString();
                writer.write(userData.toString());
            } else {
                writer.write("invalid");
            }
        } else {
            writer.write("invalid");
        }
    }

    private static final String JWT_SECERT = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOG4rnfT7nWAgTtZcWkyQgqbaeg+YKaPeijKakVwyC1P5vxpLrSCT/U8dBRiav3zbcA8xvtgFr1KS3f0V5w1skKGlY9+qhzCOelzLuezQQD3Vt6Ig1fuPJIxCNfhCs+tqYsf+5UUVep+jX30l9z/ocym1NoY6OMmpDR+MLrWvuKvAgMBAAECgYEArgOOnVqJT5TODE8cG5lfSIYf06ejI0UhTCkgXIBiEmyzCoycMRT1R35fjweArF7qkXVJganGXg/UtplsoUTUMvpzYeyOVf/4Ldnmwp3w/v1KxXv0SdcAt8hcbE4geK0NPrqajYUaRA0mr72vlx1CjmPeYssCtm1MfLK7QzUgJsECQQD4DttDhnW34L7ASQNXY0u1p3rpCAOUBxxRs7dNoa4O+VBJy/0K3rSn2wfvxwDXTOwPUtldHcJYV2t6jis5ll9nAkEA6PK/qYkZYOZxZuQ1CmRsCdbGeXiVk7xOkWw/y5aojcaGeeF7YV4IOCE9OiSuxirIhZuL/DI+DUwQ04UaM6X9eQJBANinYSqhDcAEM5aOLrTsrPuDw/40Wau4aiuR48+SHwxFBLLvz5rm2LKiw1PRZBKnxKW9nsuNamjuMZlhAq/RfpECQATEOtHAQWmvLXDaiI9O4LsOzv7bTcw5FS56lY+X5JkKD+Rcjca/QtHLNFKFP6JEa9f4B9RAPu7MuKdrWM7nvZkCQBuPQ9p6eUsL8sO8UkaYCJq5HVKUONtaJ+czoSKEeKG4PL4VKtIgw23OCZ7tL0UXYQKVQBhVflU8snSj4OJJbI8=";

    /**
     * 创建key
     */
    private static SecretKey generalKey() {
        byte[] encodeKey = Base64.decode(JWT_SECERT.getBytes());
        SecretKey key = new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");
        return key;
    }

    /**
     * 签发JWT
     *
     * @param jti           jwt的唯一身份标识,主要用来作为一次性token,从而回避重放攻击
     * @param sub           jwt所面向的用户
     * @param expiredTimeAt 过期时间(当前时间ms+要过期时间ms),单位ms
     */
    public static String createJWT(String jti, String sub, long expiredTimeAt, JSONObject userdata) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        Date now = new Date(System.currentTimeMillis());
        Date end = new Date(now.getTime() + expiredTimeAt);

        Map<String, Object> claims = new HashMap<>();
        claims.put("state","true");

        JwtBuilder builder = Jwts.builder()
//                .setId(jti)                                      // JWT_ID
//                .setAudience("")                                // 接受者
//                .setClaims(userdata)                                // 自定义属性
//                .setSubject(sub)                                 // 主题
//                .setIssuer("issuer")                                  // 签发者
//                .setIssuedAt(now)// 签发时间
//                .setNotBefore(end)                       // 失效时间
                .setClaims(claims)
                .signWith(signatureAlgorithm,secretKey);           // 签名算法以及密钥
        String token = builder.compact();
        return token;
    }
}
