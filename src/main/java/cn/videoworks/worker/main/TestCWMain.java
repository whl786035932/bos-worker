package cn.videoworks.worker.main;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

public class TestCWMain {
	private static Logger logger = LoggerFactory.getLogger(TestCWMain.class);

	public static void main(String[] args) {
		try {
			String filePath = args[0];
			logger.info("要上传的文件的地址是=" + filePath);
			// Create a minioClient with the Minio Server name, Port, Access key and Secret
			// key.
			MinioClient minioClient = new MinioClient("http://10.215.78.77:9000", "1JN9R2TGBWQLUQ15IZK6",
					"wg76RF8mi7XkDoIS73XaZfZDCTL1sq1Pa+XstKnS");

			// Check if the bucket already exists.
			boolean isExist;
			try {
				isExist = minioClient.bucketExists("videoworks-0");
				if (isExist) {
					System.out.println("Bucket already exists.");
				} else {
					// Make a new bucket called asiatrip to hold a zip file of photos.
					minioClient.makeBucket("videoworks-0");
				}

				// Upload the zip file to the bucket with putObject
				minioClient.putObject("videoworks-0", "testcwlast", filePath);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info(
					"/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
		} catch (MinioException e) {
			System.out.println("Error occurred: " + e);
		}
	}
}
