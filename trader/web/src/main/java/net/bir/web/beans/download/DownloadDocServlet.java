package net.bir.web.beans.download;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class DownloadDocServlet extends HttpServlet {
    private static final long serialVersionUID = 6564210631805526316L;



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {
        process(req, resp);
        //
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {
        process(req, resp);
    }
//
    private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {
        FileInputStream in = null;
        OutputStream out = null;
        resp.addDateHeader("Expires", new Date().getTime());
        resp.addHeader("Cache-Control", "no-cache, must-revalidate");
        resp.addHeader("Pragma", "no-cache");

        String ctype = (String) req.getSession().getAttribute("ContentType");
        Integer clen = (Integer) req.getSession().getAttribute("ContentLength");
        try {//
            // String docName = (String)
            // req.getSession().getAttribute("docName");
            String fileName = (String) req.getSession().getAttribute("fileName");

            if (fileName != null && fileName.trim().length() > 0) {
                resp.setContentType(ctype);
                resp.setContentLength(clen);
                resp.addHeader("Content-Disposition", "inline");
                // resp.addHeader("Content-Disposition", "attachment;filename="+
                // docName);
                System.out
                        .println("sending file: '" + fileName
                                + "' with content type: " + ctype
                                + ", length: " + clen);
                File file = new File(fileName);

                in = new FileInputStream(file);
                out = resp.getOutputStream();

                byte[] buf = new byte[1024];
                int count = 0;
                while ((count = in.read(buf)) >= 0) {
                    out.write(buf, 0, count);
                    out.flush();
                }
            }
        } catch (IOException e) {
            throw new ServletException(e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                }
        }
    }
}
