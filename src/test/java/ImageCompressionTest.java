import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanghz.myutil.httpclient.HttpClientUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author whz
 * @date 2024/3/11 10:14
 */
public class ImageCompressionTest {
    private static String path1 = "d:/tmp/pic/";
    private static String path2 = "d:/tmp/newpic/";


    @Test
    public void test1() throws IOException {
        String csv = """
                716	http://localhost/test.jpg
                 """;
        String[] strArr = csv.split("\n");
        for (String s : strArr) {
            String[] rowData = s.split("\t");
            String url = rowData[1];
            String[] file = url.split("/");
            String filename = file[file.length - 1];
            /*if (!filename.contains("jpeg")
                    && !filename.contains("jpg")
                    && !filename.contains("png")
                    && !filename.contains("PNG")
                    && !filename.contains("JPEG")
                    && !filename.contains("JPG")) {
                filename = filename + ".jpg";
            }*/

            InputStream inputStream = HttpClientUtils.downloadFile(url);
            File file1 = new File(path1 + filename);
            FileUtils.copyInputStreamToFile(inputStream, file1);
            compressionPicPath(file1, filename);

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("file", FileUtil.file(path2 + filename));
            HttpResponse result = HttpUtil.createPost("http://localhost/imageup")
                    .header("token", "abc")
                    .form(paramMap).execute();

            JSONObject jsonObject = JSON.parseObject(result.body());

            System.out.println(s + "\t" + jsonObject.getString("value"));
        }
    }

    public static void compressionPicPath(File file, String filename) throws IOException {
        // 小于100KB不压缩
        if (file.length() < 100 * 1024) {
            FileUtils.copyFile(file, new File(path2 + filename));
            return;
        }
        Thumbnails.of(file).scale(0.5f).toOutputStream(Files.newOutputStream(Paths.get(path2 + filename)));//按比例缩小.
    }
}
