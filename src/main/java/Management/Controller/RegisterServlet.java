package Management.Controller;

import Management.Domain.User;
import Management.Service.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "RegisterServlet", urlPatterns = "/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    String host = "222.192.7.75";
    Integer port = 27017;
    String databaseName = "CollaborativeWork";
    String collectionName = "User";

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
        //从前台获得用户信息
        User user = new User();
        String userName = req.getParameter("userName");
        user.setUsernName(userName);
        String email = req.getParameter("email");
        user.setEmail(email);
        String password = req.getParameter("password");
        user.setPassword(password);
        String mobilePhone = req.getParameter("mobilePhone");
        user.setMobilePhone(mobilePhone);
        String gender = req.getParameter("gender");
        user.setGender(gender);
        String jobTitle = req.getParameter("jobTitle");
        user.setJobTitle(jobTitle);
        String country = req.getParameter("country");
        user.setCountry(country);
        String city = req.getParameter("city");
        user.setCity(city);
        String organization = req.getParameter("organization");
        user.setOrganization(organization);
        String introduction = req.getParameter("introduction");
        user.setIntroduction(introduction);
        String direction = req.getParameter("direction");
        user.setDirection(direction);
        String homePage = req.getParameter("homePage");
        user.setHomePage(homePage);
        String avatar = req.getParameter("avatar");
        user.setAvatar(avatar);
        //存入数据库
        UserServiceImpl userService = new UserServiceImpl();
        //发送消息到前台
        writer.print(userService.userRegister(user));
        writer.flush();
        writer.close();

    }
}
