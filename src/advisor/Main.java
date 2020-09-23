package advisor;

import advisor.auth.Authorization;
import advisor.auth.LocalServer;
import advisor.config.Params;
import advisor.controller.AdvisorController;
import advisor.model.AdvisorModel;
import advisor.view.AdvisorView;

import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String accessServer = Params.ACCESS_SERVER;
        String resourceServer = Params.RESOURCE_SERVER;
        boolean userAuth = false, exit = false, access = false, resource = false, page = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-access") && !access) {
                accessServer = args[i + 1];
                access = true;
                i++;
            }
            if (args[i].equals("-resource") && !resource) {
                resourceServer = args[i + 1];
                resource = true;
                i++;
            }
            if (args[i].equals("-page") && !page) {
                Params.RESULTS_PER_PAGE = Integer.parseInt(args[i + 1]);
                page = true;
                i++;
            }
        }

        AdvisorController advisorController = new AdvisorController(resourceServer);

        while (!exit) {
            String[] input = sc.nextLine().split("\\s+");
            if (!userAuth) {
                if (input[0].equals("auth")) {
                    LocalServer localServer = new LocalServer(accessServer);
                    localServer.startServer();
                    localServer.getCode();
                    localServer.stopServer();
                    Authorization authorization = new Authorization(accessServer);
                    authorization.getToken();
                    userAuth = true;
                    AdvisorView.printMessage(Params.SUCCESS);
                } else {
                    AdvisorView.printMessage(Params.ANSWER_DENIED_ACCESS);
                }
            } else {
                switch (input[0]) {
                    case "featured":
                        AdvisorView.print(advisorController.getFeaturedPlaylists());
                        break;
                    case "new":
                        AdvisorView.print(advisorController.getNewReleases());
                        break;
                    case "categories":
                        AdvisorView.print(advisorController.getCategories());
                        break;
                    case "playlists":
                        StringJoiner sj = new StringJoiner(" ");
                        for (int i = 1; i < input.length; i++) {
                            sj.add(input[i]);
                        }
                        String categoryName = sj.toString();
                        List<AdvisorModel> categoryPlaylists = advisorController.getCategoryPlaylists(categoryName);
                        if (categoryPlaylists != null) {
                            AdvisorView.print(categoryPlaylists);
                        } else {
                            AdvisorView.printMessage(Params.UNKNOWN_CATEGORY_NAME);
                        }
                        break;
                    case "next":
                        AdvisorView.printNextPage();
                        break;
                    case "prev":
                        AdvisorView.printPrevPage();
                        break;
                    case "exit":
                        AdvisorView.printMessage(Params.GOODBYE);
                        exit = true;
                        break;
                    default:
                        AdvisorView.printMessage(Params.INCORRECT_COMMAND);
                }
            }
        }
    }
}