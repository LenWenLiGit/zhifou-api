package in.hocg.share.app.service;

import com.google.common.collect.Sets;
import in.hocg.share.app.controller.param.PostDetailResponse;
import in.hocg.share.app.controller.param.PublishedPostParam;
import in.hocg.share.app.controller.param.SearchPostResponse;
import in.hocg.share.app.controller.param.lang.UserResponse;
import in.hocg.share.app.entity.Classify;
import in.hocg.share.app.entity.Post;
import in.hocg.share.app.entity.User;
import in.hocg.share.app.repository.PostRepository;
import in.hocg.share.app.support.BaseService;
import in.hocg.share.app.support.redis.RedisService;
import in.hocg.share.app.util.ApiException;
import in.hocg.share.app.util.Vid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

/**
 * Created by hocgin on 2019/5/22.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Service
@AllArgsConstructor
public class PostServiceImpl extends BaseService<Post, PostRepository>
        implements PostService {
    private final UserService userService;
    private final ClassifyService classifyService;
    private final RedisService redisService;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void published(PublishedPostParam param, Principal principal) {
        String username = principal.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        userOptional.orElseThrow(() -> new ApiException("请先进行登陆"));
        User user = userOptional.get();
        
        Post post = param.asPost();
        post.setAuthorId(user.getId());
        repository.save(post);
    }
    
    @Override
    public Page<SearchPostResponse> search(Pageable pageable) {
        Page<SearchPostResponse> result = repository.findAll(pageable)
                .map((post) -> {
                    SearchPostResponse response = new SearchPostResponse();
                    BeanUtils.copyProperties(post, response);
                    
                    // 作者
                    Optional<User> authorOptional = userService.findById(post.getAuthorId());
                    authorOptional.ifPresent(user -> response.setAuthor(new UserResponse(user)));
                    
                    // 浏览量
                    response.setPageviews(redisService.getPageviewsCount(String.valueOf(post.getId())));
                    
                    // 分类
                    Optional<Classify> classifyOptional = classifyService.findById(post.getClassifyId());
                    classifyOptional.ifPresent(classify -> response.setClassify(classify.getName()));
                    
                    // 文章简介
                    response.setDesc(post.getContent().substring(0, 255));
                    
                    response.setV(Vid.encode(post.getId()));
                    
                    response.setTags(Sets.newHashSet(post.getTags().split(",")));
                    response.setBanner(Sets.newHashSet(post.getBanner().split(",")));
                    
                    return response;
                });
        
        return result;
    }
    
    @Override
    public PostDetailResponse getPostDetail(String v) {
        Long id = Vid.decode(v);
        Optional<Post> postOptional = repository.findById(id);
        if (!postOptional.isPresent()) {
            throw new ApiException("文章不存在");
        }
        
        Post post = postOptional.get();
        PostDetailResponse result = new PostDetailResponse();
        
        BeanUtils.copyProperties(post, result);
        
        // 作者
        Optional<User> authorOptional = userService.findById(post.getAuthorId());
        authorOptional.ifPresent(user -> result.setAuthor(new UserResponse(user)));
        
        // 浏览量
        result.setPageviews(redisService.getPageviewsCount(String.valueOf(post.getId())));
        
        // 分类
        Optional<Classify> classifyOptional = classifyService.findById(post.getClassifyId());
        classifyOptional.ifPresent(classify -> result.setClassify(classify.getName()));
        
        result.setTags(Sets.newHashSet(post.getTags().split(",")));
        result.setBanner(Sets.newHashSet(post.getBanner().split(",")));
        
        return result;
    }
}
