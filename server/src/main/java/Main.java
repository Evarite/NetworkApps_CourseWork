import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Statement statement = null;
        ResultSet resultSet = null;

        try (Connection connection = DatabaseManager.getConnection()) {
            statement = connection.createStatement();

            String selectSql = "select * from account";
            resultSet = statement.executeQuery(selectSql);
            while(resultSet.next()) {
                Account acc = new Account(resultSet.getInt("id"),
                        resultSet.getString("email"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("password_hash"),
                        resultSet.getDate("birth_date"));

                System.out.println(acc);
            }
        }
        catch (SQLException e) {
            System.out.println("Пад час працы з базай дадзеных была атрымленая памылка");
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null)
                    statement.close();
                if(resultSet != null)
                    resultSet.close();
            }
            catch (SQLException e) {
                System.out.println("Пад час зачыненьня базы дадзеных была атрымленая памылка");
                e.printStackTrace();
            }
        }
    }
}
