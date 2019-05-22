package in.hocg.share.app.controller;

import in.hocg.share.app.controller.param.BannerResponse;
import in.hocg.share.app.service.BannerService;
import in.hocg.share.app.util.http.Result;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by hocgin on 2019/5/22.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@RestController
@RequestMapping("/api/v1/banner")
@AllArgsConstructor
public class BannerController {
    private final BannerService service;
    
    @GetMapping
    public ResponseEntity all() {
        List<BannerResponse> result = service.getAll();
        return Result.success(result)
                .asResponseEntity();
    }
}
