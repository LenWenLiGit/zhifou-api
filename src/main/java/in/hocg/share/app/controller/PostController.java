package in.hocg.share.app.controller;

import in.hocg.share.app.controller.param.AddPostParam;
import in.hocg.share.app.service.IndexService;
import in.hocg.share.app.util.http.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hocgin on 2019/5/4.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {
    private final IndexService indexService;
    
    
    /**
     * 用户投稿
     *
     * @param param
     * @return
     */
    @PostMapping
    public ResponseEntity add(@Validated @RequestBody AddPostParam param) {
        indexService.addPost(param);
        return Result.success()
                .asResponseEntity();
    }
}
