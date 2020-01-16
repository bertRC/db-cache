package education.bert.service;

import education.bert.Cache;
import education.bert.CacheLinkedMapImpl;
import education.bert.model.PostModel;
import education.bert.model.UserModel;

/**
 * Service class that provides read/write data from/to the database using cache-api. This service implements a
 * simplified model of an internet forum or social network.
 */
public class CachedForumService extends ForumService {

    /**
     * Cache that stores string requests and objects received from the database.
     */
    private Cache<String, Object> cache;

    /**
     * Creates empty DB tables and new cache with default maximum cache size.
     */
    @Override
    public void setup() {
        cache = new CacheLinkedMapImpl<>();
        super.setup();
    }

    /**
     * Creates empty DB tables and new cache with specified maximum cache size.
     *
     * @param maxCacheSize the maximum cache size.
     */
    public void setup(int maxCacheSize) {
        cache = new CacheLinkedMapImpl<>(maxCacheSize);
        super.setup();
    }

    /**
     * Adds specified user to DB if {@code user.getId() == 0}, otherwise updates existing user. If successful,
     * invalidates the corresponding cache data.
     *
     * @param user user to be added or updated.
     * @return either (1) the same user with updated id or (2) {@code null} if there is no user to update in the
     * database.
     */
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

    /**
     * Returns user with specified id from cache, if possible. Otherwise returns user from DB and caches the result.
     *
     * @param id id by which user is to be returned.
     * @return user with specified id.
     */
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

    /**
     * Removes user with specified id from DB. If successful, invalidates the corresponding cache data.
     *
     * @param id id by which user is to be removed.
     * @return {@code true} if the user is successfully removed, otherwise {@code null}.
     */
    @Override
    public boolean removeUser(int id) {
        boolean removed = super.removeUser(id);
        if (removed) {
            cache.remove("getUsersCount()");
            cache.remove("getUser(" + id + ")");
        }
        return removed;
    }

    /**
     * Returns the total number of users in the database using cache.
     *
     * @return the total number of users in the database using cache.
     */
    @Override
    public int getUsersCount() {
        Integer result = (Integer) cache.get("getUsersCount()");
        if (result == null) {
            result = super.getUsersCount();
            cache.put("getUsersCount()", result);
        }
        return result;
    }

    /**
     * Adds specified post to DB if {@code post.getId() == 0}, otherwise updates existing post. If successful,
     * invalidates the corresponding cache data.
     *
     * @param post post to be added or updated.
     * @return either (1) the same post with updated id or (2) {@code null} if there is no post to update in the
     * database.
     */
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

    /**
     * Returns post with specified id from cache, if possible. Otherwise returns post from DB and caches the result.
     *
     * @param id id by which post is to be returned.
     * @return post with specified id.
     */
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

    /**
     * Removes post with specified id from DB. If successful, invalidates the corresponding cache data.
     *
     * @param id id by which post is to be removed.
     * @return {@code true} if the post is successfully removed, otherwise {@code null}.
     */
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

    /**
     * Returns the total number of posts in the database using cache.
     *
     * @return the total number of posts in the database using cache.
     */
    @Override
    public int getPostsCount() {
        Integer result = (Integer) cache.get("getPostsCount()");
        if (result == null) {
            result = super.getPostsCount();
            cache.put("getPostsCount()", result);
        }
        return result;
    }

    /**
     * Returns the number of posts for specified creator id using cache.
     *
     * @param creatorId creator id for which number of posts is to be returned.
     * @return the number of posts for specified creator id.
     */
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
