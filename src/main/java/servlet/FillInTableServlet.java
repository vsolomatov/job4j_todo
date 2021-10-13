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
import java.util.ArrayList;
import java.util.List;

public class FillInTableServlet extends HttpServlet  {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean showAll = Boolean.parseBoolean(req.getParameter("showAll"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        HttpSession httpSession = req.getSession();
        User user = (User) httpSession.getAttribute("user");

        List<Item> itemList;
        if (showAll) {
            itemList = new ArrayList<>(PsqlStore.instOf().findAllItems(user));
        } else {
            itemList = new ArrayList<>(PsqlStore.instOf().findNotDoneItems(user));
        }
        String items = objectMapper.writeValueAsString(itemList);
        resp.getWriter().write(items);
    }
}

