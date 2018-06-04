package cn.piumnl.mdlib.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-12.
 */
public class ServerResponse {

    private ServerContext context;

    private ServerOperated operated;

    public ServerResponse(OutputStream outputStream, ServerContext context) {
        this.operated = new ServerOperated(outputStream);
        this.context = context;
    }

    public void done() throws IOException {
        Path realPath = context.getRequest().getRealPath();
        if (Files.isDirectory(realPath) || Files.notExists(realPath)) {
            if (!realPath.toFile().getName().contains(".")) {
                realPath = Paths.get(realPath.toAbsolutePath().toFile() + ".html");
            }
        }

        if (Files.exists(realPath)) {
            operated.opReadFile(realPath, context.getRequest());
        } else if (Files.notExists(realPath)) {
            throw new FileNotFoundException(realPath.toString());
        }
    }

    public ServerOperated getOperated() {
        return operated;
    }

    public ServerContext getContext() {
        return context;
    }
}
