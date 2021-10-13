package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Item;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangeItemServlet extends HttpServlet  {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Boolean done = Boolean.valueOf(req.getParameter("done"));

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");

        // Находим нашу задачу
        Item item = PsqlStore.instOf().findByItemId(Integer.parseInt(id));
        // Обновляем поле done
        item.setDone(done);
        // Сохраняем в базе нашу задачу
        PsqlStore.instOf().saveItem(item);

        // Просто какой-то ответ, созданный через ObjectMapper (если передать просто строку, то не работает)
        ObjectMapper objectMapper = new ObjectMapper();
        String rs = objectMapper.writeValueAsString("The task #" + id + " is " + (done ? "" : "not ") + "done!");

        resp.getWriter().write(rs);
    }

}

