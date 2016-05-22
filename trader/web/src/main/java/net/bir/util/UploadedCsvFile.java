package net.bir.util;

/**
 * Created by Eugene on 22.05.2016.
 */
import java.io.Serializable;

public class UploadedCsvFile implements Serializable {
    private static final long serialVersionUID = -8192553629588066292L;
    private String name;
    private String mime;
    private long length;
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        int extDot = name.lastIndexOf('.');
        if (extDot > 0) {
            String extension = name.substring(extDot + 1);
            if ("csv".equals(extension)) {
                mime = "application/csv";
            } else if ("xls".equals(extension)) {
                mime = "application/xls";
            } else {
                mime = "image/unknown";
            }
        }
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMime() {
        return mime;
    }
}
