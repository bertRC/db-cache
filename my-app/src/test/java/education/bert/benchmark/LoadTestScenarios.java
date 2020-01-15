package education.bert.benchmark;

import education.bert.model.PostModel;
import education.bert.model.UserModel;
import education.bert.service.ForumService;
import org.openjdk.jmh.infra.Blackhole;

public class LoadTestScenarios {
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
        service.saveUser(new UserModel(0, "Alesha"));

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

    public static PostModel getSomePost(ForumService service) {
        return service.getPost(9);
    }

    public static void particularLoadTestScenario(ForumService service, Blackhole blackhole) {
        int usersCount = service.getUsersCount();
        int postsCount = service.getPostsCount();
        UserModel userOne = service.getUser(1);
        int postsCountForUserOne = service.getPostsCountForCreator(1);

        PostModel newPost = service.savePost(new PostModel(0, "Worst forum ever...", 10));

        PostModel readingNewPost = service.getPost(newPost.getId());
        UserModel userTwo = service.getUser(2);
        PostModel postTwo = service.getPost(2);
        int postsCountForUserSeven = service.getPostsCountForCreator(7);

        service.saveUser(new UserModel(0, "Guest"));

        int usersCountAgain = service.getUsersCount();
        PostModel postTen = service.getPost(10);
        UserModel userFour = service.getUser(4);
        PostModel postSix = service.getPost(6);

        blackhole.consume(usersCount);
        blackhole.consume(postsCount);
        blackhole.consume(userOne);
        blackhole.consume(postsCountForUserOne);
        blackhole.consume(newPost);
        blackhole.consume(readingNewPost);
        blackhole.consume(userTwo);
        blackhole.consume(postTwo);
        blackhole.consume(postsCountForUserSeven);
        blackhole.consume(usersCountAgain);
        blackhole.consume(postTen);
        blackhole.consume(userFour);
        blackhole.consume(postSix);
    }

    public static void randomLoadTestScenario(ForumService service, Blackhole blackhole) {
        int usersCount = service.getUsersCount();
        int postsCount = service.getPostsCount();
        int randomCase;
        int randomId;

        for (int i = 0; i < 10; i++) {
            randomCase = (int) (3 * Math.random());
            switch (randomCase) {
                case 0:
                    randomId = (int) (usersCount * Math.random() + 1);
                    blackhole.consume(service.getUser(randomId));
                    break;
                case 1:
                    randomId = (int) (postsCount * Math.random() + 1);
                    blackhole.consume(service.getPost(randomId));
                    break;
                case 2:
                    randomId = (int) (usersCount * Math.random() + 1);
                    blackhole.consume(service.getPostsCountForCreator(randomId));
                    break;
            }
        }

        randomCase = (int) (3 * Math.random());
        switch (randomCase) {
            case 0:
                service.saveUser(new UserModel(0, "new User"));
                break;
            case 1:
                randomId = (int) (usersCount * Math.random() + 1);
                service.saveUser(new UserModel(randomId, "renamed User"));
                break;
            case 2:
                randomId = (int) (usersCount * Math.random() + 1);
                service.removeUser(randomId);
                break;
        }

        randomCase = (int) (3 * Math.random());
        switch (randomCase) {
            case 0:
                randomId = (int) (usersCount * Math.random() + 1);
                service.savePost(new PostModel(0, "new Post", randomId));
                break;
            case 1:
                randomId = (int) (postsCount * Math.random() + 1);
                PostModel post = service.getPost(randomId);
                if (post != null) {
                    post.setPostName("changed Post");
                    service.savePost(post);
                }
                break;
            case 2:
                randomId = (int) (postsCount * Math.random() + 1);
                service.removePost(randomId);
                break;
        }
    }
}
