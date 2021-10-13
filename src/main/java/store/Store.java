package store;

import model.Category;
import model.Item;
import model.User;

import java.util.Collection;
import java.util.List;

public interface Store {

    Collection<Item> findAllItems(User user);

    Collection<Item> findNotDoneItems(User user);

    void saveItem(Item item);

    Item findByItemId(int id);

    User findUserByEmail(String email);

    void saveUser(User user);

    List<Category> allCategories();

    void addNewItem(Item item, String[] ids);

}
