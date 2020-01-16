package education.bert.service;

import education.bert.Cache;
import education.bert.CacheLinkedMapImpl;
import education.bert.model.PostModel;
import education.bert.model.UserModel;

public class CachedForumService extends ForumService {
    private Cache<String, Object> cache;

    @Override
    public void setup() {
        cache = new CacheLinkedMapImpl<>();
        super.setup();
    }

    public void setup(int maxCacheSize) {
        cache = new CacheLinkedMapImpl<>(maxCacheSize);
        super.setup();
    }

    @Override
    public UserModel saveUser(UserModel user) {
        UserModel result = super.saveUser(user);
        if (result != null) {
            if (user.getId() == 0) {
                cache.remove("getUsersCount()");
            } else {
                cache.remove("getUser(" + user.getId() + ")");
            }
        }
        return result;
    }

    @Override
    public UserModel getUser(int id) {
        UserModel result = (UserModel) cache.get("getUser(" + id + ")");
        if (result == null) {
            result = super.getUser(id);
            if (result != null) {
                cache.put("getUser(" + id + ")", result);
            }
        }
        return result;
    }

    @Override
    public boolean removeUser(int id) {
        boolean removed = super.removeUser(id);
        if (removed) {
            cache.remove("getUsersCount()");
            cache.remove("getUser(" + id + ")");
        }
        return removed;
    }

    @Override
    public int getUsersCount() {
        Integer result = (Integer) cache.get("getUsersCount()");
        if (result == null) {
            result = super.getUsersCount();
            cache.put("getUsersCount()", result);
        }
        return result;
    }

    @Override
    public PostModel savePost(PostModel post) {
        PostModel result = super.savePost(post);
        if (result != null) {
            if (post.getId() == 0) {
                cache.remove("getPostsCount()");
                cache.remove("getPostsCountForCreator(" + post.getCreatorId() + ")");
            } else {
                cache.remove("getPost(" + post.getId() + ")");
            }
        }
        return result;
    }

    @Override
    public PostModel getPost(int id) {
        PostModel result = (PostModel) cache.get("getPost(" + id + ")");
        if (result == null) {
            result = super.getPost(id);
            if (result != null) {
                cache.put("getPost(" + id + ")", result);
            }
        }
        return result;
    }

    @Override
    public boolean removePost(int id) {
        PostModel post = getPost(id);
        if (post != null) {
            int creatorId = post.getCreatorId();
            boolean removed = super.removePost(id);
            if (removed) {
                cache.remove("getPostsCount()");
                cache.remove("getPost(" + id + ")");
                cache.remove("getPostsCountForCreator(" + creatorId + ")");
            }
            return removed;
        } else {
            return false;
        }
    }

    @Override
    public int getPostsCount() {
        Integer result = (Integer) cache.get("getPostsCount()");
        if (result == null) {
            result = super.getPostsCount();
            cache.put("getPostsCount()", result);
        }
        return result;
    }

    @Override
    public int getPostsCountForCreator(int creatorId) {
        Integer result = (Integer) cache.get("getPostsCountForCreator(" + creatorId + ")");
        if (result == null) {
            result = super.getPostsCountForCreator(creatorId);
            cache.put("getPostsCountForCreator(" + creatorId + ")", result);
        }
        return result;
    }
}
