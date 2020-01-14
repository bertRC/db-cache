package education.bert.benchmark.action;

import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.service.ForumService;

public class Actions {
    public static void addSomeInitialData(ForumService service) {
        service.saveUser(new UserModel(0, "Vasya"));
        service.saveUser(new UserModel(0, "Petya"));
        service.saveUser(new UserModel(0, "Ivan"));
        service.saveUser(new UserModel(0, "Masha"));
        service.saveUser(new UserModel(0, "Sasha"));
        service.saveUser(new UserModel(0, "Dasha"));
        service.saveUser(new UserModel(0, "Bert"));
        service.saveUser(new UserModel(0, "Kolya"));
        service.saveUser(new UserModel(0, "Styopa"));
        service.saveUser(new UserModel(0, "Ira"));

        service.savePost(new PostModel(0, "Hello Friends", 1));
        service.savePost(new PostModel(0, "Forum Rules", 2));
        service.savePost(new PostModel(0, "General Discussion", 1));
        service.savePost(new PostModel(0, "FAQ", 2));
        service.savePost(new PostModel(0, "News", 1));
        service.savePost(new PostModel(0, "Searching Teammates", 7));
        service.savePost(new PostModel(0, "Please Help Me", 4));
        service.savePost(new PostModel(0, "Technical Issues", 1));
        service.savePost(new PostModel(0, "Guides", 2));
        service.savePost(new PostModel(0, "Happy New Year", 7));
    }

//    public static void doSomeInitialQueries(ForumService service) {
//        service.getUsersCount();
//        service.getPostsCount();
//        service.getPostsCountForCreator(2);
//        service.getUser(3);
//        service.getPost(6);
//    }

    public static void addSomeNewPost(ForumService service) {
        service.savePost(new PostModel(0, "Wishes and Suggestions", 2));
    }

    public static PostModel getSomePost(ForumService service) {
        return service.getPost(9);
    }
}
