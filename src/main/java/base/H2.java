package base;

import dictionary.BaseRequest;
import dictionary.TableName;
import lombok.extern.slf4j.Slf4j;
import model.Constant;
import model.Participant;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class H2 {
    private final String JDBC_DRIVER = "org.h2.Driver";
    private final String DB_URL = "jdbc:h2:~/constants";
    private final String USER = "admin";
    private final String PASS = "12345";

    private static H2 instance;

    private H2() {
        if (!isTableExist(TableName.CONSTANT.name())) {
            createTable(BaseRequest.CREATE_CONSTANT.getText());
        }
        if (!isTableExist(TableName.CHANNEL.name())) {
            createTable(BaseRequest.CREATE_CHANNEL.getText());
        }
        if (!isTableExist(TableName.PARTICIPANT.name())) {
            createTable(BaseRequest.CREATE_PARTICIPANT.getText());
        }
    }

    public static H2 getInstance() {
        if (instance == null) {
            instance = new H2();
        }
        return instance;
    }

    public long insertNewConstant() {
        long idConstant = -1;
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.INSERT_CONSTANT.getText(),
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, null);
            preparedStatement.setDate(2, null);
            preparedStatement.setDate(3, null);
            preparedStatement.setInt(4, 0);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                idConstant = generatedKeys.getLong(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return idConstant;
    }

    public void insertNewChannel(Long telegramId, Long constantId) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.INSERT_CHANNEL.getText());
            preparedStatement.setLong(1, telegramId);
            preparedStatement.setLong(2, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantText(Long constantId, String text) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_TEXT.getText());
            preparedStatement.setString(1, text);
            preparedStatement.setLong(2, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantStart(Long constantId, LocalDateTime start) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_START.getText());
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            preparedStatement.setLong(2, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantEnd(Long constantId, LocalDateTime end) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_END.getText());
            preparedStatement.setTimestamp(1, Timestamp.valueOf(end));
            preparedStatement.setLong(2, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantWinners(Long constantId, int count) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_WINNERS.getText());
            preparedStatement.setInt(1, count);
            preparedStatement.setLong(2, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantStarted(int constantId) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_STARTED.getText());
            preparedStatement.setInt(1, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateConstantFinished(int constantId) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(BaseRequest.UPDATE_CONSTANT_FINISHED.getText());
            preparedStatement.setInt(1, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Constant> getAllConstant() {
        List<Constant> constants = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(BaseRequest.GET_ALL_CONSTANT.getText());
            while (resultSet.next()) {
                constants.add(createConstant(resultSet));
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return constants;
    }

    public int getExistConstantIdForNow(Timestamp time, String request) {
        int id = 0;
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setTimestamp(1, time);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return id;
    }

    public Constant getConstantById(long id) {
        Constant constant = null;
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.GET_CONSTANT_BY_ID.getText());
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                constant = createConstant(resultSet);
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return constant;
    }

    public List<Long> getChannelsForConstantById(long constantId) {
        List<Long> channels = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.GET_CHANNEL_BY_CONSTANT_ID.getText());
            preparedStatement.setLong(1, constantId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                channels.add(resultSet.getLong(1));
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return channels;
    }

    public HashMap<Long, Integer> getChannelsAndMessagesForConstantById(long constantId) {
        HashMap<Long, Integer> data = new HashMap<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.GET_CHANNEL_AND_MESSAGE_BY_CONSTANT_ID.getText());
            preparedStatement.setLong(1, constantId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                data.put(resultSet.getLong(1), resultSet.getInt(2));
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return data;
    }

    public void updateChannelMessageId(long telegramChanelId, long messageId) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.UPDATE_CHANNEL_BY_MESSAGE_ID.getText());
            preparedStatement.setLong(1, messageId);
            preparedStatement.setLong(2, telegramChanelId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public long getConstantByTelegramChannel(Long telegramId) {
        long id = 0;
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.GET_CONSTANT_BY_TELEGRAM_CHANNEL_ID.getText());
            preparedStatement.setLong(1, telegramId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return id;
    }

    public List<Participant> getAllParticipantsByConstantId(Long constantId) {
        List<Participant> participants = new ArrayList<>();
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.GET_PARTICIPANTS_BY_CONSTANT_ID.getText());
            preparedStatement.setLong(1, constantId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Participant participant = new Participant();
                participant.setId(resultSet.getLong(1));
                participant.setName(resultSet.getString(2));
                participant.setUsername(resultSet.getString(3));
                participants.add(participant);
            }
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return participants;
    }

    public boolean isExitUsername(Integer userId, Long constantId) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement statement = connection.prepareStatement(
                    BaseRequest.GET_PARTICIPANT_BY_TELEGRAM_ID_AND_CONSTANT.getText());
            statement.setInt(1, userId);
            statement.setLong(2, constantId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public void insertParticipant(Participant participant) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    BaseRequest.INSERT_PARTICIPANT.getText());
            preparedStatement.setString(1, participant.getName());
            preparedStatement.setString(2, participant.getUsername());
            preparedStatement.setLong(3, participant.getTelegramId());
            preparedStatement.setLong(4, participant.getConstantId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void deleteByConstantId(Long constantId, String request) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setLong(1, constantId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void deleteAllByConstantId(long constantId) {
        deleteByConstantId(constantId, BaseRequest.DELETE_PARTICIPANT_BY_CONSTANT_ID.getText());
        deleteByConstantId(constantId, BaseRequest.DELETE_CHANNEL_BY_CONSTANT_ID.getText());
        deleteByConstantId(constantId, BaseRequest.DELETE_CONSTANT_BY_ID.getText());
    }

    public void deleteBase() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement preparedStatement = connection.createStatement();
            preparedStatement.executeUpdate(BaseRequest.DELETE.getText());
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    public int getCountParticipant() {
//        Connection connection = null;
//        try {
//            Class.forName(JDBC_DRIVER);
//            connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery(BaseRequest.GET_ALL.getText());
//            if (resultSet.next()) {
//                resultSet.last();
//                return resultSet.getRow() + 1115;
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    log.error(e.getMessage());
//                }
//            }
//        }
//        return 1115;
//    }

    private void createTable(String request) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            statement.executeUpdate(request);
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isTableExist(String tableName) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(BaseRequest.SHOW_TABLES.getText());
            while (rs.next()) {
                if (rs.getString(1).equals(tableName)) {
                    return true;
                }
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    private Constant createConstant(ResultSet resultSet) {
        Constant constant = new Constant();
        try {
            constant.setId(resultSet.getLong(1));
            constant.setText(resultSet.getString(2));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String start = resultSet.getString(3);
            if (start != null) {
                constant.setStart(LocalDateTime.parse(start, formatter));
            } else {
                constant.setStart(null);
            }
            String end = resultSet.getString(4);
            if (end != null) {
                constant.setEnd(LocalDateTime.parse(end, formatter));
            } else {
                constant.setEnd(null);
            }
            constant.setCountWinners(resultSet.getInt(5));
            constant.setStarted(resultSet.getBoolean(6));
            constant.setEnded(resultSet.getBoolean(7));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return constant;
    }
}
