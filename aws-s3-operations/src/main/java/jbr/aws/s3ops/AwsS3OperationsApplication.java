package jbr.aws.s3ops;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.opencsv.CSVWriter;

import jbr.aws.s3ops.model.ProductModel;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Main class.
 * 
 * @author Ranjith Sekar
 * @since 2021-Jun-22
 */
@SpringBootApplication
@Slf4j
public class AwsS3OperationsApplication implements CommandLineRunner {

  @Value("${product-data-file-name}")
  private String productDataFileName;

  @Value("${access-key}")
  private String accessKey;

  @Value("${secret-key}")
  private String secretKey;

  @Value("${session-token}")
  private String sessionToken;

  @Value("${s3-bucket-name}")
  private String s3BucketName;

  public static void main(String[] args) {
    SpringApplication.run(AwsS3OperationsApplication.class, args);
  }

  @Override
  public void run(String... args) {
    String command = args[0];

    if (command.equals("csvgen")) { // Fetch data from DB and convert to CSV.
      log.info("command: " + command);

      List<String[]> voteDataStringArray = new ArrayList<>();
      getProdutData().forEach(product ->
        {
          String[] data = { product.getId()
              .toString(), product.getName(), product.getCategory(), product.getPrice() };
          voteDataStringArray.add(data);
        });

      try (CSVWriter writer = new CSVWriter(new FileWriter(productDataFileName))) {
        writer.writeAll(voteDataStringArray);
        log.info("Csv File generated successfuly.");
      } catch (Exception e) {
        log.error("Csv generation failed: " + e.getMessage());
      }
    } else if (command.equals("s3copy")) { // Copy csv to S3 bucket.
      log.info("command: " + command);
      log.info("accessKey: " + accessKey);
      log.info("secretKey: " + secretKey);
      log.info("sessionToken: " + sessionToken);
      log.info("s3BucketName: " + s3BucketName);

      try {
        AWSSessionCredentials credentials = new BasicSessionCredentials(accessKey, secretKey, sessionToken);
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_WEST_2)
            .build();
        s3client.putObject(s3BucketName, accessKey, new File(productDataFileName));
        log.info("Csv file copied to S3 successfuly.");
      } catch (Exception e) {
        log.error("Csv file copy failed. " + e.getMessage());
      }
    }
  }

  private static List<ProductModel> getProdutData() {
    return new ArrayList<>(Arrays.asList(new ProductModel(100L, "Redmi 9", "Mobile", "8000"),
        new ProductModel(200L, "Dell Vostro", "Laptop", "80000")));
  }
}
