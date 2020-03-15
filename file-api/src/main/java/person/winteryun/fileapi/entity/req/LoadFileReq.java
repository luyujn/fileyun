package person.winteryun.fileapi.entity.req;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoadFileReq {
    private String path;

    private String version;


}
