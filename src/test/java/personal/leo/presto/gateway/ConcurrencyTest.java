package personal.leo.presto.gateway;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyTest {

    @Test
    public void test() throws SQLException, ExecutionException, InterruptedException {

        final int poolSize = 10;
        final ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                String url = "jdbc:presto://localhost:8080";
                try {
                    Connection connection = DriverManager.getConnection(url, "root", null);
                    final Statement statement = connection.createStatement();
                    final ResultSet resultSet = statement.executeQuery("select count(*) from hive.ads.chengpei_order");
                    resultSet.next();
                    return resultSet.getString(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }, pool);
            futures.add(future);
        }

        for (CompletableFuture<String> future : futures) {
            System.out.println(future.get());
        }


    }
}
