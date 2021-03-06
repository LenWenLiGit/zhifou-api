package in.hocg.zhifou.service;

import in.hocg.zhifou.controller.param.CommentParam;
import in.hocg.zhifou.controller.param.CommentResponse;
import in.hocg.zhifou.controller.param.lang.UserResponse;
import in.hocg.zhifou.entity.Comment;
import in.hocg.zhifou.entity.User;
import in.hocg.zhifou.repository.CommentRepository;
import in.hocg.zhifou.support.BaseService;
import in.hocg.zhifou.util.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by hocgin on 2019/5/14.
 * email: hocgin@gmail.com
 *
 * @author hocgin
 */
@Service
@AllArgsConstructor
public class CommentServiceImpl extends BaseService<Comment, CommentRepository>
        implements CommentService {
    
    private final UserService userService;
    
    @Override
    public void comment(Principal principal,
                        String targetId,
                        CommentParam param) {
        
        String username = principal.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        userOptional.orElseThrow(() -> new ApiException("请先进行登陆"));
        
        User user = userOptional.get();
        Comment comment = param.asComment();
        comment.setTargetId(targetId);
        comment.setUserId(user.getId());
        repository.save(comment);
    }
    
    @Override
    public Page<CommentResponse> queryRootComment(String targetId,
                                                  Pageable pageable) {
        
        Page<Comment> comments = repository.findAllByTargetIdAndRootId(targetId, null, pageable);
        CommentResponse body;
        ArrayList<CommentResponse> result = new ArrayList<>(comments.getContent().size());
        Optional<User> userOptional;
        
        // 填充信息
        for (Comment comment : comments.getContent()) {
            userOptional = userService.findById(comment.getUserId());
            if (!userOptional.isPresent()) {
                continue;
            }
            body = new CommentResponse(comment);
            body.setCommenter(new UserResponse(userOptional.get()));
            
            body.setCommentCount(repository.countAllByRootId(comment.getId()));
            result.add(body);
        }
        
        return new PageImpl<>(result, pageable, comments.getTotalElements());
    }
    
    
    @Override
    public Page<CommentResponse> queryChildrenComment(String targetId,
                                                      Long rootId,
                                                      Pageable pageable) {
        
        
        Page<Comment> comments = repository.findAllByTargetIdAndRootId(targetId, rootId, pageable);
        
        CommentResponse body;
        ArrayList<CommentResponse> result = new ArrayList<>(comments.getContent().size());
        Optional<User> userOptional;
        
        // 填充信息
        for (Comment comment : comments.getContent()) {
            userOptional = userService.findById(comment.getUserId());
            if (!userOptional.isPresent()) {
                continue;
            }
            
            body = new CommentResponse(comment);
            body.setCommenter(new UserResponse(userOptional.get()));
            
            // 填充父评论
            Long parentId = comment.getParentId();
            if (!Objects.equals(parentId, rootId)) {
                Optional<Comment> pCommentOptional = repository.findById(parentId);
                if (pCommentOptional.isPresent()) {
                    Comment pComment = pCommentOptional.get();
                    Optional<User> pCommenter = userService.findById(pComment.getUserId());
                    if (pCommenter.isPresent()) {
                        body.setPCommenter(new UserResponse(pCommenter.get()));
                    }
                }
            }
            
            // 查询子评论
            result.add(body);
        }
        
        return new PageImpl<>(result, pageable, comments.getTotalElements());
    }
    
    
}
