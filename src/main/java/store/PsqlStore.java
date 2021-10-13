package store;


import model.Category;
import model.Item;
import model.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class PsqlStore implements Store, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger(PsqlStore.class.getName());

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();

    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    private static final Store INST = new PsqlStore();

    private PsqlStore() {
    }

    public static Store instOf() {
        return INST;
    }

    @Override
    public void saveItem(Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.update(item);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Collection<Item> findAllItems(User user) {
        return this.tx(
                session -> session.createQuery(
                        "select distinct i from Item i join fetch i.categories "
                                + "where i.user.id = :user_id order by i.id")
                        .setParameter("user_id", user.getId()).getResultList()
        );
    }

    @Override
    public Collection<Item> findNotDoneItems(User user) {
        return this.tx(
                session -> session.createQuery(
                        "select distinct i from Item i join fetch i.categories "
                                + "where i.done != true and i.user.id = :user_id order by i.id")
                        .setParameter("user_id", user.getId()).getResultList()
        );
    }

    @Override
    public Item findByItemId(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Item result = session.get(Item.class, id);
        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User findUserByEmail(String email) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        User rs = null;
        try {
            final Query query = session.createQuery("from User where email = :email");
            query.setParameter("email", email);
            rs = (User) query.getSingleResult();
            tx.commit();
            return rs;
        } catch (final Exception e) {
            LOGGER.info("User with email: " + email + " not found!");
            return rs;
        } finally {
            session.close();
        }
    }

    @Override
    public void saveUser(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    private void create(User user) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    private void update(User user) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    public void addNewItem(Item item, String[] ids) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();

            for (String id : ids) {
                Category category = session.find(Category.class, Integer.parseInt(id));
                item.addCategory(category);
            }
            session.save(item);

            session.getTransaction().commit();
        } catch (Exception e) {
            sf.getCurrentSession().getTransaction().rollback();
        }
    }

    @Override
    public List<Category> allCategories() {
        List<Category> rsl = new ArrayList<>();
        try (Session session = sf.openSession()) {
            session.beginTransaction();

            rsl = session.createQuery("select c from Category c", Category.class).list();

            session.getTransaction().commit();
        } catch (Exception e) {
            sf.getCurrentSession().getTransaction().rollback();
        }
        return rsl;
    }

}