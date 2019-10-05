import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser {
    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306/rmDdcKsJdO", "rmDdcKsJdO", "Npt1c7ppww");
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    public static void main(String[] args) {
        String url = "https://hotline.ua/computer/usb-flash-drajvy/";
        int i=0;
        Document document = null;
        try {
            Statement statement = null;
            Connection dbConnection = getDBConnection();

            document = Jsoup.connect(url).userAgent("Mozilla").get();
            Elements links = document.select("p.h4 a");
            System.out.println(links.size());
            String tbId= "SELECT MAX(id) FROM products";
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(tbId);
            while (rs.next()){
             String max= rs.getString("MAX(id)");
             if (max!=null){
                 i= Integer.parseInt(max);
             }
            }
            System.out.println(i);

            for (Element item : links) {
                i++;
                String link = "https://hotline.ua" + item.attr("href");
                Document page = Jsoup.connect(link).userAgent("Mozilla").get();

                Elements name = page.select("h1");
                Elements description = page.select(".resume-description .text p");
                Elements prices = page.select(".resume-price  span.value");
                Elements images = page.select(".zg-canvas-img");


                String desc=description.text().split("\\.\\.\\.")[0];
                String price =prices.text().split(" – ")[0].replaceAll("\\s+","");
                System.out.println(name.text() + '\n' + desc + '\n' + price);
                String image=images.attr("src");
                System.out.println(image);

                statement = dbConnection.createStatement();
                String insertTb = "INSERT INTO `products`" +
                        "(`article`, `category_id`, `name`, `description`, `price`,  `user_id`) " +
                        "VALUES ('"+name.text()+"','17','"+name.text()+"','"+desc+"',"+price+",'2')";

                // выполнить SQL запрос
                System.out.println(insertTb);
                statement.executeUpdate(insertTb);
                System.out.println("Insert is created!");
                statement = dbConnection.createStatement();
                String insertImg = "INSERT INTO `img_products`" +
                        "(`key`, `name`, `type`, `size`) " +
                        "VALUES ('"+i+"','"+image+"','url','0')";
                statement.executeUpdate(insertImg);
                System.out.println("Image Insert");
                }

        } catch (IOException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
