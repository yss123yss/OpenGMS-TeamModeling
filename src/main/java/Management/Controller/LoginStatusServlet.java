package Management.Controller;

import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginStatusServlet", urlPatterns = "/LoginStatusServlet")
public class LoginStatusServlet extends HttpServlet {
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
        HttpSession session = req.getSession();
        String userName = (String) session.getAttribute("userName");
        String email = (String) session.getAttribute("email");
        String jsonStr = null;
        if (userName != null && userName != "" && email != null && email != "") {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userName", userName);
            jsonObject.put("email", email);
            jsonStr = jsonObject.toString();
            writer.print(jsonStr);
        } else {
            writer.print("message invalid");
        }


    }
}
