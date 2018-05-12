package cn.piumnl.mdlib.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

/**
 * @author piumnl
 * @version 1.0.0
 * @since on 2018-05-12.
 */
public class ServerContext implements Runnable {

    private ServerRequest request;

    private ServerResponse response;

    public ServerContext(Socket socket) throws IOException {
        this.request = new ServerRequest(socket.getInputStream(), this);
        this.response = new ServerResponse(socket.getOutputStream(), this);
    }

    public ServerRequest getRequest() {
        return request;
    }

    public ServerResponse getResponse() {
        return response;
    }

    @Override
    public void run() {
        ServerOperated operated = response.getOperated();
        try {
            request.init();
            response.done();
        } catch (FileNotFoundException e) {
            operated.opNotFound(getRequest().getResourcePath());
        } catch (Exception e) {
            operated.opError(e);
        } finally {
            operated.close();
        }
    }
}
