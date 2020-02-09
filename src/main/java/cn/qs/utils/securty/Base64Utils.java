package cn.qs.utils.securty;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * Base64就是一种基于64个可打印字符来表示二进制数据的方法。
 * 
 * @author Administrator
 *
 */
public class Base64Utils {

	public static String encode(String str) {
		byte[] encode = Base64.encodeBase64(str.getBytes());
		return new String(encode);
	}

	public static String decode(String str) {
		byte[] decode = Base64.decodeBase64(str);
		return new String(decode);
	}

	public static String encodeFile(File file) throws IOException {
		byte[] readFileToByteArray = FileUtils.readFileToByteArray(file);
		return Base64.encodeBase64String(readFileToByteArray);
	}

	public static String encodeFile(String filePath) throws IOException {
		return encodeFile(new File(filePath));
	}

	public static void decodeFile(String codes, File file) throws IOException {
		byte[] decodeBase64 = Base64.decodeBase64(codes);
		FileUtils.writeByteArrayToFile(file, decodeBase64);
	}

	public static void decodeFile(String codes, String filePath) throws IOException {
		decodeFile(codes, new File(filePath));
	}

	public static void main(String[] args) throws IOException {
		String encodeFile = encodeFile("F:/1.png");
		System.out.println(encodeFile);

		decodeFile(encodeFile, "F:/12.png");
	}
}
