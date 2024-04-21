package ru.gb_spring.clientresourceserver.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gb_spring.clientresourceserver.model.User;

import java.util.List;

/**
 * The type User repository.
 */
@Repository
public class UserRepository {

    private final SqlRequest sqlRequest;
    private final JdbcTemplate jdbc;

    /**
     * Instantiates a new User repository.
     *
     * @param sqlRequest the sql request
     * @param jdbc       the jdbc
     */
    public UserRepository(SqlRequest sqlRequest, JdbcTemplate jdbc) {
        this.sqlRequest = sqlRequest;
        this.jdbc = jdbc;
    }

    /**
     * Получение списка пользователей из базы данных
     *
     * @return список пользователей
     */
    public List<User> findAll() {
        RowMapper<User> userRowMapper = (r, i) -> {
            User rowObject = new User();
            rowObject.setId(r.getInt("id"));
            rowObject.setFirstName(r.getString("firstName"));
            rowObject.setLastName(r.getString("lastName"));
            return rowObject;
        };
        return jdbc.query(sqlRequest.getFindAll(), userRowMapper);
    }

    /**
     * Сохранение пользователя в базу
     *
     * @param user пользователь
     */
    public void save(User user) {
        jdbc.update(sqlRequest.getSave(), user.getFirstName(), user.getLastName());
    }

    /**
     * Удаление пользователя по id
     *
     * @param id идентификатор пользователя
     */
    public void deleteById(int id) {
        jdbc.update(sqlRequest.getDelete(), id);
    }

    /**
     * Получение пользователя из базы по id
     *
     * @param id идентификатор пользователя
     * @return - возвращает пользователя или null если нет записей в базе
     */
    public User getById(int id) {
        if (!isExistUserById(id)) return null;
        return jdbc.queryForObject(sqlRequest.getGetById(),
                (resultSet, rowNum) -> {
                    User newUser = new User();
                    newUser.setId(Integer.parseInt(resultSet.getString("id")));
                    newUser.setFirstName(resultSet.getString("firstName"));
                    newUser.setLastName(resultSet.getString("lastName"));
                    return newUser;
                },
                id);
    }

    /**
     * Обновление пользователя по id
     *
     * @param user пользователь
     */
    public void updateUser(User user) {
        if (isExistUserById(user.getId())) {
            jdbc.update(sqlRequest.getUpdate(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getId());
        }
    }

    /**
     * Проверка существующей записи
     *
     * @param id идентификатор пользователя
     * @return true если есть запись с указанным id
     */
    private boolean isExistUserById(int id) {
        int countRow = jdbc.queryForObject(sqlRequest.getCount(), Integer.class, id);
        return countRow > 0;
    }
}
