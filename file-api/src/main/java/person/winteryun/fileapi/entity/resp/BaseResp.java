package person.winteryun.fileapi.entity.resp;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResp implements Serializable {

    private String code;
    private String msg;

}

