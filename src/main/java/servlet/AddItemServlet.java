package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Item;
import model.User;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AddItemServlet extends HttpServlet  {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AddItemServlet.doGet");
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String description = req.getParameter("description");
        String[] cIds = req.getParameterValues("cIds");

        HttpSession httpSession = req.getSession();
        User user = (User) httpSession.getAttribute("user");

        Item item = new Item(description, user);

        // Сохраняем в базе нашу задачу
        PsqlStore.instOf().addNewItem(item, cIds);

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        // Просто какой-то ответ, созданный через ObjectMapper (если передать просто строку, то не работает)
        ObjectMapper objectMapper = new ObjectMapper();
        String rs = objectMapper.writeValueAsString("The task created!");

        resp.getWriter().write(rs);
        resp.sendRedirect(req.getContextPath() + "/items.do");
    }

}

