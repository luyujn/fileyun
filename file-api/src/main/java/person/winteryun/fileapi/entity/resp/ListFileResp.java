package person.winteryun.fileapi.entity.resp;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListFileResp extends BaseResp{
    private List<String> flies;

    public ListFileResp(String code, String msg) {
        super(code, msg);
    }
}
