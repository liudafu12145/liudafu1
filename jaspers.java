import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class jaspers extends HttpServletResponseWrapper {
    private final CharArrayWriter charWriter;
    private final PrintWriter writer;
    private final ByteArrayOutputStream byteStream;
    private ServletOutputStream outputStream;
    private boolean usingWriter;
    private boolean usingStream;

    public jaspers(HttpServletResponse response) {
        super(response);
        charWriter = new CharArrayWriter();
        writer = new PrintWriter(charWriter);
        byteStream = new ByteArrayOutputStream();
    }

    @Override
    public PrintWriter getWriter() {
        if (usingStream) {
            throw new IllegalStateException("getOutputStream() has already been called.");
        }
        usingWriter = true;
        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (usingWriter) {
            throw new IllegalStateException("getWriter() has already been called.");
        }
        usingStream = true;
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    byteStream.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {}
            };
        }
        return outputStream;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (usingWriter) {
            writer.flush();
        } else if (usingStream) {
            outputStream.flush();
        }
    }

    @Override
    public String toString() {
        return charWriter.toString();
    }
}
