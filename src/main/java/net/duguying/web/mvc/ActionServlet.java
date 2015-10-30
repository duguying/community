package net.duguying.web.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by duguying on 2015/10/30.
 */
public class ActionServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=GB2312"); //这条语句指明了向客户端发送的内容格式和采用的字符编码．
        PrintWriter out = response.getWriter();
        out.println("hello"); //利用PrintWriter对象的方法将数据发送给客户端
        out.close();
    }
}
