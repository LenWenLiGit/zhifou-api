package in.hocg.share.app.controller;

import in.hocg.share.app.config.security.NeedLogin;
import in.hocg.share.app.controller.param.PostDetailResponse;
import in.hocg.share.app.controller.param.PublishedPostParam;
import in.hocg.share.app.controller.param.SearchPostResponse;
import in.hocg.share.app.service.PostService;
import in.hocg.share.app.util.http.Result;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by hocgin on 2019/5/22.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@RestController
@RequestMapping("/api/v1/post")
@AllArgsConstructor
public class PostController {
    
    private final PostService service;
    
    
    /**
     * 发布
     *
     * @param param
     * @return
     */
    @PostMapping
    @NeedLogin
    public ResponseEntity published(@Validated @RequestBody PublishedPostParam param,
                                    Principal principal) {
        service.published(param, principal);
        return Result.success()
                .asResponseEntity();
    }
    
    /**
     * 检索
     *
     * @param pageable
     * @return
     */
    @PostMapping("_search")
    public ResponseEntity search(@PageableDefault(value = 8, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SearchPostResponse> result = service.search(pageable);
        return Result.success(result)
                .asResponseEntity();
    }
    
    
    /**
     * 内容
     *
     * @return
     */
    @GetMapping
    public ResponseEntity get(@RequestParam("v") String v) {
        PostDetailResponse result = service.getPostDetail(v);
        return Result.success(result)
                .asResponseEntity();
    }
}
