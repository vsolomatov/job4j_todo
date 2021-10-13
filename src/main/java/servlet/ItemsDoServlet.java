package servlet;

import model.Category;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ItemsDoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = PsqlStore.instOf().allCategories();
        req.setAttribute("allCategories", categories);
        req.getRequestDispatcher("items.jsp").forward(req, resp);
    }
}
