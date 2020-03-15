package person.winteryun.fileapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import person.winteryun.fileapi.entity.consts.ReturnConst;
import person.winteryun.fileapi.entity.resp.ListFileResp;
import person.winteryun.fileapi.entity.resp.UploadFileResp;
import person.winteryun.fileapi.service.FileService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "fileyun")
@Slf4j
public class RemoteFileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "file")
    public UploadFileResp postFile(@RequestParam String path, @RequestParam MultipartFile file) {
        UploadFileResp resp = new UploadFileResp(ReturnConst.SUCCESS, ReturnConst.SUCCESS_MSG);
        try {
            if (StringUtils.isEmpty(path)) {
                resp.setCode(ReturnConst.PARAM_EMPTY);
                resp.setMsg(ReturnConst.PARAM_EMPTY_MSG);
                return resp;
            }
            if (null == file) {
                resp.setCode(ReturnConst.PARAM_EMPTY);
                resp.setMsg(ReturnConst.PARAM_EMPTY_MSG);
                return resp;
            }

            String fileName = fileService.storeFile(path, file);
            resp.setFileName(fileName);
        } catch (Exception e) {
            log.error("上传文件异常" + file.getName(), e);
            resp.setCode(ReturnConst.UNKNOW_ERROR);
            resp.setMsg(ReturnConst.UNKNOW_ERROR_MSG);
        }

        return resp;
    }


    @GetMapping(value = "file/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable(value = "filename") String fileName,
                                            @RequestParam(value = "path", required = false) String path,
                                            HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileService.loadFile(path, fileName);

        try {
            if (null == resource) {
                return ResponseEntity.notFound().build();
            }

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.info("解析文件类型失败");
            }

            // Fallback to the default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("获取文件异常", e);
            return null;
        }

    }

    @GetMapping(value = "files")
    public ListFileResp listFile(@RequestParam(value = "path", required = false) String path,
                                 @RequestParam(value = "offset", required = false) Integer offset,
                                 @RequestParam(value = "limit", required = false) Integer limit) {


        ListFileResp resp = new ListFileResp(ReturnConst.SUCCESS, ReturnConst.SUCCESS_MSG);


        try {
            //检查参数默认值
            if (null == offset) {
                offset = 0;
            }
            if (null == limit || 0 == limit) {
                limit = 10;
            }

            //获取文件名称列表
            List<String> files = fileService.listFiles(path, offset, limit);

            if (null == files || files.isEmpty()) {
                resp.setCode(ReturnConst.NOFILE);
                resp.setMsg(ReturnConst.NOFILE_MSG);
            } else {
                resp.setFlies(files);

            }

        } catch (Exception e) {
            log.error("获取文件列表异常", e);
            resp.setCode(ReturnConst.UNKNOW_ERROR);
            resp.setMsg(ReturnConst.UNKNOW_ERROR_MSG);
        }

        return resp;


    }


}
