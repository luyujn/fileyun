package person.fileyun.filecore.po;

import lombok.Data;

import java.io.Serializable;


@Data
public class BaseFile implements Serializable {
    private String fileName;

    private String filePath;

}
