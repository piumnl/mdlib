package cn.piumnl.mdlib.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Logger;

import cn.piumnl.mdlib.util.IOUtil;
import cn.piumnl.mdlib.util.StringUtil;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-12.
 */
public class ServerOperated {

    private static final Logger LOGGER = Logger.getLogger(ServerOperated.class.getName());

    private OutputStream client;

    public ServerOperated(Socket client) throws IOException {
        Objects.requireNonNull(client);

        this.client = client.getOutputStream();
    }

    public ServerOperated(OutputStream outputStream) {
        this.client = outputStream;
    }

    public void close() {
        try {
            this.client.close();
            LOGGER.fine(StringUtil.format("{} 离开了 HTTP 服务器！", client));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void opNotFound(String resource) {
        try (PrintWriter out = new PrintWriter(IOUtil.wrapperOut(client));) {
            // 返回应答消息,并结束应答
            out.println("HTTP/1.0 404 NOTFOUND");
            out.println("Content-Type:text/html;charset=UTF-8");
            // 根据 HTTP 协议, 空行将结束头信息
            out.println();
            out.println("对不起，您寻找的资源在本服务器上不存在");
            out.close();

            LOGGER.info(StringUtil.format("Not Found '{}'", resource));
        }
    }

    public void opError(Exception e) {
        try (PrintWriter out = new PrintWriter(IOUtil.wrapperOut(client))) {
            // 返回应答消息,并结束应答
            out.println("HTTP/1.0 500");
            out.println("");
            out.flush();

            LOGGER.info(StringUtil.format("Error: '{}'", e.getMessage()));
        }
    }

    public void opReadFile(Path path, ServerRequest request) throws IOException {
        String contentType = request.getMime();
        File file = path.toFile();

        opReadBinaryFile(file, contentType);
    }

    private void opReadBinaryFile(File file, String contentType) throws IOException {
        try (PrintStream out = new PrintStream(client, true);
             FileInputStream fis = new FileInputStream(file)) {

            // 返回应答消息,并结束应答
            out.print("HTTP/1.0 200 OK\r\n");
            out.print(StringUtil.format("Content-Type: {}\r\n", contentType));
            out.print(StringUtil.format("Content-Length: {}\r\n", file.length()));
            out.print(StringUtil.format("Date: {}\r\n", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC))));
            out.print("Server: Mdlib Static HTTP server\r\n");
            // 根据 HTTP 协议, 空行将结束头信息
            out.print("\r\n");

            byte[] data = new byte[1024];
            int length;
            while ((length = fis.read(data)) != -1) {
                out.write(data, 0, length);
            }
        }
    }
}
