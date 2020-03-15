package person.winteryun.fileapi.entity.resp;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResp extends BaseResp{
    private String fileName;

    public UploadFileResp(String code, String msg) {
        super(code, msg);
    }
}
